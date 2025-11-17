/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.commons.config.security;

import java.util.Optional;

import org.springframework.data.domain.AuditorAware;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.plutospace.events.commons.definitions.GeneralConstants;
import com.plutospace.events.commons.definitions.PropertyConstants;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class CustomAuditorAware implements AuditorAware<String> {

	private final PropertyConstants propertyConstants;

	@Override
	public Optional<String> getCurrentAuditor() {
		ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

		if (attributes == null) {
			// Not in a web request context (e.g., scheduled task)
			return Optional.of("SYSTEM");
		}

		HttpServletRequest request = attributes.getRequest();

		String token = request.getHeader(GeneralConstants.TOKEN_KEY);
		if (token == null) {
			return Optional.empty();
		}

		String decryptedToken = extractOrgIDAndUserID(token);
		String[] words = decryptedToken.split(":");

		return Optional.of(words[1]); // or return Optional.ofNullable(words[1])
	}

	private String extractOrgIDAndUserID(String token) {
		return AES.decrypt(token, propertyConstants.getEventsLoginEncryptionSecretKey());
	}
}
