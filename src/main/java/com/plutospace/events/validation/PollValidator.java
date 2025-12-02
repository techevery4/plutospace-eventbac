/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.validation;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.plutospace.events.commons.exception.GeneralPlatformDomainRuleException;
import com.plutospace.events.domain.data.request.SavePollRequest;
import com.plutospace.events.domain.entities.Poll;

@Component
public class PollValidator {

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
}
