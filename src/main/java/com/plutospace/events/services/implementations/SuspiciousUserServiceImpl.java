/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.services.implementations;

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
import com.plutospace.events.commons.exception.GeneralPlatformServiceException;
import com.plutospace.events.domain.data.request.SaveSuspiciousActivityRequest;
import com.plutospace.events.domain.data.response.AccountUserResponse;
import com.plutospace.events.domain.data.response.OperationalResponse;
import com.plutospace.events.domain.data.response.SuspiciousActivityResponse;
import com.plutospace.events.domain.data.response.SuspiciousUserResponse;
import com.plutospace.events.domain.entities.*;
import com.plutospace.events.domain.repositories.SuspiciousActivityRepository;
import com.plutospace.events.domain.repositories.SuspiciousUserRepository;
import com.plutospace.events.services.AccountUserService;
import com.plutospace.events.services.SuspiciousUserService;
import com.plutospace.events.services.mappers.SuspiciousUserMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class SuspiciousUserServiceImpl implements SuspiciousUserService {

	private final SuspiciousUserRepository suspiciousUserRepository;
	private final SuspiciousActivityRepository suspiciousActivityRepository;
	private final AccountUserService accountUserService;
	private final SuspiciousUserMapper suspiciousUserMapper;

	@Override
	public OperationalResponse saveSuspiciousThread(SaveSuspiciousActivityRequest saveSuspiciousActivityRequest,
			String accountUserId) {
		SuspiciousUser existingSuspiciousUser = suspiciousUserRepository
				.findByCreatedByAndUserAgentIgnoreCase(accountUserId, saveSuspiciousActivityRequest.getUserAgent());
		boolean isBlocked = false;
		int noOfOccurrences = 0;
		if (existingSuspiciousUser != null) {
			isBlocked = existingSuspiciousUser.getIsBlocked();
			noOfOccurrences = existingSuspiciousUser.getNumberOfOccurrences();
		}
		noOfOccurrences += 1;
		if (noOfOccurrences >= GeneralConstants.ATTEMPTS_BEFORE_BLOCKING)
			isBlocked = true;

		SuspiciousUser suspiciousUser = suspiciousUserMapper.toEntity(saveSuspiciousActivityRequest, noOfOccurrences,
				isBlocked);
		SuspiciousActivity suspiciousActivity = suspiciousUserMapper.toEntity(saveSuspiciousActivityRequest);

		try {
			suspiciousUserRepository.save(suspiciousUser);
			suspiciousActivityRepository.save(suspiciousActivity);

			return OperationalResponse.instance(GeneralConstants.SUCCESS_MESSAGE);
		} catch (DataIntegrityViolationException e) {
			throw new DataIntegrityViolationException(e.getLocalizedMessage());
		}
	}

	@Override
	public OperationalResponse checkIfUserIsBlocked(String userAgent) {
		SuspiciousUser firstSuspiciousUser = suspiciousUserRepository
				.findFirstByUserAgentIgnoreCaseAndIsBlocked(userAgent, true);
		if (firstSuspiciousUser != null)
			throw new GeneralPlatformServiceException("You cannot proceed");

		return OperationalResponse.instance(GeneralConstants.SUCCESS_MESSAGE);
	}

	@Override
	public CustomPageResponse<SuspiciousActivityResponse> retrieveSuspiciousActivities(int pageNo, int pageSize) {
		Pageable pageable = PageRequest.of(pageNo, pageSize);

		Page<SuspiciousActivity> suspiciousActivities = suspiciousActivityRepository
				.findAllByOrderByCreatedOnDesc(pageable);
		if (suspiciousActivities.getTotalElements() == 0)
			return new CustomPageResponse<>();

		List<String> accountUserIds = suspiciousActivities.getContent().stream().map(SuspiciousActivity::getCreatedBy)
				.toList();
		List<AccountUserResponse> accountUserResponses = accountUserService.retrieveAccountUser(accountUserIds);
		Map<String, AccountUserResponse> accountUserResponseMap = new HashMap<>();
		for (AccountUserResponse accountUserResponse : accountUserResponses) {
			accountUserResponseMap.putIfAbsent(accountUserResponse.getId(), accountUserResponse);
		}

		return suspiciousUserMapper.toPagedResponse(suspiciousActivities, accountUserResponseMap);
	}

	@Override
	public CustomPageResponse<SuspiciousUserResponse> retrieveSuspiciousUsers(int pageNo, int pageSize) {
		Pageable pageable = PageRequest.of(pageNo, pageSize);

		Page<SuspiciousUser> suspiciousUsers = suspiciousUserRepository.findAllByOrderByCreatedOnDesc(pageable);
		if (suspiciousUsers.getTotalElements() == 0)
			return new CustomPageResponse<>();

		List<String> accountUserIds = suspiciousUsers.getContent().stream().map(SuspiciousUser::getCreatedBy).toList();
		List<AccountUserResponse> accountUserResponses = accountUserService.retrieveAccountUser(accountUserIds);
		Map<String, AccountUserResponse> accountUserResponseMap = new HashMap<>();
		for (AccountUserResponse accountUserResponse : accountUserResponses) {
			accountUserResponseMap.putIfAbsent(accountUserResponse.getId(), accountUserResponse);
		}

		return suspiciousUserMapper.toPagedResponse(accountUserResponseMap, suspiciousUsers);
	}
}
