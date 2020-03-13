package com.duallab.verapdf.jobservice.model.dto;

import java.util.Objects;

/**
 * @author Maksim Bezrukov
 */
public class SettingsDTO {

	private long fileMaxSizeBytes;
	private long requestMaxSizeBytes;

	public SettingsDTO() {
	}

	public SettingsDTO(long fileMaxSizeBytes, long requestMaxSizeBytes) {
		this.fileMaxSizeBytes = fileMaxSizeBytes;
		this.requestMaxSizeBytes = requestMaxSizeBytes;
	}

	public long getFileMaxSizeBytes() {
		return fileMaxSizeBytes;
	}

	public void setFileMaxSizeBytes(long fileMaxSizeBytes) {
		this.fileMaxSizeBytes = fileMaxSizeBytes;
	}

	public long getRequestMaxSizeBytes() {
		return requestMaxSizeBytes;
	}

	public void setRequestMaxSizeBytes(long requestMaxSizeBytes) {
		this.requestMaxSizeBytes = requestMaxSizeBytes;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof SettingsDTO)) {
			return false;
		}
		SettingsDTO that = (SettingsDTO) o;
		return fileMaxSizeBytes == that.fileMaxSizeBytes &&
		       requestMaxSizeBytes == that.requestMaxSizeBytes;
	}

	@Override
	public int hashCode() {
		return Objects.hash(fileMaxSizeBytes, requestMaxSizeBytes);
	}

	@Override
	public String toString() {
		return "SettingsDTO{" +
		       "fileMaxSizeBytes=" + fileMaxSizeBytes +
		       ", requestMaxSizeBytes=" + requestMaxSizeBytes +
		       '}';
	}
}
