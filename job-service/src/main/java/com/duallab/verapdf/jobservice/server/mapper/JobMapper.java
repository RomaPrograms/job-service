package com.duallab.verapdf.jobservice.server.mapper;

import com.duallab.verapdf.jobservice.model.dto.JobDTO;
import com.duallab.verapdf.jobservice.model.entity.Job;
import com.duallab.verapdf.jobservice.model.entity.StoredFile;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Maksim Bezrukov
 */
@Component
public class JobMapper {

	private final StoredFileMapper storedFileMapper;

	public JobMapper(StoredFileMapper storedFileMapper) {
		this.storedFileMapper = storedFileMapper;
	}

	public JobDTO createDTOFromEntity(Job job) {
		JobDTO res = new JobDTO();
		res.setId(job.getId());
		res.setStatus(job.getStatus());
		res.setErrorType(job.getErrorType());
		StoredFile resultFile = job.getResultFile();
		if (resultFile != null) {
			res.setResultFile(storedFileMapper.createDTOFromEntity(resultFile));
		}
		List<StoredFile> sourceFiles = job.getSourceFiles();
		if (sourceFiles != null && !sourceFiles.isEmpty()) {
			res.setSourceFiles(sourceFiles.stream()
			                              .map(storedFileMapper::createDTOFromEntity)
			                              .collect(Collectors.toList()));
		}
		res.setProfile(job.getProfile());
		return res;
	}
}
