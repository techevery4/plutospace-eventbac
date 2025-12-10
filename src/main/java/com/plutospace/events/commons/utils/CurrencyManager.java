/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.commons.utils;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.plutospace.events.commons.exception.GeneralPlatformDomainRuleException;

@Component
public class CurrencyManager {

	public void checkCurrency(String currency) {
		if (StringUtils.isBlank(currency))
			throw new GeneralPlatformDomainRuleException("Currency is required");

		if (!currency.equalsIgnoreCase("NGN") && !currency.equalsIgnoreCase("USD"))
			throw new GeneralPlatformDomainRuleException("Currency is invalid");
	}
}
