package com.duallab.verapdf.jobservice.model.entity;

import javax.persistence.*;
import java.time.Instant;
import java.util.UUID;

/**
 * @author Maksim Bezrukov
 */
@Entity
@Table(name = "files")
public class StoredFile {

	@Id
	@GeneratedValue
	@Column(name = "id")
	private UUID id;

	@Column(name = "file_path", nullable = false)
	private String filePath;
	@Column(name = "checksum", nullable = false)
	private String checksum;
	@Column(name = "file_type", nullable = false)
	private String fileType;
	@Column(name = "file_size", nullable = false)
	private long fileSize;
	@Column(name = "file_orig_name")
	private String originalName;
	@Column(name = "created_at", nullable = false)
	private Instant createdAt;

	@ManyToOne
	@JoinColumn(name = "source_for_job")
	private Job sourceFor;
	@OneToOne
	@JoinColumn(name = "result_for_job", unique = true)
	private Job resultFor;

	public StoredFile() {
		this.createdAt = Instant.now();
	}

	public StoredFile(String filePath, String checksum, String fileType, long fileSize) {
		this(filePath, checksum, fileType, fileSize, null);
	}

	public StoredFile(String filePath, String checksum, String fileType, long fileSize, String originalName) {
		this();
		this.filePath = filePath;
		this.checksum = checksum;
		this.fileType = fileType;
		this.fileSize = fileSize;
		this.originalName = originalName;
	}

	public UUID getId() {
		return id;
	}

	public String getFilePath() {
		return filePath;
	}

	public String getChecksum() {
		return checksum;
	}

	public String getFileType() {
		return fileType;
	}

	public long getFileSize() {
		return fileSize;
	}

	public String getOriginalName() {
		return originalName;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}

	public Job getSourceFor() {
		return sourceFor;
	}

	public Job getResultFor() {
		return resultFor;
	}

	void setSourceFor(Job sourceFor) {
		this.sourceFor = sourceFor;
	}

	void setResultFor(Job resultFor) {
		this.resultFor = resultFor;
	}
}
