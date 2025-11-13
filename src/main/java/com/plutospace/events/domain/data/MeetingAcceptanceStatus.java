/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.domain.data;

import com.plutospace.events.commons.exception.GeneralPlatformDomainRuleException;

public enum MeetingAcceptanceStatus {
	PENDING, GOING, NOT_GOING, UNSURE;

	public static MeetingAcceptanceStatus fromValue(String value) {
		for (MeetingAcceptanceStatus meetingAcceptanceStatus : MeetingAcceptanceStatus.values()) {
			if (meetingAcceptanceStatus.toString().equalsIgnoreCase(value)) {
				return meetingAcceptanceStatus;
			}
		}
		throw new GeneralPlatformDomainRuleException("Invalid meeting acceptance status: " + value);
	}
}
