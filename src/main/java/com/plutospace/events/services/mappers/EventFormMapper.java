/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.services.mappers;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import com.plutospace.events.commons.data.CustomPageResponse;
import com.plutospace.events.domain.data.FormType;
import com.plutospace.events.domain.data.request.CreateEventFormRequest;
import com.plutospace.events.domain.data.response.EventFormResponse;
import com.plutospace.events.domain.entities.EventForm;

@Component
public class EventFormMapper {

	public EventFormResponse toResponse(EventForm eventForm) {
		return EventFormResponse.instance(eventForm.getId(), eventForm.getEventId(), eventForm.getTitle(),
				eventForm.getType(), eventForm.getOptions(), eventForm.getIsRequired(), eventForm.getCreatedOn());
	}

	public EventForm toEntity(CreateEventFormRequest createEventFormRequest, String eventId) {
		FormType formType = FormType.fromValue(createEventFormRequest.type());

		return EventForm.instance(eventId, createEventFormRequest.title(), formType, createEventFormRequest.options(),
				createEventFormRequest.isRequired());
	}

	public CustomPageResponse<EventFormResponse> toPagedResponse(Page<EventForm> eventForms) {
		List<EventFormResponse> eventFormResponses = eventForms.getContent().stream().map(this::toResponse).toList();
		long totalElements = eventForms.getTotalElements();
		Pageable pageable = eventForms.getPageable();
		return CustomPageResponse.resolvePageResponse(eventFormResponses, totalElements, pageable);
	}
}
