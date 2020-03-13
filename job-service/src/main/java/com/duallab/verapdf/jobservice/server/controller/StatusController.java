package com.duallab.verapdf.jobservice.server.controller;

import com.duallab.verapdf.jobservice.model.dto.InfoDTO;
import com.duallab.verapdf.jobservice.model.dto.SettingsDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.info.BuildProperties;
import org.springframework.util.unit.DataSize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Maksim Bezrukov
 */
@RestController
@RequestMapping("/status")
public class StatusController {

	private static final String VERAPDF_VERSIONS_PREFIX = "verapdf.versions.";

	private final DataSize fileMaxSize;
	private final DataSize requestMaxSize;
	private final Map<String, String> verapdfVersions;

	public StatusController(BuildProperties properties,
                            @Value("${spring.servlet.multipart.max-file-size}") DataSize fileMaxSize,
                            @Value("${spring.servlet.multipart.max-request-size}") DataSize requestMaxSize) {
		this.fileMaxSize = fileMaxSize;
		this.requestMaxSize = requestMaxSize;
		this.verapdfVersions = new HashMap<>();

		properties.forEach(property -> {
			if (property.getKey().startsWith(VERAPDF_VERSIONS_PREFIX)) {
				verapdfVersions.put(property.getKey().substring(VERAPDF_VERSIONS_PREFIX.length()),
				                    property.getValue());
			}
		});
	}

	@GetMapping("/settings")
	public SettingsDTO getSettings() {
		return new SettingsDTO(fileMaxSize.toBytes(), requestMaxSize.toBytes());
	}

	@GetMapping("/info")
	public InfoDTO getInfo() {
		return new InfoDTO(verapdfVersions);
	}
}
