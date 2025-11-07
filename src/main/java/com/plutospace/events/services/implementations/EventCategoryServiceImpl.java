/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.services.implementations;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.plutospace.events.commons.data.CustomPageResponse;
import com.plutospace.events.commons.exception.ResourceAlreadyExistsException;
import com.plutospace.events.commons.exception.ResourceNotFoundException;
import com.plutospace.events.domain.data.request.CreateEventCategoryRequest;
import com.plutospace.events.domain.data.request.UpdateEventCategoryRequest;
import com.plutospace.events.domain.data.response.EventCategoryResponse;
import com.plutospace.events.domain.entities.EventCategory;
import com.plutospace.events.domain.repositories.EventCategoryRepository;
import com.plutospace.events.services.EventCategoryService;
import com.plutospace.events.services.mappers.EventCategoryMapper;
import com.plutospace.events.validation.EventCategoryValidator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventCategoryServiceImpl implements EventCategoryService {

	private final EventCategoryRepository eventCategoryRepository;
	private final EventCategoryMapper eventCategoryMapper;
	private final EventCategoryValidator eventCategoryValidator;

	@Override
	public EventCategoryResponse createEventCategory(CreateEventCategoryRequest createEventCategoryRequest) {
		eventCategoryValidator.validate(createEventCategoryRequest);

		EventCategory eventCategory = eventCategoryMapper.toEntity(createEventCategoryRequest);
		if (eventCategoryRepository.existsByNameIgnoreCase(createEventCategoryRequest.name()))
			throw new ResourceAlreadyExistsException("Event category already exists");

		try {
			EventCategory savedEventCategory = eventCategoryRepository.save(eventCategory);

			return eventCategoryMapper.toResponse(savedEventCategory);
		} catch (DataIntegrityViolationException e) {
			throw new DataIntegrityViolationException(e.getLocalizedMessage());
		}
	}

	@Override
	public EventCategoryResponse updateEventCategory(String id, UpdateEventCategoryRequest updateEventCategoryRequest) {
		EventCategory existingEventCategory = retrieveEventCategoryById(id);

		if (StringUtils.isNotBlank(updateEventCategoryRequest.name())) {
			EventCategory checkEventCategory = eventCategoryRepository
					.findByNameIgnoreCase(updateEventCategoryRequest.name());
			if (!id.equals(checkEventCategory.getId()))
				throw new ResourceAlreadyExistsException("Event category already exists");

			existingEventCategory.setName(updateEventCategoryRequest.name());
		}
		if (StringUtils.isNotBlank(updateEventCategoryRequest.description()))
			existingEventCategory.setDescription(updateEventCategoryRequest.description());

		try {
			EventCategory savedEventCategory = eventCategoryRepository.save(existingEventCategory);

			return eventCategoryMapper.toResponse(savedEventCategory);
		} catch (DataIntegrityViolationException e) {
			throw new DataIntegrityViolationException(e.getLocalizedMessage());
		}
	}

	@Override
	public CustomPageResponse<EventCategoryResponse> retrieveEventCategories(int pageNo, int pageSize) {
		Pageable pageable = PageRequest.of(pageNo, pageSize);

		Page<EventCategory> eventCategories = eventCategoryRepository.findAll(pageable);

		return eventCategoryMapper.toPagedResponse(eventCategories);
	}

	@Override
	public List<EventCategoryResponse> retrieveAllEventCategories() {
		List<EventCategory> eventCategories = eventCategoryRepository.findAll();
		if (eventCategories.isEmpty())
			return new ArrayList<>();

		return eventCategories.stream().map(eventCategoryMapper::toResponse).toList();
	}

	@Override
	public List<EventCategoryResponse> retrieveEventCategory(List<String> ids) {
		List<EventCategory> eventCategories = eventCategoryRepository.findByIdIn(ids);
		if (eventCategories.isEmpty())
			return new ArrayList<>();

		return eventCategories.stream().map(eventCategoryMapper::toResponse).toList();
	}

	private EventCategory retrieveEventCategoryById(String id) {
		return eventCategoryRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Event Category Not Found"));
	}
}
