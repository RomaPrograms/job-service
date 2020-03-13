package com.duallab.verapdf.jobservice.server.tools;

import com.duallab.verapdf.jobservice.model.dto.StoredFileDTO;
import com.duallab.verapdf.jobservice.model.error.exception.BadRequestException;
import com.duallab.verapdf.jobservice.model.error.exception.VeraPDFBackEndException;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.DigestUtils;

import java.io.*;
import java.util.Objects;

/**
 * @author Maksim Bezrukov
 */
public class FilesTool {
	private static final Logger LOGGER = LoggerFactory.getLogger(FilesTool.class);

	public static final String MIME_TYPE_PDF = "application/pdf";
	public static final String MIME_TYPE_ZIP = "application/zip";
	public static final String MIME_TYPE_XML = "application/xml";
	public static final String MIME_TYPE_JSON = "application/json";

	private FilesTool() {
	}

	public static String getExtensionByContentType(String contentType) {
		switch (contentType) {
			case MIME_TYPE_PDF:
				return ".pdf";
			case MIME_TYPE_ZIP:
				return ".zip";
			case MIME_TYPE_XML:
				return ".xml";
			case MIME_TYPE_JSON:
				return ".json";
			default:
				return ".unknown";
		}
	}

	public static String evaluateMD5(File file) throws IOException {
		try (FileInputStream is = new FileInputStream(file)) {
			return DigestUtils.md5DigestAsHex(is);
		}
	}

	public static File saveFileOnDiskAndCheck(InputStream fileStream, String actualContentType, String actualName,
                                              StoredFileDTO expectedFileData, File dirToSave, String prefix)
			throws VeraPDFBackEndException, IOException {
		if (!(Objects.equals(actualContentType, expectedFileData.getFileType())
		      && Objects.equals(actualName, expectedFileData.getFileName()))) {
			throw new BadRequestException("File data object doesn't corresponds to obtained file");
		}
		File savedFile = null;
		try {
			String suffix = FilesTool.getExtensionByContentType(actualContentType);
			savedFile = File.createTempFile(prefix, suffix, dirToSave);
			try (FileOutputStream out = new FileOutputStream(savedFile)) {
				IOUtils.copyLarge(fileStream, out);
			}
			String resultChecksum = FilesTool.evaluateMD5(savedFile);
			if (savedFile.length() != expectedFileData.getFileSize()
			    || !resultChecksum.equals(expectedFileData.getChecksum())) {
				throw new VeraPDFBackEndException("Save file size or checksum does not match provided values. " +
				                                  "Actual: " + savedFile.length() + ", " + resultChecksum + ". " +
				                                  "Expected: " + expectedFileData.getFileSize() + ", " +
				                                  expectedFileData.getChecksum());
			}
			return savedFile;
		} catch (Throwable e) {
			deleteFileSilently(savedFile);
			throw e;
		}
	}

	public static void deleteFile(File toDelete) throws IOException {
		if (toDelete != null && toDelete.exists()) {
			FileUtils.forceDelete(toDelete);
		}
	}

	public static void deleteFileSilently(File toDelete) {
		try {
			deleteFile(toDelete);
		} catch (IOException e) {
			LOGGER.error("Exception during file remove: " + toDelete.getAbsolutePath(), e);
		}
	}
}
