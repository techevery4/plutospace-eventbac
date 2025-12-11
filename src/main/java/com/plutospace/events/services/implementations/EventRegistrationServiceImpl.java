/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.services.implementations;

import java.util.*;

import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.plutospace.events.commons.data.CustomPageResponse;
import com.plutospace.events.commons.definitions.GeneralConstants;
import com.plutospace.events.commons.exception.GeneralPlatformDomainRuleException;
import com.plutospace.events.commons.exception.ResourceAlreadyExistsException;
import com.plutospace.events.commons.exception.ResourceNotFoundException;
import com.plutospace.events.domain.data.EventRegistrationStatus;
import com.plutospace.events.domain.data.request.CreateEventRegistrationDataRequest;
import com.plutospace.events.domain.data.request.CreateEventRegistrationRequest;
import com.plutospace.events.domain.data.response.*;
import com.plutospace.events.domain.entities.*;
import com.plutospace.events.domain.repositories.EventRegistrationDataRepository;
import com.plutospace.events.domain.repositories.EventRegistrationLogRepository;
import com.plutospace.events.domain.repositories.EventRegistrationRepository;
import com.plutospace.events.services.AccountUserService;
import com.plutospace.events.services.EventRegistrationService;
import com.plutospace.events.services.EventService;
import com.plutospace.events.services.mappers.EventRegistrationMapper;
import com.plutospace.events.validation.EventRegistrationValidator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventRegistrationServiceImpl implements EventRegistrationService {

	private final EventRegistrationRepository eventRegistrationRepository;
	private final EventRegistrationDataRepository eventRegistrationDataRepository;
	private final EventRegistrationLogRepository eventRegistrationLogRepository;
	private final AccountUserService accountUserService;
	private final EventService eventService;
	private final EventRegistrationMapper eventRegistrationMapper;
	private final EventRegistrationValidator eventRegistrationValidator;

	@Transactional
	@Override
	public OperationalResponse registerForAnEvent(CreateEventRegistrationRequest createEventRegistrationRequest) {
		eventRegistrationValidator.validate(createEventRegistrationRequest);

		if (eventRegistrationRepository.existsByEmailIgnoreCase(createEventRegistrationRequest.email()))
			throw new ResourceAlreadyExistsException("You have already registered for this event before now");

		EventResponse eventResponse = eventService.retrieveEvent(createEventRegistrationRequest.eventId());
		CustomPageResponse<EventFormResponse> eventFormResponses = eventService
				.retrieveEventForms(eventResponse.getId(), 0, 1000);
		if (eventFormResponses.getTotalElements() == 0)
			throw new GeneralPlatformDomainRuleException("This event has no registration form yet");

		Map<String, EventFormResponse> eventFormResponseMap = new HashMap<>();
		for (EventFormResponse eventFormResponse : eventFormResponses.getContent()) {
			eventFormResponseMap.putIfAbsent(eventFormResponse.getId(), eventFormResponse);
		}

		List<EventRegistrationData> eventRegistrationDataList = new ArrayList<>();
		for (CreateEventRegistrationDataRequest createEventRegistrationDataRequest : createEventRegistrationRequest
				.eventRegistrationDataRequests()) {
			EventFormResponse eventFormResponse = eventFormResponseMap.get(createEventRegistrationDataRequest.formId());
			if (eventFormResponse == null)
				throw new GeneralPlatformDomainRuleException("This registration is not allowed");
			if (StringUtils.isBlank(createEventRegistrationDataRequest.response()) && eventFormResponse.getIsRequired())
				throw new GeneralPlatformDomainRuleException(eventFormResponse.getTitle() + " is required");

			EventRegistrationData eventRegistrationData = eventRegistrationMapper.toEntity(
					createEventRegistrationDataRequest, createEventRegistrationRequest.email(),
					createEventRegistrationRequest.eventId());
			eventRegistrationDataList.add(eventRegistrationData);
		}

		EventRegistration eventRegistration = eventRegistrationMapper.toEntity(createEventRegistrationRequest,
				eventResponse.getDate());

		try {
			eventRegistrationRepository.save(eventRegistration);
			eventRegistrationDataRepository.saveAll(eventRegistrationDataList);

			return OperationalResponse.instance(GeneralConstants.SUCCESS_MESSAGE);
		} catch (DataIntegrityViolationException e) {
			throw new DataIntegrityViolationException(e.getLocalizedMessage());
		}
	}

	@Override
	public CustomPageResponse<EventRegistrationResponse> retrieveEventRegistrations(String eventId, int pageNo,
			int pageSize) {
		if (pageSize > 30)
			throw new GeneralPlatformDomainRuleException("Please choose a smaller page size");

		Pageable pageable = PageRequest.of(pageNo, pageSize);

		Page<EventRegistration> eventRegistrations = eventRegistrationRepository
				.findByEventIdOrderByCreatedOnDesc(eventId, pageable);
		if (eventRegistrations.getTotalElements() == 0)
			return new CustomPageResponse<>();
		CustomPageResponse<EventFormResponse> eventFormResponses = eventService.retrieveEventForms(eventId, 0, 1000);
		if (eventFormResponses.getTotalElements() == 0)
			return new CustomPageResponse<>();

		List<String> eventRegistrationEmails = eventRegistrations.getContent().stream().map(EventRegistration::getEmail)
				.toList();
		List<EventRegistrationData> eventRegistrationDataList = eventRegistrationDataRepository
				.findByEmailIgnoreCaseIn(eventRegistrationEmails);
		Map<String, Map<String, EventRegistrationData>> eventRegistrationDataMaps = new HashMap<>();
		for (String email : eventRegistrationEmails) {
			List<EventRegistrationData> eventRegistrationData = eventRegistrationDataList.stream()
					.filter(registrationData -> registrationData.getEmail().equalsIgnoreCase(email)).toList();

			Map<String, EventRegistrationData> eventRegistrationDataMap = new HashMap<>();
			for (EventFormResponse eventFormResponse : eventFormResponses.getContent()) {
				Optional<EventRegistrationData> dataForForm = eventRegistrationData.stream()
						.filter(registrationData -> registrationData.getFormId().equals(eventFormResponse.getId()))
						.findFirst();
				EventRegistrationData singleData = null;
				if (dataForForm.isPresent())
					singleData = dataForForm.get();
				eventRegistrationDataMap.putIfAbsent(eventFormResponse.getId(), singleData);
			}

			eventRegistrationDataMaps.putIfAbsent(email, eventRegistrationDataMap);
		}

		return eventRegistrationMapper.toPagedResponse(eventRegistrations, eventRegistrationDataMaps,
				eventFormResponses.getContent());
	}

	@Transactional
	@Override
	public OperationalResponse approveRegistration(String id) {
		EventRegistration existingEventRegistration = retrieveEventRegistrationById(id);

		if (existingEventRegistration.getEventRegistrationStatus().equals(EventRegistrationStatus.APPROVED))
			throw new GeneralPlatformDomainRuleException("This registration is already approved");

		EventRegistrationLog eventRegistrationLog = new EventRegistrationLog();
		eventRegistrationLog.setRegistrationId(id);
		eventRegistrationLog.setPreviousState(existingEventRegistration.getEventRegistrationStatus());
		eventRegistrationLog.setCurrentState(EventRegistrationStatus.APPROVED);

		existingEventRegistration.setEventRegistrationStatus(EventRegistrationStatus.APPROVED);

		try {
			eventRegistrationRepository.save(existingEventRegistration);
			eventRegistrationLogRepository.save(eventRegistrationLog);

			return OperationalResponse.instance(GeneralConstants.SUCCESS_MESSAGE);
		} catch (DataIntegrityViolationException e) {
			throw new DataIntegrityViolationException(e.getLocalizedMessage());
		}
	}

	@Transactional
	@Override
	public OperationalResponse declineRegistration(String id, String reason) {
		EventRegistration existingEventRegistration = retrieveEventRegistrationById(id);

		if (StringUtils.isBlank(reason))
			throw new GeneralPlatformDomainRuleException("Please you must provide reason for this action");

		if (existingEventRegistration.getEventRegistrationStatus().equals(EventRegistrationStatus.DECLINED))
			throw new GeneralPlatformDomainRuleException("This registration is already declined");

		EventRegistrationLog eventRegistrationLog = new EventRegistrationLog();
		eventRegistrationLog.setRegistrationId(id);
		eventRegistrationLog.setPreviousState(existingEventRegistration.getEventRegistrationStatus());
		eventRegistrationLog.setCurrentState(EventRegistrationStatus.DECLINED);
		eventRegistrationLog.setAdditionalInformation(reason);

		existingEventRegistration.setEventRegistrationStatus(EventRegistrationStatus.DECLINED);

		try {
			eventRegistrationRepository.save(existingEventRegistration);
			eventRegistrationLogRepository.save(eventRegistrationLog);

			return OperationalResponse.instance(GeneralConstants.SUCCESS_MESSAGE);
		} catch (DataIntegrityViolationException e) {
			throw new DataIntegrityViolationException(e.getLocalizedMessage());
		}
	}

	@Transactional
	@Override
	public OperationalResponse signInAttendee(String id) {
		EventRegistration existingEventRegistration = retrieveEventRegistrationById(id);

		if (existingEventRegistration.getEventRegistrationStatus().equals(EventRegistrationStatus.SIGNED_IN))
			throw new GeneralPlatformDomainRuleException("This attendee already signed in");

		EventRegistrationLog eventRegistrationLog = new EventRegistrationLog();
		eventRegistrationLog.setRegistrationId(id);
		eventRegistrationLog.setPreviousState(existingEventRegistration.getEventRegistrationStatus());
		eventRegistrationLog.setCurrentState(EventRegistrationStatus.SIGNED_IN);

		existingEventRegistration.setEventRegistrationStatus(EventRegistrationStatus.SIGNED_IN);

		try {
			eventRegistrationRepository.save(existingEventRegistration);
			eventRegistrationLogRepository.save(eventRegistrationLog);

			return OperationalResponse.instance(GeneralConstants.SUCCESS_MESSAGE);
		} catch (DataIntegrityViolationException e) {
			throw new DataIntegrityViolationException(e.getLocalizedMessage());
		}
	}

	@Transactional
	@Override
	public OperationalResponse denyAttendeeEntry(String id, String reason) {
		EventRegistration existingEventRegistration = retrieveEventRegistrationById(id);

		if (StringUtils.isBlank(reason))
			throw new GeneralPlatformDomainRuleException("Please you must provide reason for this action");

		if (existingEventRegistration.getEventRegistrationStatus().equals(EventRegistrationStatus.DENIED_ENTRY))
			throw new GeneralPlatformDomainRuleException("This attendee is already denied entry to this event");

		EventRegistrationLog eventRegistrationLog = new EventRegistrationLog();
		eventRegistrationLog.setRegistrationId(id);
		eventRegistrationLog.setPreviousState(existingEventRegistration.getEventRegistrationStatus());
		eventRegistrationLog.setCurrentState(EventRegistrationStatus.DENIED_ENTRY);
		eventRegistrationLog.setAdditionalInformation(reason);

		existingEventRegistration.setEventRegistrationStatus(EventRegistrationStatus.DENIED_ENTRY);

		try {
			eventRegistrationRepository.save(existingEventRegistration);
			eventRegistrationLogRepository.save(eventRegistrationLog);

			return OperationalResponse.instance(GeneralConstants.SUCCESS_MESSAGE);
		} catch (DataIntegrityViolationException e) {
			throw new DataIntegrityViolationException(e.getLocalizedMessage());
		}
	}

	@Transactional
	@Override
	public OperationalResponse signOutAttendee(String id) {
		EventRegistration existingEventRegistration = retrieveEventRegistrationById(id);

		if (existingEventRegistration.getEventRegistrationStatus().equals(EventRegistrationStatus.SIGNED_OUT))
			throw new GeneralPlatformDomainRuleException("This attendee already signed out");

		EventRegistrationLog eventRegistrationLog = new EventRegistrationLog();
		eventRegistrationLog.setRegistrationId(id);
		eventRegistrationLog.setPreviousState(existingEventRegistration.getEventRegistrationStatus());
		eventRegistrationLog.setCurrentState(EventRegistrationStatus.SIGNED_OUT);

		existingEventRegistration.setEventRegistrationStatus(EventRegistrationStatus.SIGNED_OUT);

		try {
			eventRegistrationRepository.save(existingEventRegistration);
			eventRegistrationLogRepository.save(eventRegistrationLog);

			return OperationalResponse.instance(GeneralConstants.SUCCESS_MESSAGE);
		} catch (DataIntegrityViolationException e) {
			throw new DataIntegrityViolationException(e.getLocalizedMessage());
		}
	}

	@Override
	public List<EventRegistrationLogResponse> viewLogsAroundRegistration(String id) {
		List<EventRegistrationLog> eventRegistrationLogs = eventRegistrationLogRepository
				.findByRegistrationIdOrderByCreatedOnDesc(id);
		if (eventRegistrationLogs.isEmpty())
			return new ArrayList<>();

		List<String> accountUserIds = eventRegistrationLogs.stream().map(EventRegistrationLog::getCreatedBy).toList();
		List<AccountUserResponse> accountUserResponses = accountUserService.retrieveAccountUser(accountUserIds);
		Map<String, AccountUserResponse> accountUserResponseMap = new HashMap<>();
		for (AccountUserResponse accountUserResponse : accountUserResponses) {
			accountUserResponseMap.putIfAbsent(accountUserResponse.getId(), accountUserResponse);
		}

		return eventRegistrationLogs.stream().map(eventRegistrationLog -> {
			AccountUserResponse accountUserResponse = accountUserResponseMap.get(eventRegistrationLog.getCreatedBy());
			return eventRegistrationMapper.toResponse(eventRegistrationLog, accountUserResponse);
		}).toList();
	}

	@Override
	public CustomPageResponse<EventRegistrationResponse> searchEventRegistration(String eventId, String text) {
		return null;
	}

	private EventRegistration retrieveEventRegistrationById(String id) {
		return eventRegistrationRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Event Registration Not Found"));
	}
}
