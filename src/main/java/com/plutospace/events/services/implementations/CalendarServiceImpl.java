/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.services.implementations;

import org.springframework.stereotype.Service;

import com.plutospace.events.commons.utils.DateConverter;
import com.plutospace.events.domain.data.response.CalendarResponse;
import com.plutospace.events.services.CalendarService;
import com.plutospace.events.services.EventService;
import com.plutospace.events.services.MeetingService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class CalendarServiceImpl implements CalendarService {

	private final EventService eventService;
	private final MeetingService meetingService;
	private final DateConverter dateConverter;

	@Override
	public CalendarResponse retrieveCalendarBookingsBetween(String accountId, Long startTime, Long endTime) {
		return CalendarResponse.instance(meetingService.retrieveMeetingsBetween(accountId, startTime, endTime),
				eventService.retrieveEventsBetween(accountId, startTime, endTime));
	}
}
