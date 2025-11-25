/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.services;

import java.util.List;

import com.plutospace.events.commons.data.CustomPageResponse;
import com.plutospace.events.domain.data.request.CreateEventRequest;
import com.plutospace.events.domain.data.response.EventFormResponse;
import com.plutospace.events.domain.data.response.EventResponse;

public interface EventService {

	EventResponse createEvent(CreateEventRequest createEventRequest, String accountId);

	CustomPageResponse<EventResponse> retrieveEvents(int pageNo, int pageSize);

	CustomPageResponse<EventResponse> retrieveEventsForAccount(String accountId, int pageNo, int pageSize);

	CustomPageResponse<EventFormResponse> retrieveEventForms(String id, int pageNo, int pageSize);

	List<EventResponse> retrieveEvent(List<String> ids);

	List<EventResponse> retrieveEventsBetween(String accountId, Long startTime, Long endTime);

	EventResponse retrieveEvent(String id);

	CustomPageResponse<EventResponse> searchEvent(String accountId, String text, int pageNo, int pageSize);
}
