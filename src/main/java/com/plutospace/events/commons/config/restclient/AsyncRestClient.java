/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.commons.config.restclient;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.plutospace.events.commons.exception.ExternalServiceException;
import com.plutospace.events.commons.exception.GeneralPlatformServiceException;
import com.plutospace.events.commons.exception.handler.ExceptionResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class AsyncRestClient {

	public static final String COULD_NOT_COMPLETE_HTTP_REQUEST = "Could not complete your request at this time. Please try again later";
	private final RestClient restClient;
	private final ObjectMapper objectMapper;

	public <T> T post(String uri, Object requestBody, Class<T> responseType, Map<String, String> headers) {
		try {
			ResponseEntity<T> response = restClient.post().uri(uri)
					.headers(httpHeaders -> headers.forEach(httpHeaders::add)).body(requestBody).retrieve()
					.toEntity(responseType);

			if (response.getBody() == null) {
				throw new GeneralPlatformServiceException(COULD_NOT_COMPLETE_HTTP_REQUEST);
			}

			return response.getBody();
		} catch (HttpClientErrorException e) {
			decode(e);
		} catch (Exception e) {
			log.info("Unexpected post error occurred: {}", e.getMessage(), e);
		}

		throw new GeneralPlatformServiceException(COULD_NOT_COMPLETE_HTTP_REQUEST);
	}

	public <T> T postV2(String uri, Object requestBody, ParameterizedTypeReference<T> responseType,
			Map<String, String> headers) {
		try {
			ResponseEntity<T> response = restClient.post().uri(uri)
					.headers(httpHeaders -> headers.forEach(httpHeaders::add)).body(requestBody).retrieve()
					.toEntity(responseType);

			if (response.getBody() == null) {
				throw new GeneralPlatformServiceException(COULD_NOT_COMPLETE_HTTP_REQUEST);
			}

			return response.getBody();
		} catch (HttpClientErrorException e) {
			decode(e);
		} catch (Exception e) {
			log.info("Unexpected post v2 error occurred: {}", e.getMessage(), e);
		}

		throw new GeneralPlatformServiceException(COULD_NOT_COMPLETE_HTTP_REQUEST);
	}

	public <T> T get(String uri, Class<T> responseType, Map<String, String> headers) {
		try {
			ResponseEntity<T> response = restClient.get().uri(uri)
					.headers(httpHeaders -> headers.forEach(httpHeaders::add)).retrieve().toEntity(responseType);

			if (response.getBody() == null) {
				throw new GeneralPlatformServiceException(COULD_NOT_COMPLETE_HTTP_REQUEST);
			}

			return response.getBody();
		} catch (HttpClientErrorException e) {
			decode(e);
		} catch (Exception e) {
			log.info("Unexpected get error occurred: {}", e.getMessage(), e);
		}
		throw new GeneralPlatformServiceException(COULD_NOT_COMPLETE_HTTP_REQUEST);
	}

	public <T> T patch(String uri, Object requestBody, ParameterizedTypeReference<T> responseType,
			Map<String, String> headers) {
		try {
			ResponseEntity<T> response = restClient.patch().uri(uri)
					.headers(httpHeaders -> headers.forEach(httpHeaders::add)).body(requestBody).retrieve()
					.toEntity(responseType);

			if (response.getBody() == null) {
				throw new GeneralPlatformServiceException(COULD_NOT_COMPLETE_HTTP_REQUEST);
			}

			return response.getBody();
		} catch (HttpClientErrorException e) {
			decode(e);
		} catch (Exception e) {
			log.info("Unexpected patch error occurred: {}", e.getMessage(), e);
		}

		throw new GeneralPlatformServiceException(COULD_NOT_COMPLETE_HTTP_REQUEST);
	}

	private void decode(HttpClientErrorException e) {
		String errorMessage = e.getMessage();
		int code = e.getStatusCode().value();

		log.info("code {}", code);
		log.info("message {}", errorMessage);

		String cleanErrorMessage = extractExternalErrorMessage(errorMessage);

		ExceptionResponse exceptionResponse = new ExceptionResponse();
		exceptionResponse.setMessage(cleanErrorMessage);
		throw new ExternalServiceException(exceptionResponse.getMessage());
	}

	private String extractExternalErrorMessage(String errorResponse) {
		if (errorResponse == null || errorResponse.isEmpty()) {
			return COULD_NOT_COMPLETE_HTTP_REQUEST;
		}

		try {
			// Look for JSON part in the error response
			int jsonStart = errorResponse.indexOf("{");
			if (jsonStart != -1) {
				String jsonPart = errorResponse.substring(jsonStart);

				Pattern pattern = Pattern.compile("\"message\"\\s*:\\s*\"([^\"]+)\"");
				Matcher matcher = pattern.matcher(jsonPart);
				if (matcher.find()) {
					return matcher.group(1);
				}
			}
		} catch (Exception ignored) {
		}

		return COULD_NOT_COMPLETE_HTTP_REQUEST;
	}
}
