/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.domain.data;

import com.plutospace.events.commons.exception.GeneralPlatformDomainRuleException;

public enum EventType {
	CLOSED_ENDED, OPEN_ENDED, UNLIMITED;

	public static EventType fromValue(String value) {
		for (EventType eventType : EventType.values()) {
			if (eventType.toString().equalsIgnoreCase(value)) {
				return eventType;
			}
		}
		throw new GeneralPlatformDomainRuleException("Invalid event type: " + value);
	}
}
