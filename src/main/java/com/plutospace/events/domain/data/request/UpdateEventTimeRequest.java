/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.domain.data.request;

import java.time.LocalDate;

public record UpdateEventTimeRequest(LocalDate date, Long startTime, Long endTime, Integer timezoneValue,
		String timezoneString) {
}
