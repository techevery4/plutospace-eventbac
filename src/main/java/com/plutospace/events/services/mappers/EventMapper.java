/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.services.mappers;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import com.plutospace.events.commons.data.CustomPageResponse;
import com.plutospace.events.domain.data.EventType;
import com.plutospace.events.domain.data.LocationType;
import com.plutospace.events.domain.data.VisibilityType;
import com.plutospace.events.domain.data.request.CreateEventRequest;
import com.plutospace.events.domain.data.response.EventCategoryResponse;
import com.plutospace.events.domain.data.response.EventResponse;
import com.plutospace.events.domain.entities.Event;

@Component
public class EventMapper {

	public EventResponse toResponse(Event event, EventCategoryResponse eventCategoryResponse) {
		return EventResponse.instance(event.getId(), event.getName(), event.getAccountId(), event.getType(),
				event.getCategoryId(), eventCategoryResponse, event.getDescription(), event.getDate(),
				event.getStartTime(), event.getEndTime(), event.getTimezone(), event.getLocationType(),
				event.getVirtualRoomName(), event.getPhysicalAddress(), event.getAdditionalInstructions(),
				event.getVisibilityType(), event.getRequireApproval(), event.getEnableRegistration(),
				event.getEnableWaitlist(), event.getAttendeeSize(), event.getRegistrationCutOffTime(),
				event.getIsPaidEvent(), event.getAmount(), event.getCurrency(), event.getConfirmationMessage(),
				event.getTermsAndConditions(), event.getSendReminder(), event.getReminderHour(), event.getLogo(),
				event.getThumbnail(), event.getMeetingLink(), event.getQAndALink(), event.getPollsLink(),
				event.getRegistrationLink(), event.getCreatedOn());
	}

	public Event toEntity(CreateEventRequest createEventRequest) {
		LocationType locationType = LocationType.fromValue(createEventRequest.locationType());
		VisibilityType visibilityType = VisibilityType.fromValue(createEventRequest.visibilityType());
		EventType eventType = EventType.fromValue(createEventRequest.type());

		Event.Timezone timezone = new Event.Timezone();
		timezone.setRepresentation(createEventRequest.timezoneString());
		timezone.setValue(createEventRequest.timezoneValue());

		Event.PhysicalAddress physicalAddress = new Event.PhysicalAddress();
		physicalAddress.setStreet(createEventRequest.street());
		physicalAddress.setCity(createEventRequest.city());
		physicalAddress.setState(createEventRequest.state());
		physicalAddress.setCountry(createEventRequest.country());

		return Event.instance(createEventRequest.name(), null, eventType, createEventRequest.categoryId(),
				createEventRequest.description(), createEventRequest.date(), createEventRequest.startTime(),
				createEventRequest.endTime(), timezone, locationType, createEventRequest.virtualRoomName(),
				physicalAddress, createEventRequest.additionalInstructions(), visibilityType,
				createEventRequest.requireApproval(), createEventRequest.enableRegistration(),
				createEventRequest.enableWaitlist(), createEventRequest.attendeeSize(),
				createEventRequest.registrationCutOffTime(), createEventRequest.isPaidEvent(),
				createEventRequest.amount(), createEventRequest.currency(), createEventRequest.confirmationMessage(),
				createEventRequest.termsAndConditions(), createEventRequest.sendReminder(),
				createEventRequest.reminderHour(), createEventRequest.logo(), createEventRequest.thumbnail(), null,
				null, null, null);
	}

	public CustomPageResponse<EventResponse> toPagedResponse(Page<Event> events,
			Map<String, EventCategoryResponse> eventCategoryResponseMap) {
		List<EventResponse> eventResponses = events.getContent().stream().map(event -> {
			EventCategoryResponse eventCategoryResponse = eventCategoryResponseMap.get(event.getCategoryId());
			return toResponse(event, eventCategoryResponse);
		}).toList();
		long totalElements = events.getTotalElements();
		Pageable pageable = events.getPageable();
		return CustomPageResponse.resolvePageResponse(eventResponses, totalElements, pageable);
	}
}
