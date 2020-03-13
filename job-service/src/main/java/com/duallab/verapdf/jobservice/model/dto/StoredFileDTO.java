package com.duallab.verapdf.jobservice.model.dto;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;
import java.util.Objects;
import java.util.UUID;

/**
 * @author Maksim Bezrukov
 */
public class StoredFileDTO {
	private UUID id;
	@NotNull
	@Pattern(regexp = "^[\\dA-Fa-f]{32}$")
	private String checksum;
	@NotEmpty
	private String fileType;
	@Positive
	private long fileSize;
	@NotEmpty
	private String fileName;

	public StoredFileDTO() {
	}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public String getChecksum() {
		return checksum;
	}

	public void setChecksum(String checksum) {
		this.checksum = checksum;
	}

	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	public long getFileSize() {
		return fileSize;
	}

	public void setFileSize(long fileSize) {
		this.fileSize = fileSize;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof StoredFileDTO)) {
			return false;
		}
		StoredFileDTO that = (StoredFileDTO) o;
		return fileSize == that.fileSize &&
		       Objects.equals(id, that.id) &&
		       Objects.equals(checksum, that.checksum) &&
		       Objects.equals(fileType, that.fileType) &&
		       Objects.equals(fileName, that.fileName);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, checksum, fileType, fileSize, fileName);
	}

	@Override
	public String toString() {
		return "StoredFileDTO{" +
		       "id=" + id +
		       ", checksum='" + checksum + '\'' +
		       ", fileType='" + fileType + '\'' +
		       ", fileSize=" + fileSize +
		       ", fileName='" + fileName + '\'' +
		       '}';
	}
}
