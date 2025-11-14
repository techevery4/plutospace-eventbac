/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.services.implementations;

import java.time.LocalDateTime;
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
import com.plutospace.events.commons.definitions.GeneralConstants;
import com.plutospace.events.commons.definitions.PropertyConstants;
import com.plutospace.events.commons.exception.GeneralPlatformDomainRuleException;
import com.plutospace.events.commons.exception.ResourceNotFoundException;
import com.plutospace.events.commons.utils.DateConverter;
import com.plutospace.events.commons.utils.LinkGenerator;
import com.plutospace.events.domain.data.LocationType;
import com.plutospace.events.domain.data.request.CreateEventFormRequest;
import com.plutospace.events.domain.data.request.CreateEventRequest;
import com.plutospace.events.domain.data.request.CreateMeetingRequest;
import com.plutospace.events.domain.data.response.EventCategoryResponse;
import com.plutospace.events.domain.data.response.EventFormResponse;
import com.plutospace.events.domain.data.response.EventResponse;
import com.plutospace.events.domain.data.response.MeetingResponse;
import com.plutospace.events.domain.entities.Event;
import com.plutospace.events.domain.entities.EventForm;
import com.plutospace.events.domain.repositories.EventFormRepository;
import com.plutospace.events.domain.repositories.EventRepository;
import com.plutospace.events.services.EventCategoryService;
import com.plutospace.events.services.EventService;
import com.plutospace.events.services.MeetingService;
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
	private final MeetingService meetingService;
	private final EventMapper eventMapper;
	private final EventFormMapper eventFormMapper;
	private final EventValidator eventValidator;
	private final EventFormValidator eventFormValidator;
	private final LinkGenerator linkGenerator;
	private final PropertyConstants propertyConstants;
	private final DateConverter dateConverter;

	@Transactional
	@Override
	public EventResponse createEvent(CreateEventRequest createEventRequest, String accountId) {
		eventValidator.validate(createEventRequest);

		List<EventCategoryResponse> eventCategoryResponses = eventCategoryService
				.retrieveEventCategory(List.of(createEventRequest.categoryId()));
		if (eventCategoryResponses.isEmpty())
			throw new ResourceNotFoundException("Event Category Not Found");
		if (createEventRequest.enableRegistration()
				&& (createEventRequest.eventFormRequests() == null || createEventRequest.eventFormRequests().isEmpty()))
			throw new GeneralPlatformDomainRuleException(
					"Kindly customize your registration form for this event. It requires registration");

		Event event = eventMapper.toEntity(createEventRequest);
		event.setAccountId(accountId);

		try {
			Event savedEvent = eventRepository.save(event);
			if (event.getEnableRegistration()) {
				String registrationLink = linkGenerator.generatePublicLink(savedEvent.getId(),
						savedEvent.getAccountId(), GeneralConstants.EVENT_REGISTRATION,
						propertyConstants.getEventsEncryptionSecretKey());
				savedEvent.setRegistrationLink(registrationLink);
				eventRepository.save(savedEvent);

				createEventForms(createEventRequest.eventFormRequests(), savedEvent.getId());
			}
			if (!event.getLocationType().equals(LocationType.PHYSICAL)) {
				LocalDateTime startTime = dateConverter.convertTimestamp(event.getStartTime());
				LocalDateTime endTime = dateConverter.convertTimestamp(event.getEndTime());
				CreateMeetingRequest createMeetingRequest = new CreateMeetingRequest("Meeting for " + event.getName(),
						event.getDescription(), event.getDate(), event.getDate(),
						dateConverter.convertLocalDateTimeToTimestring(startTime),
						dateConverter.convertLocalDateTimeToTimestring(endTime), createEventRequest.timezoneValue(),
						createEventRequest.timezoneString(), false, null, GeneralConstants.EVENT_MEETING_PARTICIPANTS,
						true, true);
				MeetingResponse meetingResponse = meetingService.createMeeting(createMeetingRequest, accountId);
				savedEvent.setMeetingLink(meetingResponse.getPublicId());
				eventRepository.save(savedEvent);
			}

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
	public CustomPageResponse<EventResponse> retrieveEventsForAccount(String accountId, int pageNo, int pageSize) {
		Pageable pageable = PageRequest.of(pageNo, pageSize);

		Page<Event> events = eventRepository.findByAccountIdOrderByCreatedOnDesc(accountId, pageable);
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

	@Override
	public List<EventResponse> retrieveEventsBetween(String accountId, Long startTime, Long endTime) {
		LocalDateTime startDate = dateConverter.convertTimestamp(startTime);
		LocalDateTime endDate = dateConverter.convertTimestamp(endTime);

		return eventRepository.findByAccountIdAndCreatedOnBetweenOrderByCreatedOnDesc(accountId, startDate, endDate);
	}

	@Override
	public EventResponse retrieveEvent(String id) {
		Event existingEvent = retrieveEventById(id);
		List<EventCategoryResponse> eventCategoryResponses = eventCategoryService
				.retrieveEventCategory(List.of(existingEvent.getCategoryId()));
		if (eventCategoryResponses.isEmpty())
			throw new ResourceNotFoundException("Event Category Not Found");

		return eventMapper.toResponse(existingEvent, eventCategoryResponses.get(0));
	}

	private Event retrieveEventById(String id) {
		return eventRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Event Not Found"));
	}
}
