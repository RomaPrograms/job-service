package com.duallab.verapdf.jobservice.model.entity;

import com.duallab.verapdf.jobservice.model.entity.enums.JobError;
import com.duallab.verapdf.jobservice.model.entity.enums.JobStatus;
import com.duallab.verapdf.jobservice.model.entity.enums.Profile;

import javax.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author Maksim Bezrukov
 */
@Entity
@Table(name = "jobs")
public class Job {

	@Id
	@GeneratedValue
	@Column(name = "id")
	private UUID id;

	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false)
	private JobStatus status;
	@Column(name = "created_at", nullable = false)
	private Instant createdAt;
	@Column(name = "finished_at")
	private Instant finishedAt;
	@Column(name = "last_used", nullable = false)
	private Instant lastUsed;
	@Enumerated(EnumType.STRING)
	@Column(name = "error_type")
	private JobError errorType;
	@Column(name = "exception_message")
	private String exceptionMessage;
	@Enumerated(EnumType.STRING)
	@Column(name = "profile", nullable = false)
	private Profile profile;

	@OneToMany(mappedBy = "sourceFor", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<StoredFile> sourceFiles = new ArrayList<>();
	@OneToOne(mappedBy = "resultFor", cascade = CascadeType.ALL, orphanRemoval = true)
	private StoredFile resultFile;

	public Job() {
		this.createdAt = Instant.now();
		this.lastUsed = Instant.now();
		this.status = JobStatus.PROCESSING;
	}

	public Job(Profile profile) {
		this.createdAt = Instant.now();
		this.lastUsed = Instant.now();
		this.status = JobStatus.PROCESSING;
		this.profile = profile;
	}

	public UUID getId() {
		return id;
	}

	public JobStatus getStatus() {
		return status;
	}

	public StoredFile getResultFile() {
		return resultFile;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}

	public Instant getFinishedAt() {
		return finishedAt;
	}

	public Instant getLastUsed() {
		return lastUsed;
	}

	public JobError getErrorType() {
		return errorType;
	}

	public String getExceptionMessage() {
		return exceptionMessage;
	}

	public Profile getProfile() {
		return profile;
	}

	public List<StoredFile> getSourceFiles() {
		return sourceFiles;
	}

	public void addSourceFile(StoredFile file) {
		file.setSourceFor(this);
		this.sourceFiles.add(file);
	}

	public void finished(StoredFile result) {
		jobEnd(JobStatus.FINISHED);
		this.resultFile = result;
		this.resultFile.setResultFor(this);
	}

	public void error(JobError errorType, String exceptionMessage) {
		jobEnd(JobStatus.ERROR);
		this.errorType = errorType;
		this.exceptionMessage = exceptionMessage;
	}

	public void updateLastUsed() {
		this.lastUsed = Instant.now();
	}

	private void jobEnd(JobStatus status) {
		assert this.status == JobStatus.PROCESSING;

		this.status = status;
		this.finishedAt = Instant.now();
		this.sourceFiles.clear();
		updateLastUsed();
	}
}
