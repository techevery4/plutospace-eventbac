/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.services;

import com.plutospace.events.domain.data.response.CalendarResponse;

public interface CalendarService {

	CalendarResponse retrieveCalendarBookingsBetween(String accountId, Long startTime, Long endTime);
}
