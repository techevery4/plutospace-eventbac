/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.services.implementations;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
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
import com.plutospace.events.commons.utils.CurrencyManager;
import com.plutospace.events.commons.utils.DateConverter;
import com.plutospace.events.commons.utils.LinkGenerator;
import com.plutospace.events.domain.data.LocationType;
import com.plutospace.events.domain.data.MeetingType;
import com.plutospace.events.domain.data.request.*;
import com.plutospace.events.domain.data.response.*;
import com.plutospace.events.domain.entities.*;
import com.plutospace.events.domain.repositories.EventFormRepository;
import com.plutospace.events.domain.repositories.EventRegistrationRepository;
import com.plutospace.events.domain.repositories.EventRepository;
import com.plutospace.events.intelligence.search.DatabaseSearchService;
import com.plutospace.events.services.AccountUserService;
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
	private final EventRegistrationRepository eventRegistrationRepository;
	private final DatabaseSearchService databaseSearchService;
	private final AccountUserService accountUserService;
	private final EventCategoryService eventCategoryService;
	private final MeetingService meetingService;
	private final EventMapper eventMapper;
	private final EventFormMapper eventFormMapper;
	private final EventValidator eventValidator;
	private final EventFormValidator eventFormValidator;
	private final LinkGenerator linkGenerator;
	private final PropertyConstants propertyConstants;
	private final DateConverter dateConverter;
	private final CurrencyManager currencyManager;

	@Transactional
	@Override
	public EventResponse createEvent(CreateEventRequest createEventRequest, String accountId) {
		eventValidator.validate(createEventRequest, dateConverter.getCurrentTimestamp());

		List<EventCategoryResponse> eventCategoryResponses = eventCategoryService
				.retrieveEventCategory(List.of(createEventRequest.categoryId()));
		if (eventCategoryResponses.isEmpty())
			throw new ResourceNotFoundException("Event Category Not Found");
		if (createEventRequest.enableRegistration()
				&& (createEventRequest.eventFormRequests() == null || createEventRequest.eventFormRequests().isEmpty()))
			throw new GeneralPlatformDomainRuleException(
					"Kindly customize your registration form for this event. It requires registration");
		if (ObjectUtils.isNotEmpty(createEventRequest.isPaidEvent()) && createEventRequest.isPaidEvent())
			currencyManager.checkCurrency(createEventRequest.currency());

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
						MeetingType.SCHEDULED.name(), event.getDescription(), event.getDate(), event.getDate(),
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
	public List<EventResponse> retrieveUpcomingEventsBetween(String accountId, String accountUserId, Long startTime,
			Long endTime) {
		AccountUserResponse accountUserResponse = accountUserService.retrieveAccountUser(accountUserId);
		LocalDate startDate = dateConverter.convertTimestamp(startTime).toLocalDate();
		LocalDate endDate = dateConverter.convertTimestamp(endTime).toLocalDate();

		List<Event> events = eventRepository.findByAccountIdAndCreatedByAndDateBetweenOrderByStartTimeAsc(accountId,
				accountUserId, startDate, endDate);
		List<EventRegistration> eventRegistrations = eventRegistrationRepository
				.findByEmailIgnoreCaseAndEventDateBetweenOrderByEventDateAsc(accountUserResponse.getEmail(), startDate,
						endDate);
		if (!eventRegistrations.isEmpty()) {
			List<String> registrationEventIds = eventRegistrations.stream().map(EventRegistration::getEventId).toList();
			List<Event> registrationEvents = eventRepository.findByIdIn(registrationEventIds);
			events.addAll(registrationEvents);
		}

		events.sort(Comparator.comparing(Event::getStartTime));

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
	public EventResponse retrieveEvent(String id) {
		Event existingEvent = retrieveEventById(id);
		List<EventCategoryResponse> eventCategoryResponses = eventCategoryService
				.retrieveEventCategory(List.of(existingEvent.getCategoryId()));
		if (eventCategoryResponses.isEmpty())
			throw new ResourceNotFoundException("Event Category Not Found");

		return eventMapper.toResponse(existingEvent, eventCategoryResponses.get(0));
	}

	@Override
	public CustomPageResponse<EventResponse> searchEvent(String accountId, String text, int pageNo, int pageSize) {
		Pageable pageable = PageRequest.of(pageNo, pageSize);

		List<String> fields = List.of("name", "description", "timezone.representation", "locationType",
				"virtualRoomName", "physicalAddress.street", "physicalAddress.city", "physicalAddress.state",
				"physicalAddress.country", "additionalInstructions", "visibilityType", "currency",
				"confirmationMessage", "termsAndConditions", "meetingLink", "qAndALink", "pollsLink",
				"registrationLink");
		Page<Event> events = databaseSearchService.findEventByDynamicFilter(accountId, text, fields, pageable);
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
	public OperationalResponse updateEventWithPoll(String id, String pollPublicId) {
		Event existingEvent = retrieveEventById(id);

		existingEvent.setPollsLink(pollPublicId);

		try {
			eventRepository.save(existingEvent);

			return OperationalResponse.instance(GeneralConstants.SUCCESS_MESSAGE);
		} catch (DataIntegrityViolationException e) {
			throw new DataIntegrityViolationException(e.getLocalizedMessage());
		}
	}

	@Override
	public OperationalResponse removePollFromEvent(String id) {
		Event existingEvent = retrieveEventById(id);

		existingEvent.setPollsLink(null);

		try {
			eventRepository.save(existingEvent);

			return OperationalResponse.instance(GeneralConstants.SUCCESS_MESSAGE);
		} catch (DataIntegrityViolationException e) {
			throw new DataIntegrityViolationException(e.getLocalizedMessage());
		}
	}

	@Override
	public EventResponse retrieveEventByForeignPublicId(String publicId, int type) {
		Event event = null;
		if (type == 1) {
			event = eventRepository.findByPollsLink(publicId);
		}

		if (event != null) {
			List<EventCategoryResponse> eventCategoryResponses = eventCategoryService
					.retrieveEventCategory(List.of(event.getCategoryId()));
			if (eventCategoryResponses.isEmpty())
				throw new ResourceNotFoundException("Event Category Not Found");

			return eventMapper.toResponse(event, eventCategoryResponses.get(0));
		}
		return null;
	}

	@Override
	public EventResponse updateEvent(String id, UpdateEventRequest updateEventRequest) {
		Event existingEvent = retrieveEventById(id);

		if (StringUtils.isNotBlank(updateEventRequest.categoryId()))
			existingEvent.setCategoryId(updateEventRequest.categoryId());
		if (StringUtils.isNotBlank(updateEventRequest.name()))
			existingEvent.setName(updateEventRequest.name());
		if (StringUtils.isNotBlank(updateEventRequest.additionalInstructions()))
			existingEvent.setAdditionalInstructions(updateEventRequest.additionalInstructions());
		if (StringUtils.isNotBlank(updateEventRequest.description()))
			existingEvent.setDescription(updateEventRequest.description());

		List<EventCategoryResponse> eventCategoryResponses = eventCategoryService
				.retrieveEventCategory(List.of(existingEvent.getCategoryId()));
		if (eventCategoryResponses.isEmpty())
			throw new ResourceNotFoundException("Event Category Not Found");

		try {
			Event savedEvent = eventRepository.save(existingEvent);

			return eventMapper.toResponse(savedEvent, eventCategoryResponses.get(0));
		} catch (DataIntegrityViolationException e) {
			throw new DataIntegrityViolationException(e.getLocalizedMessage());
		}
	}

	@Override
	public EventResponse updateEventTime(String id, UpdateEventTimeRequest updateEventTimeRequest) {
		Event existingEvent = retrieveEventById(id);

		if (StringUtils.isNotBlank(updateEventTimeRequest.timezoneString())
				|| ObjectUtils.isNotEmpty(updateEventTimeRequest.timezoneValue())) {
			if (StringUtils.isBlank(updateEventTimeRequest.timezoneString())
					&& ObjectUtils.isEmpty(updateEventTimeRequest.timezoneValue()))
				throw new GeneralPlatformDomainRuleException("Kindly update the timezone properly");

			Event.Timezone timezone = new Event.Timezone();
			timezone.setRepresentation(updateEventTimeRequest.timezoneString());
			timezone.setValue(updateEventTimeRequest.timezoneValue());
			existingEvent.setTimezone(timezone);
		}
		if (ObjectUtils.isNotEmpty(updateEventTimeRequest.date())) {
			if (updateEventTimeRequest.date().isBefore(LocalDate.now())) {
				throw new GeneralPlatformDomainRuleException("Date cannot be invalid");
			}
			existingEvent.setDate(updateEventTimeRequest.date());
		}
		if (ObjectUtils.isNotEmpty(updateEventTimeRequest.startTime())
				|| ObjectUtils.isNotEmpty(updateEventTimeRequest.endTime())) {
			existingEvent.setStartTime(ObjectUtils.isNotEmpty(updateEventTimeRequest.startTime())
					? updateEventTimeRequest.startTime()
					: existingEvent.getStartTime());
			existingEvent.setEndTime(ObjectUtils.isNotEmpty(updateEventTimeRequest.endTime())
					? updateEventTimeRequest.endTime()
					: existingEvent.getEndTime());
			if (ObjectUtils.isEmpty(existingEvent.getStartTime())
					|| existingEvent.getStartTime() < dateConverter.getCurrentTimestamp()) {
				throw new GeneralPlatformDomainRuleException("Start time cannot be empty");
			}
			if (ObjectUtils.isNotEmpty(existingEvent.getEndTime())
					&& existingEvent.getStartTime() >= existingEvent.getEndTime()) {
				throw new GeneralPlatformDomainRuleException("Start time cannot be greater than end time");
			}
		}

		List<EventCategoryResponse> eventCategoryResponses = eventCategoryService
				.retrieveEventCategory(List.of(existingEvent.getCategoryId()));
		if (eventCategoryResponses.isEmpty())
			throw new ResourceNotFoundException("Event Category Not Found");

		try {
			Event savedEvent = eventRepository.save(existingEvent);
			if (ObjectUtils.isNotEmpty(updateEventTimeRequest.date())) {
				eventRegistrationRepository.updateEventDateByEventId(savedEvent.getId(), updateEventTimeRequest.date());
			}

			return eventMapper.toResponse(savedEvent, eventCategoryResponses.get(0));
		} catch (DataIntegrityViolationException e) {
			throw new DataIntegrityViolationException(e.getLocalizedMessage());
		}
	}

	@Override
	public EventResponse updateEventForm(String id, UpdateEventFormRequest updateEventFormRequest) {
		Event existingEvent = retrieveEventById(id);

		if (updateEventFormRequest.eventFormRequests().isEmpty())
			throw new GeneralPlatformDomainRuleException("Kindly create at least one form field");
		if (!existingEvent.getEnableRegistration())
			throw new GeneralPlatformDomainRuleException("You cannot update form for this particular event");

		List<EventCategoryResponse> eventCategoryResponses = eventCategoryService
				.retrieveEventCategory(List.of(existingEvent.getCategoryId()));
		if (eventCategoryResponses.isEmpty())
			throw new ResourceNotFoundException("Event Category Not Found");

		try {
			eventFormRepository.deleteAllByEventId(id);
			createEventForms(updateEventFormRequest.eventFormRequests(), existingEvent.getId());

			return eventMapper.toResponse(existingEvent, eventCategoryResponses.get(0));
		} catch (DataIntegrityViolationException e) {
			throw new DataIntegrityViolationException(e.getLocalizedMessage());
		}
	}

	@Override
	public EventResponse updateEventLocation(String id, UpdateEventLocationRequest updateEventLocationRequest) {
		Event existingEvent = retrieveEventById(id);

		if (StringUtils.isNotBlank(updateEventLocationRequest.virtualRoomName()))
			existingEvent.setVirtualRoomName(updateEventLocationRequest.virtualRoomName());
		if (StringUtils.isNotBlank(updateEventLocationRequest.street())
				|| StringUtils.isNotBlank(updateEventLocationRequest.city())
				|| StringUtils.isNotBlank(updateEventLocationRequest.state())
				|| StringUtils.isNotBlank(updateEventLocationRequest.country())) {
			if (StringUtils.isBlank(updateEventLocationRequest.street())
					|| StringUtils.isBlank(updateEventLocationRequest.city())
					|| StringUtils.isBlank(updateEventLocationRequest.state())
					|| StringUtils.isBlank(updateEventLocationRequest.country()))
				throw new GeneralPlatformDomainRuleException("Address is incomplete");

			Event.PhysicalAddress physicalAddress = new Event.PhysicalAddress();
			physicalAddress.setStreet(updateEventLocationRequest.street());
			physicalAddress.setCity(updateEventLocationRequest.city());
			physicalAddress.setState(updateEventLocationRequest.state());
			physicalAddress.setCountry(updateEventLocationRequest.country());
			existingEvent.setPhysicalAddress(physicalAddress);
		}

		List<EventCategoryResponse> eventCategoryResponses = eventCategoryService
				.retrieveEventCategory(List.of(existingEvent.getCategoryId()));
		if (eventCategoryResponses.isEmpty())
			throw new ResourceNotFoundException("Event Category Not Found");

		try {
			Event savedEvent = eventRepository.save(existingEvent);

			return eventMapper.toResponse(savedEvent, eventCategoryResponses.get(0));
		} catch (DataIntegrityViolationException e) {
			throw new DataIntegrityViolationException(e.getLocalizedMessage());
		}
	}

	@Override
	public EventResponse updateEventBasicSettings(String id,
			UpdateEventBasicSettingsRequest updateEventBasicSettingsRequest) {
		Event existingEvent = retrieveEventById(id);

		if (ObjectUtils.isNotEmpty(updateEventBasicSettingsRequest.requireApproval()))
			existingEvent.setRequireApproval(updateEventBasicSettingsRequest.requireApproval());
		if (ObjectUtils.isNotEmpty(updateEventBasicSettingsRequest.enableWaitlist()))
			existingEvent.setEnableWaitlist(updateEventBasicSettingsRequest.enableWaitlist());
		if (ObjectUtils.isNotEmpty(updateEventBasicSettingsRequest.attendeeSize()))
			existingEvent.setAttendeeSize(updateEventBasicSettingsRequest.attendeeSize());
		if (ObjectUtils.isNotEmpty(updateEventBasicSettingsRequest.registrationCutOffTime())) {
			if (!existingEvent.getEnableRegistration())
				throw new GeneralPlatformDomainRuleException(
						"You cannot update registration cut-off time for this event");
			if (updateEventBasicSettingsRequest.registrationCutOffTime()
					.isAfter(existingEvent.getDate().atStartOfDay()))
				throw new GeneralPlatformDomainRuleException("Registration cut off time is invalid");
			existingEvent.setRegistrationCutOffTime(updateEventBasicSettingsRequest.registrationCutOffTime());
		}
		if (StringUtils.isNotBlank(updateEventBasicSettingsRequest.confirmationMessage()))
			existingEvent.setConfirmationMessage(updateEventBasicSettingsRequest.confirmationMessage());
		if (StringUtils.isNotBlank(updateEventBasicSettingsRequest.termsAndConditions()))
			existingEvent.setTermsAndConditions(updateEventBasicSettingsRequest.termsAndConditions());
		if (ObjectUtils.isNotEmpty(updateEventBasicSettingsRequest.sendReminder())) {
			if (updateEventBasicSettingsRequest.sendReminder()
					&& ObjectUtils.isEmpty(updateEventBasicSettingsRequest.reminderHour()))
				throw new GeneralPlatformDomainRuleException("Reminder hour cannot be empty");
			if (updateEventBasicSettingsRequest.reminderHour() <= 0)
				throw new GeneralPlatformDomainRuleException("Reminder hour cannot be invalid");
			existingEvent.setSendReminder(updateEventBasicSettingsRequest.sendReminder());
			existingEvent.setReminderHour(updateEventBasicSettingsRequest.reminderHour());
		}
		if (StringUtils.isNotBlank(updateEventBasicSettingsRequest.logo()))
			existingEvent.setLogo(updateEventBasicSettingsRequest.logo());
		if (StringUtils.isNotBlank(updateEventBasicSettingsRequest.thumbnail()))
			existingEvent.setThumbnail(updateEventBasicSettingsRequest.thumbnail());

		List<EventCategoryResponse> eventCategoryResponses = eventCategoryService
				.retrieveEventCategory(List.of(existingEvent.getCategoryId()));
		if (eventCategoryResponses.isEmpty())
			throw new ResourceNotFoundException("Event Category Not Found");

		try {
			Event savedEvent = eventRepository.save(existingEvent);

			return eventMapper.toResponse(savedEvent, eventCategoryResponses.get(0));
		} catch (DataIntegrityViolationException e) {
			throw new DataIntegrityViolationException(e.getLocalizedMessage());
		}
	}

	@Override
	public EventResponse updateEventPaymentDetails(String id,
			UpdateEventPaymentSettingsRequest updateEventPaymentSettingsRequest) {
		Event existingEvent = retrieveEventById(id);

		if (ObjectUtils.isEmpty(updateEventPaymentSettingsRequest.isPaidEvent()))
			throw new GeneralPlatformDomainRuleException("Please specify whether it is a paid event or not");
		existingEvent.setIsPaidEvent(updateEventPaymentSettingsRequest.isPaidEvent());
		if (existingEvent.getIsPaidEvent()) {
			if (ObjectUtils.isEmpty(updateEventPaymentSettingsRequest.amount()))
				throw new GeneralPlatformDomainRuleException("Amount cannot be empty");
			if (updateEventPaymentSettingsRequest.amount().compareTo(BigDecimal.ZERO) <= 0)
				throw new GeneralPlatformDomainRuleException("Amount cannot be invalid");
			currencyManager.checkCurrency(updateEventPaymentSettingsRequest.currency());
			existingEvent.setCurrency(updateEventPaymentSettingsRequest.currency());
			existingEvent.setAmount(updateEventPaymentSettingsRequest.amount());
		}

		List<EventCategoryResponse> eventCategoryResponses = eventCategoryService
				.retrieveEventCategory(List.of(existingEvent.getCategoryId()));
		if (eventCategoryResponses.isEmpty())
			throw new ResourceNotFoundException("Event Category Not Found");

		try {
			Event savedEvent = eventRepository.save(existingEvent);

			return eventMapper.toResponse(savedEvent, eventCategoryResponses.get(0));
		} catch (DataIntegrityViolationException e) {
			throw new DataIntegrityViolationException(e.getLocalizedMessage());
		}
	}

	private Event retrieveEventById(String id) {
		return eventRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Event Not Found"));
	}
}
