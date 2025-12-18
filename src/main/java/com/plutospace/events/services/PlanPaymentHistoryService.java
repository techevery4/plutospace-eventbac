/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.services;

import com.plutospace.events.commons.data.CustomPageResponse;
import com.plutospace.events.domain.data.request.PayForPlanRequest;
import com.plutospace.events.domain.data.response.OperationalResponse;
import com.plutospace.events.domain.data.response.PlanPaymentHistoryResponse;
import com.plutospace.events.domain.entities.PlanPaymentHistory;

public interface PlanPaymentHistoryService {

	OperationalResponse createPlanPaymentHistory(PlanPaymentHistory planPaymentHistory);

	OperationalResponse buyPlanWithPaystack(PayForPlanRequest payForPlanRequest, String accountId);

	CustomPageResponse<PlanPaymentHistoryResponse> retrievePlanPaymentHistories(int pageNo, int pageSize);

	CustomPageResponse<PlanPaymentHistoryResponse> retrieveMyPlanPaymentHistories(String accountId, int pageNo,
			int pageSize);

	CustomPageResponse<PlanPaymentHistoryResponse> searchPlanPaymentHistories(String text, int pageNo, int pageSize);

	CustomPageResponse<PlanPaymentHistoryResponse> searchMyPlanPaymentHistories(String accountId, String text,
			int pageNo, int pageSize);
}
