/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.services.implementations;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;

import com.plutospace.events.domain.data.response.EventResponse;
import com.plutospace.events.domain.data.response.MeetingResponse;
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

	@Override
	public List<Object> retrieveCalendarBookingsBetween(String accountId, Long startTime, Long endTime) {
		List<MeetingResponse> meetingResponses = meetingService.retrieveMeetingsBetween(accountId, startTime, endTime);
		List<EventResponse> eventResponses = eventService.retrieveEventsBetween(accountId, startTime, endTime);

		// Combine and sort by createdOn descending
		return Stream.concat(meetingResponses.stream(), eventResponses.stream())
				.sorted(Comparator.comparing(this::getCreatedOn, Comparator.reverseOrder()))
				.collect(Collectors.toList());
	}

	private LocalDateTime getCreatedOn(Object obj) {
		if (obj instanceof MeetingResponse) {
			return ((MeetingResponse) obj).getCreatedOn();
		} else if (obj instanceof EventResponse) {
			return ((EventResponse) obj).getCreatedOn();
		}
		return LocalDateTime.now();
	}
}
