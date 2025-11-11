/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.services.implementations;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

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
	private final MeetingMapper meetingMapper;
	private final MeetingValidator meetingValidator;
	private final LinkGenerator linkGenerator;
	private final PropertyConstants propertyConstants;
	private final DateConverter dateConverter;

	@Override
	public MeetingResponse createMeeting(CreateMeetingRequest createMeetingRequest, String accountId) {
		meetingValidator.validate(createMeetingRequest);

		Meeting meeting = meetingMapper.toEntity(createMeetingRequest);
		meeting.setAccountId(accountId);

		try {
			Meeting savedMeeting = meetingRepository.save(meeting);
			String publicLink = linkGenerator.generatePublicLink(savedMeeting.getId(), savedMeeting.getAccountId(),
					GeneralConstants.MEETING, propertyConstants.getEventsEncryptionSecretKey());
			savedMeeting.setPublicId(publicLink);
			meetingRepository.save(savedMeeting);

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

		return meetingRepository.findByAccountIdAndCreatedOnBetweenOrderByCreatedOnDesc(accountId, startDate, endDate);
	}

	private Meeting retrieveMeetingById(String id) {
		return meetingRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Meeting Not Found"));
	}
}
