package com.duallab.verapdf.jobservice.server.config;

import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.io.IOException;

/**
 * @author Maksim Bezrukov
 */
@Configuration
public class WorkerConfig {
	private static final Logger LOGGER = LoggerFactory.getLogger(WorkerConfig.class);

	@Bean("workingDir")
	public File workingDir(@Value("${verapdf.files.worker-dir}") String baseDirPath) throws IOException {
		File res = new File(baseDirPath);
		if (!res.isDirectory()) {
			LOGGER.warn("Missing working directory. Trying to create with path: " + res.getAbsolutePath());
			FileUtils.forceMkdir(res);
		}
		FileUtils.cleanDirectory(res);
		return res;
	}
}
