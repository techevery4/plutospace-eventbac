/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.domain.data;

import com.plutospace.events.commons.exception.GeneralPlatformDomainRuleException;

public enum VisibilityType {
	INVITATION_ONLY, PUBLIC, PRIVATE;

	public static VisibilityType fromValue(String value) {
		for (VisibilityType visibilityType : VisibilityType.values()) {
			if (visibilityType.toString().equalsIgnoreCase(value)) {
				return visibilityType;
			}
		}
		throw new GeneralPlatformDomainRuleException("Invalid visibility type: " + value);
	}
}
