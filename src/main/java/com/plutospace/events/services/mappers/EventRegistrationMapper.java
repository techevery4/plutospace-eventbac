/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.services.mappers;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import com.plutospace.events.commons.data.CustomPageResponse;
import com.plutospace.events.domain.data.EventRegistrationStatus;
import com.plutospace.events.domain.data.request.CreateEventRegistrationDataRequest;
import com.plutospace.events.domain.data.request.CreateEventRegistrationRequest;
import com.plutospace.events.domain.data.response.*;
import com.plutospace.events.domain.entities.EventRegistration;
import com.plutospace.events.domain.entities.EventRegistrationData;
import com.plutospace.events.domain.entities.EventRegistrationLog;

@Component
public class EventRegistrationMapper {

	public EventRegistrationResponse toResponse(EventRegistration eventRegistration,
			Map<String, EventRegistrationData> eventRegistrationDataMap, List<EventFormResponse> eventFormResponses) {
		List<EventRegistrationResponse.EventRegistrationDataResponse> eventRegistrationDataResponses = new ArrayList<>();
		for (EventFormResponse eventFormResponse : eventFormResponses) {
			String answer = null;;
			EventRegistrationData eventRegistrationData = eventRegistrationDataMap.get(eventFormResponse.getId());
			if (eventRegistrationData != null)
				answer = eventRegistrationData.getResponse();

			EventRegistrationResponse.EventRegistrationDataResponse eventRegistrationDataResponse = new EventRegistrationResponse.EventRegistrationDataResponse();
			eventRegistrationDataResponse.setResponse(answer);
			eventRegistrationDataResponse.setEventFormResponse(eventFormResponse);
			eventRegistrationDataResponse.setFormId(eventFormResponse.getId());
			eventRegistrationDataResponses.add(eventRegistrationDataResponse);
		}

		return EventRegistrationResponse.instance(eventRegistration.getId(), eventRegistration.getEmail(),
				eventRegistration.getEventId(), eventRegistration.getEventDate(),
				eventRegistration.getEventRegistrationStatus(), eventRegistrationDataResponses,
				eventRegistration.getCreatedOn());
	}

	public EventRegistrationLogResponse toResponse(EventRegistrationLog eventRegistrationLog,
			AccountUserResponse accountUserResponse) {
		return EventRegistrationLogResponse.instance(eventRegistrationLog.getId(),
				eventRegistrationLog.getRegistrationId(), eventRegistrationLog.getPreviousState(),
				eventRegistrationLog.getCurrentState(), eventRegistrationLog.getAdditionalInformation(),
				eventRegistrationLog.getCreatedBy(), accountUserResponse, eventRegistrationLog.getCreatedOn());
	}

	public EventRegistration toEntity(CreateEventRegistrationRequest createEventRegistrationRequest,
			LocalDate eventDate) {
		return EventRegistration.instance(createEventRegistrationRequest.email(),
				createEventRegistrationRequest.eventId(), eventDate, EventRegistrationStatus.PENDING);
	}

	public EventRegistrationData toEntity(CreateEventRegistrationDataRequest createEventRegistrationDataRequest,
			String email, String eventId) {
		return EventRegistrationData.instance(email, eventId, createEventRegistrationDataRequest.formId(),
				createEventRegistrationDataRequest.response());
	}

	public CustomPageResponse<EventRegistrationResponse> toPagedResponse(Page<EventRegistration> eventRegistrations,
			Map<String, Map<String, EventRegistrationData>> eventRegistrationDataMaps,
			List<EventFormResponse> eventFormResponses) {
		List<EventRegistrationResponse> eventRegistrationResponses = eventRegistrations.getContent().stream()
				.map(registration -> {
					Map<String, EventRegistrationData> eventRegistrationDataMap = eventRegistrationDataMaps
							.get(registration.getEmail());
					return toResponse(registration, eventRegistrationDataMap, eventFormResponses);
				}).toList();
		long totalElements = eventRegistrations.getTotalElements();
		Pageable pageable = eventRegistrations.getPageable();
		return CustomPageResponse.resolvePageResponse(eventRegistrationResponses, totalElements, pageable);
	}
}
