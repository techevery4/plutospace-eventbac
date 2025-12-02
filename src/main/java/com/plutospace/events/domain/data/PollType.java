/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.domain.data;

import com.plutospace.events.commons.exception.GeneralPlatformDomainRuleException;

public enum PollType {
	SINGLE_ANSWER, MULTI_ANSWER;

	public static PollType fromValue(String value) {
		for (PollType pollType : PollType.values()) {
			if (pollType.toString().equalsIgnoreCase(value)) {
				return pollType;
			}
		}
		throw new GeneralPlatformDomainRuleException("Invalid poll type: " + value);
	}
}
