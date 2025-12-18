/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.domain.data.request;

import java.time.LocalTime;

public record UpdateRecurringMeetingTimeRequest(LocalTime startTime, LocalTime endTime, Integer timezoneValue,
		String timezoneString, Boolean doForAll) {
}
