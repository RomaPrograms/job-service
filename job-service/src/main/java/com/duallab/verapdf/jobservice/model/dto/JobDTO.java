package com.duallab.verapdf.jobservice.model.dto;

import com.duallab.verapdf.jobservice.model.entity.enums.JobError;
import com.duallab.verapdf.jobservice.model.entity.enums.JobStatus;
import com.duallab.verapdf.jobservice.model.entity.enums.Profile;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * @author Maksim Bezrukov
 */
public class JobDTO {

	private UUID id;
	private JobStatus status;
	private JobError errorType;
	private StoredFileDTO resultFile;
	@NotNull
	@Size(min = 1)
	private List<@Valid StoredFileDTO> sourceFiles;
	private Profile profile;

	public JobDTO() {
	}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public JobStatus getStatus() {
		return status;
	}

	public void setStatus(JobStatus status) {
		this.status = status;
	}

	public JobError getErrorType() {
		return errorType;
	}

	public void setErrorType(JobError errorType) {
		this.errorType = errorType;
	}

	public StoredFileDTO getResultFile() {
		return resultFile;
	}

	public void setResultFile(StoredFileDTO resultFile) {
		this.resultFile = resultFile;
	}

	public List<StoredFileDTO> getSourceFiles() {
		return sourceFiles;
	}

	public void setSourceFiles(List<StoredFileDTO> sourceFiles) {
		this.sourceFiles = sourceFiles;
	}

	public Profile getProfile() {
		return profile;
	}

	public void setProfile(Profile profile) {
		if(profile == null) {
			this.profile = Profile.TAGGED_PDF_1_7;
		}
		this.profile = profile;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof JobDTO)) {
			return false;
		}
		JobDTO jobDTO = (JobDTO) o;
		return Objects.equals(id, jobDTO.id) &&
		       status == jobDTO.status &&
		       errorType == jobDTO.errorType &&
		       Objects.equals(resultFile, jobDTO.resultFile) &&
		       Objects.equals(sourceFiles, jobDTO.sourceFiles) &&
		       profile == jobDTO.profile;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, status, errorType, resultFile, sourceFiles, profile);
	}

	@Override
	public String toString() {
		return "JobDTO{" +
		       "id=" + id +
		       ", status=" + status +
		       ", errorType=" + errorType +
		       ", resultFile=" + resultFile +
		       ", sourceFiles=" + sourceFiles +
		       ", profile=" + profile +
		       '}';
	}
}
