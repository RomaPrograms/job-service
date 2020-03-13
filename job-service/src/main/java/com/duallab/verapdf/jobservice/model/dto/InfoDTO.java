package com.duallab.verapdf.jobservice.model.dto;

import java.util.Map;
import java.util.Objects;

public class InfoDTO {
	private Map<String, String> veraPDFVersions;

	public InfoDTO(Map<String, String> veraPDFVersions) {
		this.veraPDFVersions = veraPDFVersions;
	}

	public Map<String, String> getVeraPDFVersions() {
		return veraPDFVersions;
	}

	public void setVeraPDFVersions(Map<String, String> veraPDFVersions) {
		this.veraPDFVersions = veraPDFVersions;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		InfoDTO infoDTO = (InfoDTO) o;
		return Objects.equals(veraPDFVersions, infoDTO.veraPDFVersions);
	}

	@Override
	public int hashCode() {
		return Objects.hash(veraPDFVersions);
	}

	@Override
	public String toString() {
		return "InfoDTO{" +
		       "veraPDFVersions=" + veraPDFVersions +
		       '}';
	}
}
