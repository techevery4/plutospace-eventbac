/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.domain.data;

import com.plutospace.events.commons.exception.GeneralPlatformDomainRuleException;

public enum FormType {
	TEXTFIELD, TEXTAREA, SINGLE_SELECT, MULTI_SELECT, RADIO, FILE, EMAIL;

	public static FormType fromValue(String value) {
		for (FormType formType : FormType.values()) {
			if (formType.toString().equalsIgnoreCase(value)) {
				return formType;
			}
		}
		throw new GeneralPlatformDomainRuleException("Invalid form type: " + value);
	}
}
