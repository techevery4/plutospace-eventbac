/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.services;

import java.util.List;

import com.plutospace.events.commons.data.CustomPageResponse;
import com.plutospace.events.domain.data.request.CreateEventCategoryRequest;
import com.plutospace.events.domain.data.request.UpdateEventCategoryRequest;
import com.plutospace.events.domain.data.response.EventCategoryResponse;

public interface EventCategoryService {

	EventCategoryResponse createEventCategory(CreateEventCategoryRequest createEventCategoryRequest);

	EventCategoryResponse updateEventCategory(String id, UpdateEventCategoryRequest updateEventCategoryRequest);

	CustomPageResponse<EventCategoryResponse> retrieveEventCategories(int pageNo, int pageSize);

	List<EventCategoryResponse> retrieveAllEventCategories();

	List<EventCategoryResponse> retrieveEventCategory(List<String> ids);
}
