package com.duallab.verapdf.jobservice.server.service;

import com.duallab.verapdf.jobservice.model.dto.StoredFileDTO;
import com.duallab.verapdf.jobservice.model.entity.StoredFile;
import com.duallab.verapdf.jobservice.model.error.exception.BadRequestException;
import com.duallab.verapdf.jobservice.model.error.exception.LowDiskSpaceException;
import com.duallab.verapdf.jobservice.model.error.exception.VeraPDFBackEndException;
import com.duallab.verapdf.jobservice.server.tools.FilesTool;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.unit.DataSize;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Maksim Bezrukov
 */
@Service
public class LocalFileService {
	private static final Logger LOGGER = LoggerFactory.getLogger(LocalFileService.class);

	private final File fileBaseDir;
	private DataSize minSpaceThreshold;
	private int baseDirLifetime;

	public LocalFileService(@Value("${verapdf.files.base-dir}") String baseDirPath,
	                        @Value("${verapdf.files.min-space-threshold}") DataSize minSpaceThreshold,
	                        @Value("${verapdf.cleaning.disc.file-lifetime-days}") int baseDirLifetime) throws IOException {
		this.fileBaseDir = new File(baseDirPath);
		this.minSpaceThreshold = minSpaceThreshold;
		this.baseDirLifetime = baseDirLifetime;
		if (!this.fileBaseDir.isDirectory()) {
			LOGGER.warn("Missing file directory. Trying to create with path: " + this.fileBaseDir.getAbsolutePath());
			FileUtils.forceMkdir(this.fileBaseDir);
		}
	}

	@Scheduled(cron = "${verapdf.cleaning.disc.cron}")
	public void clearDirectories() {
		LocalDate now = LocalDate.now();
		for (File dir : fileBaseDir.listFiles()) {
			// at root level only dirs with date names are allowed
			boolean shouldBeRemoved = dir.isFile();
			if (!shouldBeRemoved) {
				try {
					LocalDate dirNameDate = LocalDate.parse(dir.getName());
					long period = ChronoUnit.DAYS.between(dirNameDate, now);
					shouldBeRemoved = Math.abs(period) >= baseDirLifetime;
				} catch (DateTimeException e) {
					// at root level only dirs with date names are allowed
					shouldBeRemoved = true;
				}
			}
			if (shouldBeRemoved) {
				try {
					FilesTool.deleteFile(dir);
				} catch (IOException e) {
					LOGGER.error("Exception during file remove: " + dir.getAbsolutePath(), e);
				}
			}
		}
	}

	public List<StoredFile> saveSourceFiles(List<MultipartFile> files, List<StoredFileDTO> sourceFilesDTOs)
			throws VeraPDFBackEndException, IOException {
		// for both mapping files by names we need to provide merge operator
		// as there might be duplicate file names
		// This case then will be checked inside the if statement
		// together with the same amount for files and dtos check
		Map<String, MultipartFile> filesMap = files.stream()
		                                           .collect(Collectors.toMap(MultipartFile::getOriginalFilename,
		                                                                     Function.identity(),
		                                                                     (first, second) -> first));
		Map<String, StoredFileDTO> dtosMap = sourceFilesDTOs.stream()
		                                                    .collect(Collectors.toMap(StoredFileDTO::getFileName,
		                                                                              Function.identity(),
		                                                                              (first, second) -> first));
		if (filesMap.size() != dtosMap.size() || filesMap.size() != files.size()) {
			throw new BadRequestException("Job data does not match obtained files");
		}

		List<StoredFile> savedFiles = new ArrayList<>();
		try {
			for (Map.Entry<String, MultipartFile> entry : filesMap.entrySet()) {
				StoredFileDTO dto = dtosMap.get(entry.getKey());
				if (dto == null) {
					throw new BadRequestException("Job data does not match obtained files");
				}
				StoredFile source = this.saveSourceFile(entry.getValue(), dto);
				savedFiles.add(source);
			}
		} catch (Throwable e) {
			savedFiles.forEach(this::deleteFileFromDisk);
			throw e;
		}
		return savedFiles;
	}

	public StoredFile saveResultFile(InputStream is, String contentType, StoredFileDTO fileDTO) throws IOException, VeraPDFBackEndException {
		return saveFile(is, contentType, null, fileDTO);
	}

	public Resource getFileResource(StoredFile storedFile) throws FileNotFoundException {
		return new FileSystemResource(getFileOnDiskByPath(storedFile.getFilePath()));
	}

	public void deleteFileFromDisk(StoredFile storedFile) {
		try {
			if (storedFile != null) {
				File onDisk = getFileOnDiskByPath(storedFile.getFilePath());
				FilesTool.deleteFile(onDisk);
			}
		} catch (FileNotFoundException e) {
			// do nothing as file is already missing
		} catch (IOException e) {
			LOGGER.error("Exception during stored file delete: " + storedFile.getId(), e);
		}
	}

	private StoredFile saveSourceFile(MultipartFile file, StoredFileDTO fileDTO) throws VeraPDFBackEndException, IOException {
		checkNewFileAvailability();
		try (InputStream is = file.getInputStream()) {
			return saveFile(is, file.getContentType(), file.getOriginalFilename(), fileDTO);
		}
	}

	private File getFileOnDiskByPath(String filePath) throws FileNotFoundException {
		File res = new File(this.fileBaseDir, filePath);
		if (!res.isFile()) {
			throw new FileNotFoundException("Cannot find file on disk");
		}
		return res;
	}

	private StoredFile saveFile(InputStream is, String contentType, String originalFileName,
	                            StoredFileDTO fileDTO) throws VeraPDFBackEndException, IOException {
		File savedFile = FilesTool.saveFileOnDiskAndCheck(is, contentType, originalFileName, fileDTO,
		                                                  getDirToSaveFile(), "itext-web-demo-");
		String filePath = this.fileBaseDir.toPath().relativize(savedFile.toPath()).toString();
		return new StoredFile(filePath,
		                      fileDTO.getChecksum(), fileDTO.getFileType(),
		                      fileDTO.getFileSize(), fileDTO.getFileName());
	}

	private File getDirToSaveFile() {
		File res = new File(this.fileBaseDir, LocalDate.now().toString());
		if (!res.isDirectory() && !res.mkdir()) {
			throw new IllegalStateException("Cannot obtain the directory to save a file");
		}
		return res;
	}

	private void checkNewFileAvailability() throws LowDiskSpaceException {
		if (fileBaseDir.getUsableSpace() < minSpaceThreshold.toBytes()) {
			throw new LowDiskSpaceException("Low disk space");
		}
	}
}
