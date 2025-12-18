/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.services.mappers;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import com.plutospace.events.commons.data.CustomPageResponse;
import com.plutospace.events.domain.data.request.PayForPlanRequest;
import com.plutospace.events.domain.data.response.*;
import com.plutospace.events.domain.entities.PlanPaymentHistory;

@Component
public class PlanPaymentHistoryMapper {

	public PlanPaymentHistoryResponse toResponse(PlanPaymentHistory planPaymentHistory, PlanResponse planResponse,
			AccountUserResponse accountUserResponse) {
		return PlanPaymentHistoryResponse.instance(planPaymentHistory.getId(), planPaymentHistory.getAccountId(),
				planPaymentHistory.getPlanId(), planResponse, planPaymentHistory.getPlanAmount(),
				planPaymentHistory.getPaidAmount(), planPaymentHistory.getCurrency(), planPaymentHistory.getChannel(),
				planPaymentHistory.getEmail(), planPaymentHistory.getCreatedBy(), accountUserResponse,
				planPaymentHistory.getUpdatedOn());
	}

	public PlanPaymentHistory toEntity(PayForPlanRequest payForPlanRequest, String accountId, String channel,
			PaystackVerifyPaymentResponse paystackVerifyPaymentResponse) {
		return PlanPaymentHistory.instance(accountId, payForPlanRequest.planId(), payForPlanRequest.planAmount(),
				payForPlanRequest.paidAmount(), payForPlanRequest.currency(), channel, payForPlanRequest.email(),
				payForPlanRequest.reference(), paystackVerifyPaymentResponse);
	}

	public CustomPageResponse<PlanPaymentHistoryResponse> toPagedResponse(Page<PlanPaymentHistory> planPaymentHistories,
			Map<String, PlanResponse> planResponseMap, Map<String, AccountUserResponse> accountUserResponseMap) {
		List<PlanPaymentHistoryResponse> planPaymentHistoryResponses = planPaymentHistories.getContent().stream()
				.map(paymentHistory -> {
					PlanResponse planResponse = planResponseMap.get(paymentHistory.getPlanId());
					AccountUserResponse accountUserResponse = accountUserResponseMap.get(paymentHistory.getCreatedBy());
					return toResponse(paymentHistory, planResponse, accountUserResponse);
				}).toList();
		long totalElements = planPaymentHistories.getTotalElements();
		Pageable pageable = planPaymentHistories.getPageable();
		return CustomPageResponse.resolvePageResponse(planPaymentHistoryResponses, totalElements, pageable);
	}
}
