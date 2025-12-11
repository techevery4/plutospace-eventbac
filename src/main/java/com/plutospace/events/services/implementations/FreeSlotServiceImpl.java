/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.services.implementations;

import java.time.LocalDateTime;
import java.util.List;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.plutospace.events.commons.data.CustomPageResponse;
import com.plutospace.events.commons.definitions.GeneralConstants;
import com.plutospace.events.commons.definitions.PropertyConstants;
import com.plutospace.events.commons.exception.GeneralPlatformDomainRuleException;
import com.plutospace.events.commons.exception.ResourceNotFoundException;
import com.plutospace.events.commons.utils.DateConverter;
import com.plutospace.events.commons.utils.LinkGenerator;
import com.plutospace.events.domain.data.MeetingType;
import com.plutospace.events.domain.data.request.BookFreeSlotRequest;
import com.plutospace.events.domain.data.request.CreateMeetingInviteRequest;
import com.plutospace.events.domain.data.request.CreateMeetingRequest;
import com.plutospace.events.domain.data.request.SaveFreeSlotRequest;
import com.plutospace.events.domain.data.response.FreeSlotResponse;
import com.plutospace.events.domain.data.response.MeetingResponse;
import com.plutospace.events.domain.data.response.OperationalResponse;
import com.plutospace.events.domain.entities.FreeSlot;
import com.plutospace.events.domain.repositories.FreeSlotRepository;
import com.plutospace.events.intelligence.search.DatabaseSearchService;
import com.plutospace.events.services.FreeSlotService;
import com.plutospace.events.services.MeetingInviteeService;
import com.plutospace.events.services.MeetingService;
import com.plutospace.events.services.mappers.FreeSlotMapper;
import com.plutospace.events.validation.FreeSlotValidator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class FreeSlotServiceImpl implements FreeSlotService {

	private final FreeSlotRepository freeSlotRepository;
	private final DatabaseSearchService databaseSearchService;
	private final MeetingService meetingService;
	private final MeetingInviteeService meetingInviteeService;
	private final FreeSlotMapper freeSlotMapper;
	private final FreeSlotValidator freeSlotValidator;
	private final DateConverter dateConverter;
	private final LinkGenerator linkGenerator;
	private final PropertyConstants propertyConstants;

	@Override
	public FreeSlotResponse createFreeSlot(SaveFreeSlotRequest saveFreeSlotRequest, String accountId) {
		freeSlotValidator.validate(saveFreeSlotRequest);

		LocalDateTime startTime = dateConverter.convertTimestamp(saveFreeSlotRequest.startTime());
		LocalDateTime endTime = dateConverter.convertTimestamp(saveFreeSlotRequest.endTime());
		FreeSlot freeSlot = freeSlotMapper.toEntity(saveFreeSlotRequest, accountId, startTime, endTime);

		try {
			FreeSlot savedFreeSlot = freeSlotRepository.save(freeSlot);

			return freeSlotMapper.toResponse(savedFreeSlot);
		} catch (DataIntegrityViolationException e) {
			throw new DataIntegrityViolationException(e.getLocalizedMessage());
		}
	}

	@Override
	public FreeSlotResponse updateFreeSlot(String id, SaveFreeSlotRequest saveFreeSlotRequest) {
		FreeSlot existingFreeSlot = retrieveFreeSlotById(id);

		if (!existingFreeSlot.getIsAvailable())
			throw new GeneralPlatformDomainRuleException("Slot is no longer available");

		if (ObjectUtils.isNotEmpty(saveFreeSlotRequest.date()))
			existingFreeSlot.setDate(saveFreeSlotRequest.date());
		if (ObjectUtils.isNotEmpty(saveFreeSlotRequest.startTime()))
			existingFreeSlot.setStartTime(dateConverter.convertTimestamp(saveFreeSlotRequest.startTime()));
		if (ObjectUtils.isNotEmpty(saveFreeSlotRequest.endTime()))
			existingFreeSlot.setEndTime(dateConverter.convertTimestamp(saveFreeSlotRequest.endTime()));
		if (ObjectUtils.isNotEmpty(saveFreeSlotRequest.timezoneValue())
				&& StringUtils.isNotBlank(saveFreeSlotRequest.timezoneString())) {
			FreeSlot.Timezone timezone = new FreeSlot.Timezone();
			timezone.setRepresentation(saveFreeSlotRequest.timezoneString());
			timezone.setValue(saveFreeSlotRequest.timezoneValue());

			existingFreeSlot.setTimezone(timezone);
		}

		try {
			FreeSlot savedFreeSlot = freeSlotRepository.save(existingFreeSlot);

			return freeSlotMapper.toResponse(savedFreeSlot);
		} catch (DataIntegrityViolationException e) {
			throw new DataIntegrityViolationException(e.getLocalizedMessage());
		}
	}

	@Override
	public String generateAvailableSlotLink(String accountId, String accountUserId) {
		return linkGenerator.generatePublicLink(accountUserId, accountId, GeneralConstants.FREE_SLOT,
				propertyConstants.getEventsEncryptionSecretKey());
	}

	@Override
	public CustomPageResponse<FreeSlotResponse> retrieveMyFreeSlots(String accountId, String accountUserId, int pageNo,
			int pageSize) {
		Pageable pageable = PageRequest.of(pageNo, pageSize);

		Page<FreeSlot> freeSlots = freeSlotRepository.findByAccountIdAndCreatedByOrderByStartTimeDesc(accountId,
				accountUserId, pageable);
		return freeSlotMapper.toPagedResponse(freeSlots);
	}

	@Override
	public List<FreeSlotResponse> retrieveMyAvailableSlots(String slotLink, Long startTime, Long endTime) {
		String decryptedLink = linkGenerator.extractDetailsFromPublicLink(slotLink,
				propertyConstants.getEventsEncryptionSecretKey());
		String[] words = decryptedLink.split(":");
		if (words.length != 3)
			throw new GeneralPlatformDomainRuleException(
					"Your available slot link is broken. Kindly ask for a reshare from your administrator.");

		List<FreeSlot> freeSlots = freeSlotRepository
				.findByAccountIdAndCreatedByAndIsAvailableOrderByStartTimeDesc(words[1], words[0], true);
		return freeSlots.stream().map(freeSlotMapper::toResponse).toList();
	}

	@Override
	public OperationalResponse deleteFreeSlot(String id) {
		FreeSlot existingFreeSlot = retrieveFreeSlotById(id);
		if (!existingFreeSlot.getIsAvailable())
			throw new GeneralPlatformDomainRuleException("This slot has been booked and can no longer be deleted");

		try {
			freeSlotRepository.delete(existingFreeSlot);

			return OperationalResponse.instance(GeneralConstants.SUCCESS_MESSAGE);
		} catch (DataIntegrityViolationException e) {
			throw new DataIntegrityViolationException(e.getLocalizedMessage());
		}
	}

	@Override
	public OperationalResponse bookFreeSlot(BookFreeSlotRequest bookFreeSlotRequest) {
		freeSlotValidator.validate(bookFreeSlotRequest);

		FreeSlot existingFreeSlot = retrieveFreeSlotById(bookFreeSlotRequest.freeSlotId());
		if (!existingFreeSlot.getIsAvailable())
			throw new GeneralPlatformDomainRuleException("Slot no longer available");

		// Creating a meeting
		CreateMeetingRequest createMeetingRequest = new CreateMeetingRequest(existingFreeSlot.getTitle(),
				MeetingType.SCHEDULED.name(), null, existingFreeSlot.getDate(), existingFreeSlot.getDate(),
				dateConverter.convertLocalDateTimeToTimestring(existingFreeSlot.getStartTime()),
				dateConverter.convertLocalDateTimeToTimestring(existingFreeSlot.getEndTime()),
				existingFreeSlot.getTimezone().getValue(), existingFreeSlot.getTimezone().getRepresentation(), false,
				null, GeneralConstants.EVENT_MEETING_PARTICIPANTS, true, true);

		try {
			MeetingResponse meetingResponse = meetingService.createMeeting(createMeetingRequest,
					existingFreeSlot.getAccountId());
			log.info("Response from meeting creation {}", meetingResponse);

			CreateMeetingInviteRequest createMeetingInviteRequest = getCreateMeetingInviteRequest(bookFreeSlotRequest,
					meetingResponse);
			meetingInviteeService.createMeetingInvite(createMeetingInviteRequest);
			existingFreeSlot.setIsAvailable(false);
			freeSlotRepository.save(existingFreeSlot);

			return OperationalResponse.instance(GeneralConstants.SUCCESS_MESSAGE);
		} catch (DataIntegrityViolationException e) {
			throw new DataIntegrityViolationException(e.getLocalizedMessage());
		}
	}

	@Override
	public CustomPageResponse<FreeSlotResponse> searchFreeSlot(String accountId, String accountUserId, String text,
			int pageNo, int pageSize) {
		Pageable pageable = PageRequest.of(pageNo, pageSize);

		List<String> fields = List.of("title", "timezone.representation");
		Page<FreeSlot> freeSlots = databaseSearchService.findFreeSlotByDynamicFilter(accountId, accountUserId, text,
				fields, pageable);

		return freeSlotMapper.toPagedResponse(freeSlots);
	}

	private static CreateMeetingInviteRequest getCreateMeetingInviteRequest(BookFreeSlotRequest bookFreeSlotRequest,
			MeetingResponse meetingResponse) {
		CreateMeetingInviteRequest createMeetingInviteRequest = new CreateMeetingInviteRequest();
		CreateMeetingInviteRequest.Invitee invitee = new CreateMeetingInviteRequest.Invitee();
		invitee.setEmail(bookFreeSlotRequest.email());
		createMeetingInviteRequest.setInvitees(List.of(invitee));
		createMeetingInviteRequest.setMeetingId(meetingResponse.getId());
		return createMeetingInviteRequest;
	}

	private FreeSlot retrieveFreeSlotById(String id) {
		return freeSlotRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Free Slot Not Found"));
	}
}
