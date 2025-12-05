/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.validation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.plutospace.events.commons.exception.GeneralPlatformDomainRuleException;
import com.plutospace.events.domain.data.request.CreateProposalRequest;
import com.plutospace.events.domain.data.request.CreateProposalSubmissionRequest;

@Component
public class ProposalValidator {

	private static final String REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";

	public void validate(CreateProposalRequest createProposalRequest) {
		String titleCannotBeNullValidationMessage = "Title cannot be empty";

		if (StringUtils.isBlank(createProposalRequest.title())) {
			throw new GeneralPlatformDomainRuleException(titleCannotBeNullValidationMessage);
		}
	}

	public void validate(CreateProposalSubmissionRequest createProposalSubmissionRequest) {
		String emailCannotBeNullValidationMessage = "Email cannot be null";
		String emailInvalidValidationMessage = "Email is invalid";
		String nameCannotBeNullValidationMessage = "Company name cannot be null";
		String phoneNumberCannotBeInvalidValidationMessage = "Phone number supplied is invalid";
		String mediaCannotBeNullValidationMessage = "You have not attached your proposal document to this submission";

		if (StringUtils.isBlank(createProposalSubmissionRequest.email())) {
			throw new GeneralPlatformDomainRuleException(emailCannotBeNullValidationMessage);
		}
		if (!validateEmail(createProposalSubmissionRequest.email())) {
			throw new GeneralPlatformDomainRuleException(emailInvalidValidationMessage);
		}
		if (StringUtils.isBlank(createProposalSubmissionRequest.name())) {
			throw new GeneralPlatformDomainRuleException(nameCannotBeNullValidationMessage);
		}
		if (StringUtils.isNotBlank(createProposalSubmissionRequest.countryCode())
				|| StringUtils.isNotBlank(createProposalSubmissionRequest.phoneNumber())) {
			if (StringUtils.isBlank(createProposalSubmissionRequest.countryCode())
					|| StringUtils.isBlank(createProposalSubmissionRequest.phoneNumber()))
				throw new GeneralPlatformDomainRuleException(phoneNumberCannotBeInvalidValidationMessage);
		}
		if (StringUtils.isBlank(createProposalSubmissionRequest.mediaId())
				|| StringUtils.isBlank(createProposalSubmissionRequest.mediaUrl())) {
			throw new GeneralPlatformDomainRuleException(mediaCannotBeNullValidationMessage);
		}
	}

	public boolean validateEmail(String email) {
		Pattern pattern = Pattern.compile(REGEX);
		Matcher matcher = pattern.matcher(email);

		return matcher.matches();
	}
}
