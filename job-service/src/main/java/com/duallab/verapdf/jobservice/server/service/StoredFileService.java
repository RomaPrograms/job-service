package com.duallab.verapdf.jobservice.server.service;

import com.duallab.verapdf.jobservice.model.entity.StoredFile;
import com.duallab.verapdf.jobservice.model.error.exception.NotFoundException;
import com.duallab.verapdf.jobservice.server.repository.StoredFileRepository;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.UUID;

/**
 * @author Maksim Bezrukov
 */
@Service
public class StoredFileService {

	private final StoredFileRepository storedFileRepository;
	private final LocalFileService localFileService;

	public StoredFileService(StoredFileRepository storedFileRepository,
	                         LocalFileService localFileService) {
		this.storedFileRepository = storedFileRepository;
		this.localFileService = localFileService;
	}

	@Transactional
	public void clearFilesWithoutJobReference() {
		List<StoredFile> storedFiles;
		do {
			storedFiles = storedFileRepository.findTop100BySourceForIsNullAndResultForIsNull();
			storedFiles.forEach(localFileService::deleteFileFromDisk);
			storedFileRepository.deleteAll(storedFiles);
		} while (!storedFiles.isEmpty());
	}

	@Transactional
	public Resource getFileById(UUID fileId) throws NotFoundException, FileNotFoundException {
		StoredFile result = findStoredFileById(fileId);
		return localFileService.getFileResource(result);
	}

	private StoredFile findStoredFileById(UUID fileId) throws NotFoundException {
		return storedFileRepository.findById(fileId)
		                           .orElseThrow(() -> new NotFoundException("File with specified id not fount in DB: " + fileId));
	}
}
