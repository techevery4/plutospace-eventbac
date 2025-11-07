/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.services.mappers;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import com.plutospace.events.commons.data.CustomPageResponse;
import com.plutospace.events.domain.data.request.CreateEventCategoryRequest;
import com.plutospace.events.domain.data.response.EventCategoryResponse;
import com.plutospace.events.domain.entities.EventCategory;

@Component
public class EventCategoryMapper {

	public EventCategoryResponse toResponse(EventCategory eventCategory) {
		return EventCategoryResponse.instance(eventCategory.getId(), eventCategory.getName(),
				eventCategory.getDescription(), eventCategory.getCreatedOn());
	}

	public EventCategory toEntity(CreateEventCategoryRequest createEventCategoryRequest) {
		return EventCategory.instance(createEventCategoryRequest.name(), createEventCategoryRequest.description());
	}

	public CustomPageResponse<EventCategoryResponse> toPagedResponse(Page<EventCategory> eventCategories) {
		List<EventCategoryResponse> eventCategoryResponses = eventCategories.getContent().stream().map(this::toResponse)
				.toList();
		long totalElements = eventCategories.getTotalElements();
		Pageable pageable = eventCategories.getPageable();
		return CustomPageResponse.resolvePageResponse(eventCategoryResponses, totalElements, pageable);
	}
}
