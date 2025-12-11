/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.commons.config.security;

import java.time.LocalDateTime;
import java.util.List;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;

import com.plutospace.events.commons.definitions.GeneralConstants;
import com.plutospace.events.commons.definitions.PropertyConstants;
import com.plutospace.events.commons.exception.GeneralPlatformDomainRuleException;
import com.plutospace.events.commons.exception.GeneralPlatformServiceException;
import com.plutospace.events.commons.utils.DateConverter;
import com.plutospace.events.commons.utils.SecurityMapper;
import com.plutospace.events.domain.data.request.CreateMeetingRequest;
import com.plutospace.events.domain.data.response.AccountResponse;
import com.plutospace.events.domain.data.response.PlanResponse;
import com.plutospace.events.domain.entities.AccountSession;
import com.plutospace.events.domain.entities.Proposal;
import com.plutospace.events.domain.repositories.*;
import com.plutospace.events.services.AccountUserService;
import com.plutospace.events.services.PlanService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static com.plutospace.events.commons.definitions.ApiResourceConstants.*;

@Slf4j
@RequiredArgsConstructor
@Service
public class PlanVerifier {

	private final AccountSessionRepository accountSessionRepository;
	private final EventRepository eventRepository;
	private final FreeSlotRepository freeSlotRepository;
	private final MeetingRepository meetingRepository;
	private final PollRepository pollRepository;
	private final ProposalRepository proposalRepository;
	private final ProposalSubmissionRepository proposalSubmissionRepository;
	private final PlanService planService;
	private final AccountUserService accountUserService;
	private final SecurityMapper securityMapper;
	private final PropertyConstants propertyConstants;
	private final DateConverter dateConverter;

	public Boolean checkPlan(String planFeature, String endpoint, String method, String token, Object request,
			String userAgent, LocalDateTime currentTime) {
		String accountId = securityMapper.retrieveAccountId(token,
				propertyConstants.getEventsLoginEncryptionSecretKey());
		String accountUserId = securityMapper.retrieveAccountUserId(token,
				propertyConstants.getEventsLoginEncryptionSecretKey());
		AccountResponse accountResponse = accountUserService.retrieveMyAccount(accountId);
		PlanResponse planResponse;
		if (accountResponse.getIsDefaulted()) {
			planResponse = planService.retrieveFreePlan(accountResponse.getPlanResponse().getType());
		} else {
			planResponse = accountResponse.getPlanResponse();
		}

		return switch (planFeature) {
			case "MEETING" -> checkMeetingFeature(accountId, dateConverter.getStartOfMonth(currentTime), currentTime,
					planResponse, endpoint, method, request);
			case "EVENT" -> checkEventFeature(accountId, dateConverter.getStartOfMonth(currentTime), currentTime,
					planResponse, endpoint, method);
			case "CALENDAR" -> checkCalendarFeature(accountId, dateConverter.getStartOfMonth(currentTime), currentTime,
					planResponse, endpoint, method);
			case "PROPOSAL" -> checkProposalFeature(accountId, dateConverter.getStartOfMonth(currentTime), currentTime,
					planResponse, endpoint, method);
			case "ACCOUNT" -> checkAccountFeature(accountId, accountUserId, planResponse, endpoint, method, userAgent);
			case "POLL" -> checkPollFeature(accountId, dateConverter.getStartOfMonth(currentTime), currentTime,
					planResponse, endpoint, method);
			default -> false;
		};
	}

	private Boolean checkMeetingFeature(String accountId, LocalDateTime startTime, LocalDateTime endTime,
			PlanResponse planResponse, String endpoint, String method, Object request) {
		if (endpoint.equals(MEETINGS) && method.equalsIgnoreCase(GeneralConstants.POST)) {
			Long meetingCount = meetingRepository.countByAccountIdAndCreatedOnBetween(accountId, startTime, endTime);
			if (ObjectUtils.isEmpty(planResponse.getFeatures().getMeetingFeature().getNumberAllowed())
					|| planResponse.getFeatures().getMeetingFeature().getNumberAllowed() <= meetingCount)
				throw new GeneralPlatformServiceException(GeneralConstants.PLAN_UPGRADE_MESSAGE);
			CreateMeetingRequest createMeetingRequest = (CreateMeetingRequest) request;
			if (ObjectUtils.isEmpty(planResponse.getFeatures().getMeetingFeature().getNumberOfParticipants())
					|| planResponse.getFeatures().getMeetingFeature().getNumberOfParticipants() < createMeetingRequest
							.getMaximumParticipants())
				throw new GeneralPlatformServiceException(GeneralConstants.PLAN_UPGRADE_MESSAGE);
		} else if (endpoint.equals(MEETINGS + "/record") && method.equalsIgnoreCase(GeneralConstants.GET)) {
			if (ObjectUtils.isEmpty(planResponse.getFeatures().getMeetingFeature().getCanRecord())
					|| !planResponse.getFeatures().getMeetingFeature().getCanRecord())
				throw new GeneralPlatformServiceException(GeneralConstants.PLAN_UPGRADE_MESSAGE);
		}

		return true;
	}

	private Boolean checkEventFeature(String accountId, LocalDateTime startTime, LocalDateTime endTime,
			PlanResponse planResponse, String endpoint, String method) {
		if (endpoint.equals(EVENTS) && method.equalsIgnoreCase(GeneralConstants.POST)) {
			Long eventCount = eventRepository.countByAccountIdAndCreatedOnBetween(accountId, startTime, endTime);
			if (ObjectUtils.isEmpty(planResponse.getFeatures().getEventFeature().getNumberAllowed())
					|| planResponse.getFeatures().getEventFeature().getNumberAllowed() <= eventCount)
				throw new GeneralPlatformServiceException(GeneralConstants.PLAN_UPGRADE_MESSAGE);
		}

		return true;
	}

	private Boolean checkCalendarFeature(String accountId, LocalDateTime startTime, LocalDateTime endTime,
			PlanResponse planResponse, String endpoint, String method) {
		if (endpoint.equals(FREE_SLOTS) && method.equalsIgnoreCase(GeneralConstants.POST)) {
			Long freeSlotCount = freeSlotRepository.countByAccountIdAndCreatedOnBetween(accountId, startTime, endTime);
			if (ObjectUtils.isEmpty(planResponse.getFeatures().getCalendarFeature().getNumberOfAppointmentSlots())
					|| planResponse.getFeatures().getCalendarFeature().getNumberOfAppointmentSlots() <= freeSlotCount)
				throw new GeneralPlatformServiceException(GeneralConstants.PLAN_UPGRADE_MESSAGE);
		}

		return true;
	}

	private Boolean checkProposalFeature(String accountId, LocalDateTime startTime, LocalDateTime endTime,
			PlanResponse planResponse, String endpoint, String method) {
		if (endpoint.equals(PROPOSALS + "/submissions") && method.equalsIgnoreCase(GeneralConstants.POST)) {
			List<Proposal> proposals = proposalRepository.findByAccountIdOrderByCreatedOnDesc(accountId);
			if (proposals.isEmpty())
				throw new GeneralPlatformDomainRuleException("You cannot complete this process");
			List<String> proposalIds = proposals.stream().map(Proposal::getId).toList();
			Long proposalCount = proposalSubmissionRepository.countByProposalIdInAndCreatedOnBetween(proposalIds,
					startTime, endTime);
			if (ObjectUtils.isEmpty(planResponse.getFeatures().getProposalFeature().getNumberOfProposalsReceived())
					|| planResponse.getFeatures().getProposalFeature().getNumberOfProposalsReceived() <= proposalCount)
				throw new GeneralPlatformServiceException(GeneralConstants.PLAN_UPGRADE_MESSAGE);
		} else if (endpoint.equals(PROPOSALS + "/submissions/search")
				&& method.equalsIgnoreCase(GeneralConstants.POST)) {
			if (ObjectUtils.isEmpty(planResponse.getFeatures().getProposalFeature().getCanQueryProposalSearch())
					|| !planResponse.getFeatures().getProposalFeature().getCanQueryProposalSearch())
				throw new GeneralPlatformServiceException(GeneralConstants.PLAN_UPGRADE_MESSAGE);
		}

		return true;
	}

	private Boolean checkAccountFeature(String accountId, String accountUserId, PlanResponse planResponse,
			String endpoint, String method, String userAgent) {
		if (endpoint.equals(ACCOUNT_USERS + "/login") && method.equalsIgnoreCase(GeneralConstants.POST)) {
			AccountSession checkAccountSession = accountSessionRepository
					.findByAccountIdAndUserIdAndUserAgentIgnoreCase(accountId, accountUserId, userAgent);
			Long sessionCount = accountSessionRepository.countByAccountId(accountId);
			if (checkAccountSession != null)
				sessionCount -= 1;
			if (ObjectUtils.isEmpty(planResponse.getFeatures().getAccountFeature().getNumberOfSessions())
					|| planResponse.getFeatures().getAccountFeature().getNumberOfSessions() <= sessionCount)
				throw new GeneralPlatformServiceException(GeneralConstants.PLAN_UPGRADE_MESSAGE);
		}

		return true;
	}

	private Boolean checkPollFeature(String accountId, LocalDateTime startTime, LocalDateTime endTime,
			PlanResponse planResponse, String endpoint, String method) {
		if (endpoint.equals(POLLS) && method.equalsIgnoreCase(GeneralConstants.POST)) {
			Long pollCount = pollRepository.countByAccountIdAndCreatedOnBetween(accountId, startTime, endTime);
			if (ObjectUtils.isEmpty(planResponse.getFeatures().getPollFeature().getNumberOfPolls())
					|| planResponse.getFeatures().getPollFeature().getNumberOfPolls() <= pollCount)
				throw new GeneralPlatformServiceException(GeneralConstants.PLAN_UPGRADE_MESSAGE);
		}

		return true;
	}
}
