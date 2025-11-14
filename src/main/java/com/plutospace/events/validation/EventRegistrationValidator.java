/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.validation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.plutospace.events.commons.exception.GeneralPlatformDomainRuleException;
import com.plutospace.events.domain.data.request.CreateEventRegistrationRequest;

@Component
public class EventRegistrationValidator {

	private static final String REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";

	public void validate(CreateEventRegistrationRequest createEventRegistrationRequest) {
		String eventCannotBeNullValidationMessage = "You must specify an event that you are registering for";
		String emailCannotBeNullValidationMessage = "Email cannot be null";
		String emailInvalidValidationMessage = "Email is invalid";
		String formCannotBeNullValidationMessage = "Kindly fill the form";

		if (StringUtils.isBlank(createEventRegistrationRequest.email())) {
			throw new GeneralPlatformDomainRuleException(emailCannotBeNullValidationMessage);
		}
		if (!validateEmail(createEventRegistrationRequest.email())) {
			throw new GeneralPlatformDomainRuleException(emailInvalidValidationMessage);
		}
		if (StringUtils.isBlank(createEventRegistrationRequest.eventId())) {
			throw new GeneralPlatformDomainRuleException(eventCannotBeNullValidationMessage);
		}
		if (createEventRegistrationRequest.eventRegistrationDataRequests().isEmpty()) {
			throw new GeneralPlatformDomainRuleException(formCannotBeNullValidationMessage);
		}
	}

	public boolean validateEmail(String email) {
		Pattern pattern = Pattern.compile(REGEX);
		Matcher matcher = pattern.matcher(email);

		return matcher.matches();
	}
}
