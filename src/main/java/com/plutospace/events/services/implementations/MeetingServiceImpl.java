/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.services.implementations;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
import com.plutospace.events.domain.data.request.CreateMeetingRequest;
import com.plutospace.events.domain.data.response.MeetingResponse;
import com.plutospace.events.domain.entities.Meeting;
import com.plutospace.events.domain.repositories.MeetingRepository;
import com.plutospace.events.intelligence.search.DatabaseSearchService;
import com.plutospace.events.services.MeetingService;
import com.plutospace.events.services.mappers.MeetingMapper;
import com.plutospace.events.validation.MeetingValidator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class MeetingServiceImpl implements MeetingService {

	private final MeetingRepository meetingRepository;
	private final DatabaseSearchService databaseSearchService;
	private final MeetingMapper meetingMapper;
	private final MeetingValidator meetingValidator;
	private final LinkGenerator linkGenerator;
	private final PropertyConstants propertyConstants;
	private final DateConverter dateConverter;

	@Override
	public MeetingResponse createMeeting(CreateMeetingRequest createMeetingRequest, String accountId) {
		meetingValidator.validate(createMeetingRequest);

		LocalDateTime firstStartTime = dateConverter.mergeLocalDateAndTimeString(createMeetingRequest.getStartDate(),
				createMeetingRequest.getStartTime());
		LocalDateTime firstEndTime = dateConverter.mergeLocalDateAndTimeString(createMeetingRequest.getStartDate(),
				createMeetingRequest.getStartTime());
		if (firstStartTime.isAfter(firstEndTime))
			throw new GeneralPlatformDomainRuleException("Start time selected cannot be after end time");

		Meeting meeting = meetingMapper.toEntity(createMeetingRequest, firstStartTime, firstEndTime);
		meeting.setAccountId(accountId);

		try {
			Meeting savedMeeting = meetingRepository.save(meeting);
			String publicLink = linkGenerator.generatePublicLink(savedMeeting.getId(), savedMeeting.getAccountId(),
					GeneralConstants.MEETING, propertyConstants.getEventsEncryptionSecretKey());
			savedMeeting.setPublicId(publicLink);
			meetingRepository.save(savedMeeting);

			if (createMeetingRequest.getIsRecurring()) {
				List<Meeting> recurringMeetings = new ArrayList<>();
				for (DayOfWeek dayOfWeek : createMeetingRequest.getRecurringDaysOfTheWeek()) {
					List<LocalDateTime> startTimes = dateConverter.getDateTimeBetween(
							createMeetingRequest.getStartDate(), createMeetingRequest.getEndDate(), dayOfWeek,
							createMeetingRequest.getStartTime());
					List<LocalDateTime> endTimes = dateConverter.getDateTimeBetween(createMeetingRequest.getStartDate(),
							createMeetingRequest.getEndDate(), dayOfWeek, createMeetingRequest.getEndTime());
					for (int i = 0; i < startTimes.size(); i++) {
						Meeting aMeeting = new Meeting();
						aMeeting.setAccountId(savedMeeting.getAccountId());
						aMeeting.setDescription(savedMeeting.getDescription());
						aMeeting.setTitle(savedMeeting.getTitle());
						aMeeting.setEndTime(endTimes.get(i));
						aMeeting.setPublicId(publicLink);
						aMeeting.setTimezone(meeting.getTimezone());
						aMeeting.setStartTime(startTimes.get(i));
						aMeeting.setEnableWaitingRoom(savedMeeting.getEnableWaitingRoom());
						aMeeting.setMaximumParticipants(savedMeeting.getMaximumParticipants());
						aMeeting.setMuteParticipantsOnEntry(savedMeeting.getMuteParticipantsOnEntry());

						recurringMeetings.add(aMeeting);
					}
				}

				recurringMeetings.remove(0); // remove the first meeting because it was already created

				if (!recurringMeetings.isEmpty())
					meetingRepository.saveAll(recurringMeetings);
			}

			return meetingMapper.toResponse(savedMeeting);
		} catch (DataIntegrityViolationException e) {
			throw new DataIntegrityViolationException(e.getLocalizedMessage());
		}
	}

	@Override
	public List<MeetingResponse> retrieveMeeting(List<String> ids) {
		List<Meeting> meetings = meetingRepository.findByIdIn(ids);
		if (meetings.isEmpty())
			return new ArrayList<>();

		return meetings.stream().map(meetingMapper::toResponse).toList();
	}

	@Override
	public MeetingResponse retrieveMeetingByPublicId(String publicId) {
		String decryptedLink = linkGenerator.extractDetailsFromPublicLink(publicId,
				propertyConstants.getEventsEncryptionSecretKey());
		String[] words = decryptedLink.split(":");
		if (words.length < 1)
			throw new GeneralPlatformDomainRuleException("Meeting link has been corrupted");

		Meeting meeting = retrieveMeetingById(words[0]);

		return meetingMapper.toResponse(meeting);
	}

	@Override
	public List<MeetingResponse> retrieveMeetingsBetween(String accountId, Long startTime, Long endTime) {
		LocalDateTime startDate = dateConverter.convertTimestamp(startTime);
		LocalDateTime endDate = dateConverter.convertTimestamp(endTime);

		List<Meeting> meetings = meetingRepository.findByAccountIdAndCreatedOnBetweenOrderByCreatedOnDesc(accountId,
				startDate, endDate);

		return meetings.stream().map(meetingMapper::toResponse).toList();
	}

	@Override
	public MeetingResponse retrieveMeeting(String id) {
		Meeting existingMeeting = retrieveMeetingById(id);

		return meetingMapper.toResponse(existingMeeting);
	}

	@Override
	public CustomPageResponse<MeetingResponse> searchMeeting(String accountId, String text, int pageNo, int pageSize) {
		Pageable pageable = PageRequest.of(pageNo, pageSize);

		List<String> fields = List.of("title", "description", "timezone.representation", "publicId");
		Page<Meeting> meetings = databaseSearchService.findMeetingByDynamicFilter(accountId, text, fields, pageable);

		return meetingMapper.toPagedResponse(meetings);
	}

	private Meeting retrieveMeetingById(String id) {
		return meetingRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Meeting Not Found"));
	}
}
