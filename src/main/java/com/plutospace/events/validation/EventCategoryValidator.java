/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.validation;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.plutospace.events.commons.exception.GeneralPlatformDomainRuleException;
import com.plutospace.events.domain.data.request.CreateEventCategoryRequest;

@Component
public class EventCategoryValidator {

	public void validate(CreateEventCategoryRequest createEventCategoryRequest) {
		String nameCannotBeNullValidationMessage = "Name cannot be empty";

		if (StringUtils.isBlank(createEventCategoryRequest.name())) {
			throw new GeneralPlatformDomainRuleException(nameCannotBeNullValidationMessage);
		}
	}
}
