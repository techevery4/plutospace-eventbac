/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.validation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.plutospace.events.commons.exception.GeneralPlatformDomainRuleException;
import com.plutospace.events.domain.data.request.ChangeAccountUserPasswordRequest;
import com.plutospace.events.domain.data.request.InviteAccountUserRequest;
import com.plutospace.events.domain.data.request.RegisterBusinessAccountRequest;
import com.plutospace.events.domain.data.request.RegisterPersonalAccountRequest;

@Component
public class AccountUserValidator {

	private static final String REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";

	public void validate(RegisterPersonalAccountRequest registerPersonalAccountRequest) {
		String firstNameCannotBeNullValidationMessage = "First name cannot be empty";
		String lastNameCannotBeNullValidationMessage = "Last name cannot be empty";
		String emailCannotBeNullValidationMessage = "Email cannot be null";
		String emailInvalidValidationMessage = "Email is invalid";
		String passwordCannotBeNullValidationMessage = "Password cannot be null";
		String passwordDoesNotMatchValidationMessage = "Password does not match";

		if (StringUtils.isBlank(registerPersonalAccountRequest.email())) {
			throw new GeneralPlatformDomainRuleException(emailCannotBeNullValidationMessage);
		}
		if (!validateEmail(registerPersonalAccountRequest.email())) {
			throw new GeneralPlatformDomainRuleException(emailInvalidValidationMessage);
		}
		if (StringUtils.isBlank(registerPersonalAccountRequest.firstName())) {
			throw new GeneralPlatformDomainRuleException(firstNameCannotBeNullValidationMessage);
		}
		if (StringUtils.isBlank(registerPersonalAccountRequest.lastName())) {
			throw new GeneralPlatformDomainRuleException(lastNameCannotBeNullValidationMessage);
		}
		if (StringUtils.isBlank(registerPersonalAccountRequest.password())) {
			throw new GeneralPlatformDomainRuleException(passwordCannotBeNullValidationMessage);
		}
		if (!registerPersonalAccountRequest.password().equals(registerPersonalAccountRequest.confirmPassword())) {
			throw new GeneralPlatformDomainRuleException(passwordDoesNotMatchValidationMessage);
		}
	}

	public void validate(RegisterBusinessAccountRequest registerBusinessAccountRequest) {
		String nameCannotBeNullValidationMessage = "Business name cannot be empty";
		String emailCannotBeNullValidationMessage = "Email cannot be null";
		String emailInvalidValidationMessage = "Email is invalid";
		String passwordCannotBeNullValidationMessage = "Password cannot be null";
		String passwordDoesNotMatchValidationMessage = "Password does not match";

		if (StringUtils.isBlank(registerBusinessAccountRequest.email())) {
			throw new GeneralPlatformDomainRuleException(emailCannotBeNullValidationMessage);
		}
		if (!validateEmail(registerBusinessAccountRequest.email())) {
			throw new GeneralPlatformDomainRuleException(emailInvalidValidationMessage);
		}
		if (StringUtils.isBlank(registerBusinessAccountRequest.name())) {
			throw new GeneralPlatformDomainRuleException(nameCannotBeNullValidationMessage);
		}
		if (StringUtils.isBlank(registerBusinessAccountRequest.password())) {
			throw new GeneralPlatformDomainRuleException(passwordCannotBeNullValidationMessage);
		}
		if (!registerBusinessAccountRequest.password().equals(registerBusinessAccountRequest.confirmPassword())) {
			throw new GeneralPlatformDomainRuleException(passwordDoesNotMatchValidationMessage);
		}
	}

	public void validate(ChangeAccountUserPasswordRequest changeAccountUserPasswordRequest) {
		String oldPasswordCannotBeNullValidationMessage = "Old Password cannot be null";
		String oldPasswordCannotBeNewPasswordValidationMessage = "Old Password cannot be equal to new password";
		String passwordCannotBeNullValidationMessage = "New Password cannot be null";
		String passwordDoesNotMatchValidationMessage = "Password does not match";

		if (StringUtils.isBlank(changeAccountUserPasswordRequest.oldPassword())) {
			throw new GeneralPlatformDomainRuleException(oldPasswordCannotBeNullValidationMessage);
		}
		if (StringUtils.isBlank(changeAccountUserPasswordRequest.newPassword())) {
			throw new GeneralPlatformDomainRuleException(passwordCannotBeNullValidationMessage);
		}
		if (!changeAccountUserPasswordRequest.newPassword()
				.equals(changeAccountUserPasswordRequest.confirmPassword())) {
			throw new GeneralPlatformDomainRuleException(passwordDoesNotMatchValidationMessage);
		}
		if (changeAccountUserPasswordRequest.newPassword().equals(changeAccountUserPasswordRequest.oldPassword())) {
			throw new GeneralPlatformDomainRuleException(oldPasswordCannotBeNewPasswordValidationMessage);
		}
	}

	public void validate(InviteAccountUserRequest inviteAccountUserRequest) {
		String firstNameCannotBeNullValidationMessage = "First name cannot be empty";
		String lastNameCannotBeNullValidationMessage = "Last name cannot be empty";
		String emailCannotBeNullValidationMessage = "Email cannot be null";
		String emailInvalidValidationMessage = "Email is invalid";

		if (StringUtils.isBlank(inviteAccountUserRequest.email())) {
			throw new GeneralPlatformDomainRuleException(emailCannotBeNullValidationMessage);
		}
		if (!validateEmail(inviteAccountUserRequest.email())) {
			throw new GeneralPlatformDomainRuleException(emailInvalidValidationMessage);
		}
		if (StringUtils.isBlank(inviteAccountUserRequest.firstName())) {
			throw new GeneralPlatformDomainRuleException(firstNameCannotBeNullValidationMessage);
		}
		if (StringUtils.isBlank(inviteAccountUserRequest.lastName())) {
			throw new GeneralPlatformDomainRuleException(lastNameCannotBeNullValidationMessage);
		}
	}

	public boolean validateEmail(String email) {
		Pattern pattern = Pattern.compile(REGEX);
		Matcher matcher = pattern.matcher(email);

		return matcher.matches();
	}
}
