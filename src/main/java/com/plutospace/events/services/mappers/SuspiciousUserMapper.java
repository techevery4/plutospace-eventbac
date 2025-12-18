/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.services.mappers;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import com.plutospace.events.commons.data.CustomPageResponse;
import com.plutospace.events.domain.data.request.SaveSuspiciousActivityRequest;
import com.plutospace.events.domain.data.response.*;
import com.plutospace.events.domain.entities.SuspiciousActivity;
import com.plutospace.events.domain.entities.SuspiciousUser;

@Component
public class SuspiciousUserMapper {

	public SuspiciousUserResponse toResponse(SuspiciousUser suspiciousUser, AccountUserResponse accountUserResponse) {
		return SuspiciousUserResponse.instance(suspiciousUser.getId(), suspiciousUser.getAccountId(),
				suspiciousUser.getCreatedBy(), accountUserResponse, suspiciousUser.getUserAgent(),
				suspiciousUser.getNumberOfOccurrences(), suspiciousUser.getIsBlocked(), suspiciousUser.getCreatedOn(),
				suspiciousUser.getUpdatedOn());
	}

	public SuspiciousActivityResponse toResponse(SuspiciousActivity suspiciousActivity,
			AccountUserResponse accountUserResponse) {
		return SuspiciousActivityResponse.instance(suspiciousActivity.getId(), suspiciousActivity.getAccountId(),
				suspiciousActivity.getCreatedBy(), accountUserResponse, suspiciousActivity.getUserAgent(),
				suspiciousActivity.getActionPerformed(), suspiciousActivity.getEndpoint(),
				suspiciousActivity.getMethod(), suspiciousActivity.getCreatedOn());
	}

	public SuspiciousUser toEntity(SaveSuspiciousActivityRequest saveSuspiciousActivityRequest, Integer noOfOccurrences,
			Boolean isBlocked) {
		return SuspiciousUser.instance(saveSuspiciousActivityRequest.getAccountId(),
				saveSuspiciousActivityRequest.getUserAgent(), noOfOccurrences, isBlocked);
	}

	public SuspiciousActivity toEntity(SaveSuspiciousActivityRequest saveSuspiciousActivityRequest) {
		return SuspiciousActivity.instance(saveSuspiciousActivityRequest.getAccountId(),
				saveSuspiciousActivityRequest.getUserAgent(), saveSuspiciousActivityRequest.getActionPerformed(),
				saveSuspiciousActivityRequest.getEndpoint(), saveSuspiciousActivityRequest.getMethod());
	}

	public CustomPageResponse<SuspiciousActivityResponse> toPagedResponse(Page<SuspiciousActivity> suspiciousActivities,
			Map<String, AccountUserResponse> accountUserResponseMap) {
		List<SuspiciousActivityResponse> suspiciousActivityResponses = suspiciousActivities.getContent().stream()
				.map(activity -> {
					AccountUserResponse accountUserResponse = accountUserResponseMap.get(activity.getCreatedBy());
					return toResponse(activity, accountUserResponse);
				}).toList();
		long totalElements = suspiciousActivities.getTotalElements();
		Pageable pageable = suspiciousActivities.getPageable();
		return CustomPageResponse.resolvePageResponse(suspiciousActivityResponses, totalElements, pageable);
	}

	public CustomPageResponse<SuspiciousUserResponse> toPagedResponse(
			Map<String, AccountUserResponse> accountUserResponseMap, Page<SuspiciousUser> suspiciousUsers) {
		List<SuspiciousUserResponse> suspiciousUserResponses = suspiciousUsers.getContent().stream().map(activity -> {
			AccountUserResponse accountUserResponse = accountUserResponseMap.get(activity.getCreatedBy());
			return toResponse(activity, accountUserResponse);
		}).toList();
		long totalElements = suspiciousUsers.getTotalElements();
		Pageable pageable = suspiciousUsers.getPageable();
		return CustomPageResponse.resolvePageResponse(suspiciousUserResponses, totalElements, pageable);
	}
}
