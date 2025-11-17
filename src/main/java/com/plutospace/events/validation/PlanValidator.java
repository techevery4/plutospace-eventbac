/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.validation;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.plutospace.events.commons.exception.GeneralPlatformDomainRuleException;
import com.plutospace.events.domain.data.request.PlanRequest;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class PlanValidator {

	public void validate(PlanRequest request) {
		String typeCannotBeNullValidationMessage = "Plan type cannot be empty";
		String priceNairaCannotBeNullValidationMessage = "Price in Naira cannot be empty";
		String priceUsdCannotBeNullValidationMessage = "Price in USD cannot be empty";
		String nameCannotBeNullValidationMessage = "Name cannot be empty";

		if (StringUtils.isBlank(request.getType())) {
			throw new GeneralPlatformDomainRuleException(typeCannotBeNullValidationMessage);
		}
		if (StringUtils.isBlank(request.getName())) {
			throw new GeneralPlatformDomainRuleException(nameCannotBeNullValidationMessage);
		}
		if (request.getPriceNaira() <= 0) {
			throw new GeneralPlatformDomainRuleException(priceNairaCannotBeNullValidationMessage);
		}
		if (request.getPriceUsd() <= 0) {
			throw new GeneralPlatformDomainRuleException(priceUsdCannotBeNullValidationMessage);
		}
	}
}
