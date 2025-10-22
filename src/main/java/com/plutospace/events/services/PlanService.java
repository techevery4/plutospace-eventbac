package com.plutospace.events.services;

import com.plutospace.events.commons.data.CustomPageResponse;
import com.plutospace.events.domain.data.request.CreatePlanRequest;
import com.plutospace.events.domain.data.request.UpdatePlanRequest;
import com.plutospace.events.domain.data.response.PlanResponse;

public interface PlanService {

    PlanResponse createPlan(CreatePlanRequest request);

    PlanResponse updatePlan(UpdatePlanRequest request);

    PlanResponse retrievePlan(String id);

    CustomPageResponse<PlanResponse> retrieveAllPlans(int pageNo, int pageSize);
}
