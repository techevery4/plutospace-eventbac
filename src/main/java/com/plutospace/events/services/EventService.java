/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.services;

import java.util.List;

import com.plutospace.events.commons.data.CustomPageResponse;
import com.plutospace.events.domain.data.request.*;
import com.plutospace.events.domain.data.response.EventFormResponse;
import com.plutospace.events.domain.data.response.EventResponse;
import com.plutospace.events.domain.data.response.OperationalResponse;

public interface EventService {

	EventResponse createEvent(CreateEventRequest createEventRequest, String accountId);

	CustomPageResponse<EventResponse> retrieveEvents(int pageNo, int pageSize);

	CustomPageResponse<EventResponse> retrieveEventsForAccount(String accountId, int pageNo, int pageSize);

	CustomPageResponse<EventFormResponse> retrieveEventForms(String id, int pageNo, int pageSize);

	List<EventResponse> retrieveEvent(List<String> ids);

	List<EventResponse> retrieveUpcomingEventsBetween(String accountId, String accountUserId, Long startTime,
			Long endTime);

	EventResponse retrieveEvent(String id);

	CustomPageResponse<EventResponse> searchEvent(String accountId, String text, int pageNo, int pageSize);

	OperationalResponse updateEventWithPoll(String id, String pollPublicId);

	OperationalResponse removePollFromEvent(String id);

	EventResponse retrieveEventByForeignPublicId(String publicId, int type);

	EventResponse updateEvent(String id, String accountId, UpdateEventRequest updateEventRequest);

	EventResponse updateEventTime(String id, String accountId, UpdateEventTimeRequest updateEventTimeRequest);

	EventResponse updateEventForm(String id, String accountId, UpdateEventFormRequest updateEventFormRequest);

	EventResponse updateEventLocation(String id, String accountId,
			UpdateEventLocationRequest updateEventLocationRequest);

	EventResponse updateEventBasicSettings(String id, String accountId,
			UpdateEventBasicSettingsRequest updateEventBasicSettingsRequest);

	EventResponse updateEventPaymentDetails(String id, String accountId,
			UpdateEventPaymentSettingsRequest updateEventPaymentSettingsRequest);
}
