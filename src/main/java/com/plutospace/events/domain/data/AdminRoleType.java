/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.domain.data;

import com.plutospace.events.commons.exception.GeneralPlatformDomainRuleException;

public enum AdminRoleType {
	SUPER_ADMIN, ADMIN, MANAGER;

	public static AdminRoleType fromValue(String value) {
		for (AdminRoleType adminRoleType : AdminRoleType.values()) {
			if (adminRoleType.toString().equalsIgnoreCase(value)) {
				return adminRoleType;
			}
		}
		throw new GeneralPlatformDomainRuleException("Invalid admin role type: " + value);
	}
}
