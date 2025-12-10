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

import com.plutospace.events.commons.data.CustomPageResponse;
import com.plutospace.events.commons.definitions.GeneralConstants;
import com.plutospace.events.commons.exception.GeneralPlatformDomainRuleException;
import com.plutospace.events.domain.data.MeetingAcceptanceStatus;
import com.plutospace.events.domain.data.request.CreateMeetingInviteRequest;
import com.plutospace.events.domain.data.response.*;
import com.plutospace.events.domain.entities.MeetingInvitee;
import com.plutospace.events.domain.repositories.MeetingInviteeRepository;
import com.plutospace.events.intelligence.search.DatabaseSearchService;
import com.plutospace.events.services.AccountUserService;
import com.plutospace.events.services.MeetingInviteeService;
import com.plutospace.events.services.MeetingService;
import com.plutospace.events.services.mappers.MeetingInviteeMapper;
import com.plutospace.events.validation.MeetingInviteeValidator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class MeetingInviteeServiceImpl implements MeetingInviteeService {

	private final MeetingInviteeRepository meetingInviteeRepository;
	private final DatabaseSearchService databaseSearchService;
	private final MeetingService meetingService;
	private final AccountUserService accountUserService;
	private final MeetingInviteeMapper meetingInviteeMapper;
	private final MeetingInviteeValidator meetingInviteeValidator;

	@Override
	public OperationalResponse createMeetingInvite(CreateMeetingInviteRequest createMeetingInviteRequest) {
		meetingInviteeValidator.validate(createMeetingInviteRequest);

		MeetingResponse meetingResponse = meetingService.retrieveMeeting(createMeetingInviteRequest.getMeetingId());
		log.info("Meeting response {}", meetingResponse);

		List<MeetingInvitee> meetingInvitees = new ArrayList<>();
		Map<String, String> emailMaps = new HashMap<>();
		for (CreateMeetingInviteRequest.Invitee invitee : createMeetingInviteRequest.getInvitees()) {
			if (emailMaps.containsKey(invitee.getEmail()))
				continue;
			if (meetingInviteeRepository.existsByMeetingIdAndEmailIgnoreCase(createMeetingInviteRequest.getMeetingId(),
					invitee.getEmail()))
				throw new GeneralPlatformDomainRuleException(
						invitee.getEmail() + " already sent an invite to this meeting");

			MeetingInvitee meetingInvitee = meetingInviteeMapper.toEntity(invitee.getEmail(),
					createMeetingInviteRequest.getMeetingId(), meetingResponse.getStartTime());
			meetingInvitees.add(meetingInvitee);
			emailMaps.putIfAbsent(invitee.getEmail(), invitee.getEmail());
		}

		try {
			meetingInviteeRepository.saveAll(meetingInvitees);

			return OperationalResponse.instance(GeneralConstants.SUCCESS_MESSAGE);
		} catch (DataIntegrityViolationException e) {
			throw new DataIntegrityViolationException(e.getLocalizedMessage());
		}
	}

	@Override
	public CustomPageResponse<MeetingInviteeResponse> retrieveMeetingInvitees(String meetingId, int pageNo,
			int pageSize) {
		Pageable pageable = PageRequest.of(pageNo, pageSize);

		Page<MeetingInvitee> meetingInvitees = meetingInviteeRepository.findByMeetingId(meetingId, pageable);
		if (meetingInvitees.getTotalElements() == 0)
			return new CustomPageResponse<>();

		List<String> accountUserEmails = meetingInvitees.getContent().stream().map(MeetingInvitee::getEmail).toList();
		List<AccountUserResponse> accountUserResponses = accountUserService
				.retrieveAccountUserByEmail(accountUserEmails);
		Map<String, AccountUserResponse> accountUserResponseMap = new HashMap<>();
		for (AccountUserResponse accountUserResponse : accountUserResponses) {
			accountUserResponseMap.putIfAbsent(accountUserResponse.getId(), accountUserResponse);
		}

		return meetingInviteeMapper.toPagedResponse(meetingInvitees, accountUserResponseMap);
	}

	@Override
	public OperationalResponse checkIfAlreadyInvited(String meetingId, String email) {
		if (meetingInviteeRepository.existsByMeetingIdAndEmailIgnoreCase(meetingId, email))
			throw new GeneralPlatformDomainRuleException(email + " already sent an invite to this meeting");

		return OperationalResponse.instance(GeneralConstants.SUCCESS_MESSAGE);
	}

	@Override
	public OperationalResponse joinMeeting(String meetingPublicId, String accountUserId) {
		AccountUserResponse accountUserResponse = accountUserService.retrieveAccountUser(accountUserId);
		MeetingResponse meetingResponse = meetingService.retrieveMeetingByPublicId(meetingPublicId);

		if (!meetingInviteeRepository.existsByMeetingIdAndEmailIgnoreCase(meetingResponse.getId(),
				accountUserResponse.getEmail()))
			throw new GeneralPlatformDomainRuleException(
					"You were not invited to this meeting. Please meet the organizer");

		return OperationalResponse.instance(GeneralConstants.SUCCESS_MESSAGE);
	}

	@Override
	public OperationalResponse changeInviteeStatus(String meetingId, String email, String status) {
		MeetingInvitee meetingInvitee = meetingInviteeRepository.findByMeetingIdAndEmailIgnoreCase(meetingId, email);
		if (meetingInvitee == null)
			throw new GeneralPlatformDomainRuleException("You have not been sent invite to this meeting before");

		MeetingAcceptanceStatus meetingAcceptanceStatus = MeetingAcceptanceStatus.fromValue(status);

		try {
			if (meetingAcceptanceStatus.equals(MeetingAcceptanceStatus.NOT_GOING))
				meetingInviteeRepository.delete(meetingInvitee);
			else {
				meetingInvitee.setMeetingAcceptanceStatus(meetingAcceptanceStatus);
				meetingInvitee.setLastStatusTime(LocalDateTime.now());
				meetingInviteeRepository.save(meetingInvitee);
			}

			return OperationalResponse.instance(GeneralConstants.SUCCESS_MESSAGE);
		} catch (DataIntegrityViolationException e) {
			throw new DataIntegrityViolationException(e.getLocalizedMessage());
		}
	}

	@Override
	public CustomPageResponse<MeetingInviteeResponse> searchMeetingInvitee(String meetingId, String text, int pageNo,
			int pageSize) {
		Pageable pageable = PageRequest.of(pageNo, pageSize);

		List<String> fields = List.of("email", "meetingAcceptanceStatus");
		Page<MeetingInvitee> meetingInvitees = databaseSearchService.findMeetingInviteeByDynamicFilter(meetingId, text,
				fields, pageable);
		if (meetingInvitees.getTotalElements() == 0)
			return new CustomPageResponse<>();

		List<String> accountUserEmails = meetingInvitees.getContent().stream().map(MeetingInvitee::getEmail).toList();
		List<AccountUserResponse> accountUserResponses = accountUserService
				.retrieveAccountUserByEmail(accountUserEmails);
		Map<String, AccountUserResponse> accountUserResponseMap = new HashMap<>();
		for (AccountUserResponse accountUserResponse : accountUserResponses) {
			accountUserResponseMap.putIfAbsent(accountUserResponse.getId(), accountUserResponse);
		}

		return meetingInviteeMapper.toPagedResponse(meetingInvitees, accountUserResponseMap);
	}
}
