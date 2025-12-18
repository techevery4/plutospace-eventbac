/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.commons.config.restclient;

import org.springframework.boot.autoconfigure.web.client.RestClientBuilderConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class GeneralRestClientConfig {

	@Bean
	public RestClient restClient(RestClientBuilderConfigurer configurer) {
		return configurer.configure(RestClient.builder()).requestInterceptor((request, body, execution) -> {
			request.getHeaders();
			return execution.execute(request, body);
		}).build();
	}
}
