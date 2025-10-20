/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.commons.config.documentation;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

	@Bean
	public OpenAPI customOpenAPI() {
		return new OpenAPI().info(new Info().title("Plutospace Events Service").version("1.0")
				.description("This is the collection of all API endpoints for Plutospace Events")
				.termsOfService("http://swagger.io/terms/")
				.contact(new Contact().name("PlutospaceEvents").url("https://www.plutospaceevents.com/").email("hello@plutospace-events.com")));
	}
}
