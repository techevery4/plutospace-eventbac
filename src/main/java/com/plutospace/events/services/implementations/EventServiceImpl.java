/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.services.implementations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.plutospace.events.commons.data.CustomPageResponse;
import com.plutospace.events.commons.exception.ResourceNotFoundException;
import com.plutospace.events.domain.data.request.CreateEventFormRequest;
import com.plutospace.events.domain.data.request.CreateEventRequest;
import com.plutospace.events.domain.data.response.EventCategoryResponse;
import com.plutospace.events.domain.data.response.EventFormResponse;
import com.plutospace.events.domain.data.response.EventResponse;
import com.plutospace.events.domain.entities.Event;
import com.plutospace.events.domain.entities.EventForm;
import com.plutospace.events.domain.repositories.EventFormRepository;
import com.plutospace.events.domain.repositories.EventRepository;
import com.plutospace.events.services.EventCategoryService;
import com.plutospace.events.services.EventService;
import com.plutospace.events.services.mappers.EventFormMapper;
import com.plutospace.events.services.mappers.EventMapper;
import com.plutospace.events.validation.EventFormValidator;
import com.plutospace.events.validation.EventValidator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

	private final EventRepository eventRepository;
	private final EventFormRepository eventFormRepository;
	private final EventCategoryService eventCategoryService;
	private final EventMapper eventMapper;
	private final EventFormMapper eventFormMapper;
	private final EventValidator eventValidator;
	private final EventFormValidator eventFormValidator;

	@Transactional
	@Override
	public EventResponse createEvent(CreateEventRequest createEventRequest) {
		eventValidator.validate(createEventRequest);

		List<EventCategoryResponse> eventCategoryResponses = eventCategoryService
				.retrieveEventCategory(List.of(createEventRequest.categoryId()));
		if (eventCategoryResponses.isEmpty())
			throw new ResourceNotFoundException("Event Category Not Found");

		Event event = eventMapper.toEntity(createEventRequest);

		try {
			Event savedEvent = eventRepository.save(event);
			createEventForms(createEventRequest.eventFormRequests(), savedEvent.getId());

			return eventMapper.toResponse(savedEvent, eventCategoryResponses.get(0));
		} catch (DataIntegrityViolationException e) {
			throw new DataIntegrityViolationException(e.getLocalizedMessage());
		}
	}

	private void createEventForms(List<CreateEventFormRequest> createEventFormRequests, String eventId) {
		List<EventForm> eventForms = new ArrayList<>();
		for (CreateEventFormRequest createEventFormRequest : createEventFormRequests) {
			eventFormValidator.validate(createEventFormRequest);

			EventForm eventForm = eventFormMapper.toEntity(createEventFormRequest, eventId);
			eventForms.add(eventForm);
		}

		try {
			eventFormRepository.saveAll(eventForms);
		} catch (DataIntegrityViolationException e) {
			throw new DataIntegrityViolationException(e.getLocalizedMessage());
		}
	}

	@Override
	public CustomPageResponse<EventResponse> retrieveEvents(int pageNo, int pageSize) {
		Pageable pageable = PageRequest.of(pageNo, pageSize);

		Page<Event> events = eventRepository.findAll(pageable);
		if (events.getTotalElements() == 0)
			return new CustomPageResponse<>();

		List<String> categoryIds = events.getContent().stream().map(Event::getCategoryId).toList();
		List<EventCategoryResponse> eventCategoryResponses = eventCategoryService.retrieveEventCategory(categoryIds);
		Map<String, EventCategoryResponse> eventCategoryResponseMap = new HashMap<>();
		for (EventCategoryResponse eventCategoryResponse : eventCategoryResponses) {
			eventCategoryResponseMap.putIfAbsent(eventCategoryResponse.getId(), eventCategoryResponse);
		}

		return eventMapper.toPagedResponse(events, eventCategoryResponseMap);
	}

	@Override
	public CustomPageResponse<EventFormResponse> retrieveEventForms(String id, int pageNo, int pageSize) {
		Pageable pageable = PageRequest.of(pageNo, pageSize);

		Page<EventForm> eventForms = eventFormRepository.findByEventId(id, pageable);

		return eventFormMapper.toPagedResponse(eventForms);
	}

	@Override
	public List<EventResponse> retrieveEvent(List<String> ids) {
		List<Event> events = eventRepository.findByIdIn(ids);
		if (events.isEmpty())
			return new ArrayList<>();

		List<String> categoryIds = events.stream().map(Event::getCategoryId).toList();
		List<EventCategoryResponse> eventCategoryResponses = eventCategoryService.retrieveEventCategory(categoryIds);
		Map<String, EventCategoryResponse> eventCategoryResponseMap = new HashMap<>();
		for (EventCategoryResponse eventCategoryResponse : eventCategoryResponses) {
			eventCategoryResponseMap.putIfAbsent(eventCategoryResponse.getId(), eventCategoryResponse);
		}

		return events.stream().map(event -> {
			EventCategoryResponse eventCategoryResponse = eventCategoryResponseMap.get(event.getCategoryId());
			return eventMapper.toResponse(event, eventCategoryResponse);
		}).toList();
	}
}
