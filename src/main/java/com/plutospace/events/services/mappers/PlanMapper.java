/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.services.mappers;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import com.plutospace.events.commons.data.CustomPageResponse;
import com.plutospace.events.domain.data.PlanType;
import com.plutospace.events.domain.data.request.CreatePlanRequest;
import com.plutospace.events.domain.data.response.PlanResponse;
import com.plutospace.events.domain.entities.Plan;

@Component
public class PlanMapper {

	public PlanResponse toResponse(Plan plan) {
		return PlanResponse.instance(plan.getId(), plan.getType().name(), plan.getName(), plan.getFeatures(),
				plan.getPriceNaira(), plan.getPriceUsd());
	}

	public Plan toEntity(CreatePlanRequest createPlanRequest) {
		PlanType planType = PlanType.fromValue(createPlanRequest.type());
		return Plan.instance(planType, createPlanRequest.name(), createPlanRequest.features(),
				createPlanRequest.priceNaira(), createPlanRequest.priceUsd());
	}

	public CustomPageResponse<PlanResponse> toPagedResponse(Page<Plan> plans) {
		List<PlanResponse> planResponses = plans.getContent().stream().map(this::toResponse).toList();
		long totalElements = plans.getTotalElements();
		Pageable pageable = plans.getPageable();
		return CustomPageResponse.resolvePageResponse(planResponses, totalElements, pageable);
	}
}
