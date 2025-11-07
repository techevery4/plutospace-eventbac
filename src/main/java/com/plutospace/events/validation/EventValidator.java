/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.validation;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.plutospace.events.commons.exception.GeneralPlatformDomainRuleException;
import com.plutospace.events.domain.data.request.CreateEventRequest;

@Component
public class EventValidator {

	public void validate(CreateEventRequest createEventRequest) {
		String nameCannotBeNullValidationMessage = "Name cannot be empty";
		String accountCannotBeNullValidationMessage = "Account owner cannot be empty";

		if (StringUtils.isBlank(createEventRequest.name())) {
			throw new GeneralPlatformDomainRuleException(nameCannotBeNullValidationMessage);
		}
		if (StringUtils.isBlank(createEventRequest.accountId())) {
			throw new GeneralPlatformDomainRuleException(accountCannotBeNullValidationMessage);
		}
	}
}
