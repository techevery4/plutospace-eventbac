/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.commons.config.cors;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**").allowedOrigins("*").exposedHeaders("x-token-ch").allowedMethods("GET", "POST", "PUT",
				"DELETE", "OPTIONS");
	}
}
