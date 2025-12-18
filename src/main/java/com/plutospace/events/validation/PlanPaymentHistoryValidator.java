/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.validation;

import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.plutospace.events.commons.exception.GeneralPlatformDomainRuleException;
import com.plutospace.events.domain.data.request.PayForPlanRequest;

@Component
public class PlanPaymentHistoryValidator {

	private static final String REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";

	public void validate(PayForPlanRequest payForPlanRequest) {
		String planCannotBeNullValidationMessage = "Plan cannot be empty";
		String planAmountCannotBeNullValidationMessage = "Plan amount cannot be empty";
		String planAmountCannotBeInvalidValidationMessage = "Plan amount is invalid";
		String paidAmountCannotBeNullValidationMessage = "Paid amount cannot be empty";
		String paidAmountCannotBeInvalidValidationMessage = "Paid amount is invalid";
		String emailCannotBeNullValidationMessage = "Email cannot be null";
		String emailInvalidValidationMessage = "Email is invalid";
		String referenceCannotBeNullValidationMessage = "Reference cannot be null";

		if (StringUtils.isBlank(payForPlanRequest.email())) {
			throw new GeneralPlatformDomainRuleException(emailCannotBeNullValidationMessage);
		}
		if (!validateEmail(payForPlanRequest.email())) {
			throw new GeneralPlatformDomainRuleException(emailInvalidValidationMessage);
		}
		if (StringUtils.isBlank(payForPlanRequest.planId())) {
			throw new GeneralPlatformDomainRuleException(planCannotBeNullValidationMessage);
		}
		if (ObjectUtils.isEmpty(payForPlanRequest.planAmount())) {
			throw new GeneralPlatformDomainRuleException(planAmountCannotBeNullValidationMessage);
		}
		if (payForPlanRequest.planAmount().compareTo(BigDecimal.ZERO) <= 0) {
			throw new GeneralPlatformDomainRuleException(planAmountCannotBeInvalidValidationMessage);
		}
		if (ObjectUtils.isEmpty(payForPlanRequest.paidAmount())) {
			throw new GeneralPlatformDomainRuleException(paidAmountCannotBeNullValidationMessage);
		}
		if (payForPlanRequest.paidAmount().compareTo(BigDecimal.ZERO) <= 0) {
			throw new GeneralPlatformDomainRuleException(paidAmountCannotBeInvalidValidationMessage);
		}
		if (StringUtils.isBlank(payForPlanRequest.reference())) {
			throw new GeneralPlatformDomainRuleException(referenceCannotBeNullValidationMessage);
		}
	}

	public boolean validateEmail(String email) {
		Pattern pattern = Pattern.compile(REGEX);
		Matcher matcher = pattern.matcher(email);

		return matcher.matches();
	}
}
