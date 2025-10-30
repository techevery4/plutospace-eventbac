/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.commons.config.auditor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

import com.plutospace.events.commons.config.security.CustomAuditorAware;
import com.plutospace.events.commons.definitions.PropertyConstants;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableMongoAuditing(auditorAwareRef = "auditorAware")
@RequiredArgsConstructor
public class AuditorConfig {

	private final PropertyConstants propertyConstants;

	@Bean
	AuditorAware<String> auditorAware() {
		return new CustomAuditorAware(propertyConstants);
	}
}
