/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.validation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.plutospace.events.commons.exception.GeneralPlatformDomainRuleException;
import com.plutospace.events.domain.data.request.CreatePollResultRequest;
import com.plutospace.events.domain.data.request.SavePollRequest;
import com.plutospace.events.domain.entities.Poll;

@Component
public class PollValidator {

	private static final String REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";

	public void validate(SavePollRequest savePollRequest) {
		String titleCannotBeNullValidationMessage = "Title cannot be empty";
		String bodiesCannotBeNullValidationMessage = "The poll must have questions attached to it";
		String optionsCannotBeNullValidationMessage = "Options need to be attached to the question: ";

		if (StringUtils.isBlank(savePollRequest.getTitle())) {
			throw new GeneralPlatformDomainRuleException(titleCannotBeNullValidationMessage);
		}
		if (savePollRequest.getBodies().isEmpty()) {
			throw new GeneralPlatformDomainRuleException(bodiesCannotBeNullValidationMessage);
		}
		for (Poll.Body body : savePollRequest.getBodies()) {
			if (body.getOptions().isEmpty()) {
				throw new GeneralPlatformDomainRuleException(optionsCannotBeNullValidationMessage + body.getQuestion());
			}
		}
	}

	public void validate(CreatePollResultRequest createPollResultRequest) {
		String emailCannotBeNullValidationMessage = "Email cannot be null";
		String emailInvalidValidationMessage = "Email is invalid";
		String resultsCannotBeNullValidationMessage = "The poll must have results attached to it";

		if (StringUtils.isBlank(createPollResultRequest.getEmail())) {
			throw new GeneralPlatformDomainRuleException(emailCannotBeNullValidationMessage);
		}
		if (!validateEmail(createPollResultRequest.getEmail())) {
			throw new GeneralPlatformDomainRuleException(emailInvalidValidationMessage);
		}
		if (createPollResultRequest.getResults().isEmpty()) {
			throw new GeneralPlatformDomainRuleException(resultsCannotBeNullValidationMessage);
		}
	}

	public boolean validateEmail(String email) {
		Pattern pattern = Pattern.compile(REGEX);
		Matcher matcher = pattern.matcher(email);

		return matcher.matches();
	}
}
