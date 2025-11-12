/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.services;

import java.util.List;

public interface CalendarService {

	List<Object> retrieveCalendarBookingsBetween(String accountId, Long startTime, Long endTime);
}
