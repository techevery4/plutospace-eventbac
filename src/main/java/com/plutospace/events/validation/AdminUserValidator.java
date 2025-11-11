/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.validation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.plutospace.events.commons.exception.GeneralPlatformDomainRuleException;
import com.plutospace.events.domain.data.request.ChangeAdminUserPasswordRequest;
import com.plutospace.events.domain.data.request.CreateAdminUserRequest;

@Component
public class AdminUserValidator {

	private static final String REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";

	public void validate(CreateAdminUserRequest createAdminUserRequest) {
		String emailCannotBeNullValidationMessage = "Email cannot be null";
		String emailInvalidValidationMessage = "Email is invalid";
		String firstNameCannotBeNullValidationMessage = "First name cannot be null";
		String lastNameCannotBeNullValidationMessage = "Last name cannot be null";
		String passwordCannotBeNullValidationMessage = "Password cannot be null";
		String passwordDoesNotMatchValidationMessage = "Password does not match";

		if (StringUtils.isBlank(createAdminUserRequest.email())) {
			throw new GeneralPlatformDomainRuleException(emailCannotBeNullValidationMessage);
		}
		if (!validateEmail(createAdminUserRequest.email())) {
			throw new GeneralPlatformDomainRuleException(emailInvalidValidationMessage);
		}
		if (StringUtils.isBlank(createAdminUserRequest.firstName())) {
			throw new GeneralPlatformDomainRuleException(firstNameCannotBeNullValidationMessage);
		}
		if (StringUtils.isBlank(createAdminUserRequest.lastName())) {
			throw new GeneralPlatformDomainRuleException(lastNameCannotBeNullValidationMessage);
		}
		if (StringUtils.isBlank(createAdminUserRequest.password())) {
			throw new GeneralPlatformDomainRuleException(passwordCannotBeNullValidationMessage);
		}
		if (!createAdminUserRequest.password().equals(createAdminUserRequest.confirmPassword())) {
			throw new GeneralPlatformDomainRuleException(passwordDoesNotMatchValidationMessage);
		}
	}

	public void validate(ChangeAdminUserPasswordRequest changeAdminUserPasswordRequest) {
		String oldPasswordCannotBeNullValidationMessage = "Old Password cannot be null";
		String oldPasswordCannotBeNewPasswordValidationMessage = "Old Password cannot be equal to new password";
		String passwordCannotBeNullValidationMessage = "New Password cannot be null";
		String passwordDoesNotMatchValidationMessage = "Password does not match";

		if (StringUtils.isBlank(changeAdminUserPasswordRequest.oldPassword())) {
			throw new GeneralPlatformDomainRuleException(oldPasswordCannotBeNullValidationMessage);
		}
		if (StringUtils.isBlank(changeAdminUserPasswordRequest.newPassword())) {
			throw new GeneralPlatformDomainRuleException(passwordCannotBeNullValidationMessage);
		}
		if (!changeAdminUserPasswordRequest.newPassword().equals(changeAdminUserPasswordRequest.confirmPassword())) {
			throw new GeneralPlatformDomainRuleException(passwordDoesNotMatchValidationMessage);
		}
		if (changeAdminUserPasswordRequest.newPassword().equals(changeAdminUserPasswordRequest.oldPassword())) {
			throw new GeneralPlatformDomainRuleException(oldPasswordCannotBeNewPasswordValidationMessage);
		}
	}

	public boolean validateEmail(String email) {
		Pattern pattern = Pattern.compile(REGEX);
		Matcher matcher = pattern.matcher(email);

		return matcher.matches();
	}
}
