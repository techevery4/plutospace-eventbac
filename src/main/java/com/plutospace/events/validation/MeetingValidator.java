/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.validation;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.plutospace.events.commons.exception.GeneralPlatformDomainRuleException;
import com.plutospace.events.domain.data.request.CreateMeetingRequest;

@Component
public class MeetingValidator {

	public void validate(CreateMeetingRequest createMeetingRequest) {
		String titleCannotBeNullValidationMessage = "Title cannot be empty";
		String accountCannotBeNullValidationMessage = "Account owner cannot be empty";
		String timezoneValueCannotBeNullValidationMessage = "Timezone must be selected";
		String timezoneStringCannotBeNullValidationMessage = "Timezone must be passed";
		String startDateCannotBeNullValidationMessage = "Start date cannot be empty";
		String endDateCannotBeNullValidationMessage = "End date cannot be empty";
		String startTimeCannotBeNullValidationMessage = "Start time must be passed";
		String endTimeCannotBeNullValidationMessage = "End time must be passed";
		String dateCannotBeInvalidValidationMessage = "Start date cannot be greater than end date";
		String timeCannotBeInvalidValidationMessage = "Start time cannot be greater than end time";

		if (StringUtils.isBlank(createMeetingRequest.getTitle())) {
			throw new GeneralPlatformDomainRuleException(titleCannotBeNullValidationMessage);
		}
		if (StringUtils.isBlank(createMeetingRequest.getAccountId())) {
			throw new GeneralPlatformDomainRuleException(accountCannotBeNullValidationMessage);
		}
		if (ObjectUtils.isEmpty(createMeetingRequest.getTimezoneValue())) {
			throw new GeneralPlatformDomainRuleException(timezoneValueCannotBeNullValidationMessage);
		}
		if (StringUtils.isBlank(createMeetingRequest.getTimezoneString())) {
			throw new GeneralPlatformDomainRuleException(timezoneStringCannotBeNullValidationMessage);
		}
		if (ObjectUtils.isEmpty(createMeetingRequest.getStartDate())) {
			throw new GeneralPlatformDomainRuleException(startDateCannotBeNullValidationMessage);
		}
		if (ObjectUtils.isEmpty(createMeetingRequest.getEndDate())) {
			throw new GeneralPlatformDomainRuleException(endDateCannotBeNullValidationMessage);
		}
		if (ObjectUtils.isEmpty(createMeetingRequest.getStartTime())) {
			throw new GeneralPlatformDomainRuleException(startTimeCannotBeNullValidationMessage);
		}
		if (ObjectUtils.isEmpty(createMeetingRequest.getEndTime())) {
			throw new GeneralPlatformDomainRuleException(endTimeCannotBeNullValidationMessage);
		}
		if (createMeetingRequest.getStartDate().isAfter(createMeetingRequest.getEndDate())) {
			throw new GeneralPlatformDomainRuleException(dateCannotBeInvalidValidationMessage);
		}
		if (createMeetingRequest.getStartTime() >= createMeetingRequest.getEndTime()) {
			throw new GeneralPlatformDomainRuleException(timeCannotBeInvalidValidationMessage);
		}
	}
}
