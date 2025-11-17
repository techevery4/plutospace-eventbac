/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.services;

import com.plutospace.events.commons.data.CustomPageResponse;
import com.plutospace.events.domain.data.request.PlanRequest;
import com.plutospace.events.domain.data.response.PlanResponse;

public interface PlanService {

	PlanResponse createPlan(PlanRequest request);

	PlanResponse updatePlan(PlanRequest request, String id);

	PlanResponse retrievePlan(String id);

	CustomPageResponse<PlanResponse> retrieveAllPlans(int pageNo, int pageSize);
}
