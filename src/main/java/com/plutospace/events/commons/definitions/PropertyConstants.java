/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.commons.definitions;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
public class PropertyConstants {

	@Value("${api.version}")
	private String apiVersion;

	@Value("${events-public.encryption-secret-key}")
	private String eventsEncryptionSecretKey;

	@Value("${events-login.encryption-secret-key}")
	private String eventsLoginEncryptionSecretKey;

	@Value("${events.iv-key}")
	private String eventsIvKey;

	@Value("${access.key.id}")
	private String accessKeyId;

	@Value("${access.key.secret}")
	private String accessKeySecret;

	@Value("${s3.region.name}")
	private String s3RegionName;

	@Value("${s3.bucket.name}")
	private String s3BucketName;

	@Value("${s3.endpoint}")
	private String s3Endpoint;

	@Value("${s3.display-endpoint}")
	private String s3DisplayEndpoint;

	@Value("${paystack.verify-url}")
	private String paystackVerifyUrl;

	@Value("${paystack.secret-key}")
	private String paystackSecretKey;

	@Value("${paystack.base-url}")
	private String paystackBaseUrl;

	@Value("${frontend.base-url}")
	private String frontendBaseUrl;

	@Value("${invite-user.url}")
	private String inviteUserUrl;

	@Value("${go-mailer.base-url}")
	private String goMailerBaseUrl;

	@Value("${go-mailer.transactionals-api.url}")
	private String goMailerTransactionalUrl;

	@Value("${go-mailer.api-key}")
	private String goMailerApiKey;

	@Value("${go-mailer.sender-email}")
	private String goMailerSenderEmail;

	@Value("${go-mailer.template-code.user-invite}")
	private String goMailerUserInviteTemplateCode;

	@Value("${techeverywhere.support-email}")
	private String techEveryWhereSupportEmail;
}
