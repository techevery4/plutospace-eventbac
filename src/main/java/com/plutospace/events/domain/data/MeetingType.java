/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.domain.data;

import com.plutospace.events.commons.exception.GeneralPlatformDomainRuleException;

public enum MeetingType {
	INSTANT, SCHEDULED;

	public static MeetingType fromValue(String value) {
		for (MeetingType meetingType : MeetingType.values()) {
			if (meetingType.toString().equalsIgnoreCase(value)) {
				return meetingType;
			}
		}
		throw new GeneralPlatformDomainRuleException("Invalid meeting type: " + value);
	}
}
