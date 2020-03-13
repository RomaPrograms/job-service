package com.duallab.verapdf.jobservice.server.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author Maksim Bezrukov
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

	private final String[] origins;
	private final String[] methods;

	public WebConfig(@Value("${verapdf.web.cors.methods}") String[] origins,
	                 @Value("${verapdf.web.cors.origins}") String[] methods) {
		this.origins = origins;
		this.methods = methods;
	}

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry
				.addMapping("/**")
		        .allowedOrigins(origins)
		        .allowedMethods(methods);
	}
}
