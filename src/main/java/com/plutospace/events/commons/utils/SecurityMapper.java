/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.commons.utils;

import java.time.LocalDateTime;
import java.time.ZoneId;

import org.springframework.stereotype.Component;

import com.plutospace.events.commons.config.security.AES;
import com.plutospace.events.commons.exception.UnauthorizedAccessException;

@Component
public class SecurityMapper {

	public String generateEncryptedLoginTokenForAdmin(String id, String secretKey) {
		long timestamp = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
		String originalString = timestamp + ":" + id + ":ADMIN";
		return AES.encrypt(originalString, secretKey);
	}

	public String generateEncryptedLoginTokenForUser(String id, String accountId, String secretKey) {
		long timestamp = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
		String originalString = timestamp + ":" + id + ":" + accountId + ":CUSTOMER";
		return AES.encrypt(originalString, secretKey);
	}

	public String extractDetailsFromLoginToken(String loginToken, String secretKay) {
		return AES.decrypt(loginToken, secretKay);
	}

	public String retrieveAccountId(String token, String secretKey) {
		if (token == null)
			throw new UnauthorizedAccessException(
					"You cannot complete this request as necessary credentials are missing. Kindly login again");
		String decryptedToken = this.extractDetailsFromLoginToken(token, secretKey);
		String[] words = decryptedToken.split(":");
		if (words.length < 4)
			throw new UnauthorizedAccessException("Your session has expired. Kindly login again to restart session");

		return words[2];
	}

	public String retrieveAdminUserId(String token, String secretKey) {
		if (token == null)
			throw new UnauthorizedAccessException(
					"You cannot complete this request as necessary credentials are missing. Kindly login again");
		String decryptedToken = this.extractDetailsFromLoginToken(token, secretKey);
		String[] words = decryptedToken.split(":");
		if (words.length < 3)
			throw new UnauthorizedAccessException("Your session has expired. Kindly login again to restart session");

		return words[1];
	}

	public String retrieveAccountUserId(String token, String secretKey) {
		if (token == null)
			throw new UnauthorizedAccessException(
					"You cannot complete this request as necessary credentials are missing. Kindly login again");
		String decryptedToken = this.extractDetailsFromLoginToken(token, secretKey);
		String[] words = decryptedToken.split(":");
		if (words.length < 4)
			throw new UnauthorizedAccessException("Your session has expired. Kindly login again to restart session");

		return words[1];
	}
}
