/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.domain.data.request;

import java.time.LocalDate;

public record UpdateMeetingTimeRequest(LocalDate startDate, String startTime, String endTime, Integer timezoneValue,
		String timezoneString) {
}
