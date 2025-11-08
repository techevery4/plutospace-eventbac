/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.commons.utils;

import org.springframework.stereotype.Component;

import com.plutospace.events.commons.config.security.AES;

@Component
public class LinkGenerator {

	public String generatePublicLink(String id, String accountId, String type, String secretKey) {
		String originalString = id + ":" + accountId + ":" + type;
		return AES.encrypt(originalString, secretKey);
	}

	public String extractDetailsFromPublicLink(String publicLink, String secretKay) {
		return AES.decrypt(publicLink, secretKay);
	}
}
