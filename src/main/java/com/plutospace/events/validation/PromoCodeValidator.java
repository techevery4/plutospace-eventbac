/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.validation;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.plutospace.events.commons.exception.GeneralPlatformDomainRuleException;
import com.plutospace.events.domain.data.request.CreatePromoCodeRequest;

@Component
public class PromoCodeValidator {

	public void validate(CreatePromoCodeRequest createPromoCodeRequest) {
		String codeCannotBeNullValidationMessage = "Code cannot be empty";
		String ownerCannotBeNullValidationMessage = "Owner cannot be empty";
		String discountPercentageCannotBeNullValidationMessage = "Discount percentage cannot be empty";
		String startTimeCannotBeNullValidationMessage = "Start time cannot be empty";
		String timeCannotBeInvalidValidationMessage = "Start time cannot be after end time";

		if (StringUtils.isBlank(createPromoCodeRequest.code())) {
			throw new GeneralPlatformDomainRuleException(codeCannotBeNullValidationMessage);
		}
		if (StringUtils.isBlank(createPromoCodeRequest.owner())) {
			throw new GeneralPlatformDomainRuleException(ownerCannotBeNullValidationMessage);
		}
		if (ObjectUtils.isEmpty(createPromoCodeRequest.discountPercentage())) {
			throw new GeneralPlatformDomainRuleException(discountPercentageCannotBeNullValidationMessage);
		}
		if (ObjectUtils.isEmpty(createPromoCodeRequest.startTime())) {
			throw new GeneralPlatformDomainRuleException(startTimeCannotBeNullValidationMessage);
		}
		if (createPromoCodeRequest.startTime().isAfter(createPromoCodeRequest.endTime())) {
			throw new GeneralPlatformDomainRuleException(timeCannotBeInvalidValidationMessage);
		}
	}
}
