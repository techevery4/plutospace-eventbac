/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.validation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.plutospace.events.commons.exception.GeneralPlatformDomainRuleException;
import com.plutospace.events.domain.data.request.BookFreeSlotRequest;
import com.plutospace.events.domain.data.request.SaveFreeSlotRequest;

@Component
public class FreeSlotValidator {

	private static final String REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";

	public void validate(SaveFreeSlotRequest saveFreeSlotRequest) {
		String titleCannotBeNullValidationMessage = "Title cannot be empty";
		String dateCannotBeNullValidationMessage = "Date cannot be empty";
		String startTimeCannotBeNullValidationMessage = "Start time cannot be empty";
		String timeCannotBeInvalidValidationMessage = "Start time cannot be after end time";
		String timezoneValueCannotBeNullValidationMessage = "Timezone must be selected";
		String timezoneStringCannotBeNullValidationMessage = "Timezone must be passed";

		if (StringUtils.isBlank(saveFreeSlotRequest.title())) {
			throw new GeneralPlatformDomainRuleException(titleCannotBeNullValidationMessage);
		}
		if (ObjectUtils.isEmpty(saveFreeSlotRequest.date())) {
			throw new GeneralPlatformDomainRuleException(dateCannotBeNullValidationMessage);
		}
		if (ObjectUtils.isEmpty(saveFreeSlotRequest.startTime())) {
			throw new GeneralPlatformDomainRuleException(startTimeCannotBeNullValidationMessage);
		}
		if (saveFreeSlotRequest.startTime() >= saveFreeSlotRequest.endTime()) {
			throw new GeneralPlatformDomainRuleException(timeCannotBeInvalidValidationMessage);
		}
		if (ObjectUtils.isEmpty(saveFreeSlotRequest.timezoneValue())) {
			throw new GeneralPlatformDomainRuleException(timezoneValueCannotBeNullValidationMessage);
		}
		if (StringUtils.isBlank(saveFreeSlotRequest.timezoneString())) {
			throw new GeneralPlatformDomainRuleException(timezoneStringCannotBeNullValidationMessage);
		}
	}

	public void validate(BookFreeSlotRequest bookFreeSlotRequest) {
		String freeSlotCannotBeNullValidationMessage = "This registration is not tied to any available slot";
		String emailCannotBeNullValidationMessage = "Email cannot be empty";
		String emailCannotBeInvalidValidationMessage = "Email is invalid";

		if (StringUtils.isBlank(bookFreeSlotRequest.freeSlotId())) {
			throw new GeneralPlatformDomainRuleException(freeSlotCannotBeNullValidationMessage);
		}
		if (StringUtils.isBlank(bookFreeSlotRequest.email())) {
			throw new GeneralPlatformDomainRuleException(emailCannotBeNullValidationMessage);
		}
		if (!validateEmail(bookFreeSlotRequest.email())) {
			throw new GeneralPlatformDomainRuleException(emailCannotBeInvalidValidationMessage);
		}
	}

	public boolean validateEmail(String email) {
		Pattern pattern = Pattern.compile(REGEX);
		Matcher matcher = pattern.matcher(email);

		return matcher.matches();
	}
}
