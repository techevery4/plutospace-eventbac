/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.commons.config.security;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.stereotype.Component;

import com.plutospace.events.commons.definitions.GeneralConstants;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class AES {

	private static SecretKeySpec secretKey;
	private static byte[] key;

	public static void setKey(final String myKey) {
		MessageDigest sha = null;
		try {
			key = myKey.getBytes(GeneralConstants.CONTENT_TYPE);
			sha = MessageDigest.getInstance(GeneralConstants.MESSAGE_DIGEST);
			key = sha.digest(key);
			key = Arrays.copyOf(key, GeneralConstants.ENCRYPTION_KEY_LENGTH);
			secretKey = new SecretKeySpec(key, GeneralConstants.ENCRYPTION_TYPE);
		} catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
			log.error("Error While Setting Encryption Key ", e);
		}
	}

	public static String encrypt(final String strToEncrypt, final String secret) {
		try {
			setKey(secret);
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, secretKey);
			return Base64.getEncoder()
					.encodeToString(cipher.doFinal(strToEncrypt.getBytes(GeneralConstants.CONTENT_TYPE)));
		} catch (Exception e) {
			log.error("Error While Encrypting: {}", e.toString());
			return null;
		}
	}

	public static String decrypt(final String strToDecrypt, final String secret) {
		try {
			setKey(secret);
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
			cipher.init(Cipher.DECRYPT_MODE, secretKey);
			return new String(cipher.doFinal(Base64.getDecoder().decode(strToDecrypt)));
		} catch (Exception e) {
			log.error("Error While Decrypting:{} ", e.toString());
			return null;
		}
	}
}
