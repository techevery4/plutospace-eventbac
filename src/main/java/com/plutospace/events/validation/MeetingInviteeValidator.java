/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.validation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.plutospace.events.commons.exception.GeneralPlatformDomainRuleException;
import com.plutospace.events.domain.data.request.CreateMeetingInviteRequest;

@Component
public class MeetingInviteeValidator {

	private static final String REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";

	public void validate(CreateMeetingInviteRequest createMeetingInviteRequest) {
		String meetingCannotBeNullValidationMessage = "Meeting must be selected";
		String emailsCannotBeNullValidationMessage = "You need to invite at least one person";
		String emailCannotBeInvalidValidationMessage = " cannot be invalid";

		if (StringUtils.isBlank(createMeetingInviteRequest.getMeetingId())) {
			throw new GeneralPlatformDomainRuleException(meetingCannotBeNullValidationMessage);
		}
		if (createMeetingInviteRequest.getEmails().isEmpty()) {
			throw new GeneralPlatformDomainRuleException(emailsCannotBeNullValidationMessage);
		}
		for (String email : createMeetingInviteRequest.getEmails()) {
			if (!validateEmail(email)) {
				throw new GeneralPlatformDomainRuleException(email + emailCannotBeInvalidValidationMessage);
			}
		}
	}

	public boolean validateEmail(String email) {
		Pattern pattern = Pattern.compile(REGEX);
		Matcher matcher = pattern.matcher(email);

		return matcher.matches();
	}
}
