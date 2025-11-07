/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.domain.data;

import com.plutospace.events.commons.exception.GeneralPlatformDomainRuleException;

public enum LocationType {
	VIRTUAL, HYBRID, PHYSICAL;

	public static LocationType fromValue(String value) {
		for (LocationType locationType : LocationType.values()) {
			if (locationType.toString().equalsIgnoreCase(value)) {
				return locationType;
			}
		}
		throw new GeneralPlatformDomainRuleException("Invalid location type: " + value);
	}
}
