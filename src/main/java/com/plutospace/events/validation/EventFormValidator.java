/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.validation;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.plutospace.events.commons.exception.GeneralPlatformDomainRuleException;
import com.plutospace.events.domain.data.request.CreateEventFormRequest;

@Component
public class EventFormValidator {

	public void validate(CreateEventFormRequest createEventFormRequest) {
		String titleCannotBeNullValidationMessage = "Title cannot be empty";

		if (StringUtils.isBlank(createEventFormRequest.title())) {
			throw new GeneralPlatformDomainRuleException(titleCannotBeNullValidationMessage);
		}
	}
}
