/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.services.implementations;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.plutospace.events.commons.data.CustomPageResponse;
import com.plutospace.events.commons.exception.ResourceAlreadyExistsException;
import com.plutospace.events.commons.exception.ResourceNotFoundException;
import com.plutospace.events.domain.data.PlanType;
import com.plutospace.events.domain.data.request.CreatePlanRequest;
import com.plutospace.events.domain.data.request.UpdatePlanRequest;
import com.plutospace.events.domain.data.response.PlanResponse;
import com.plutospace.events.domain.entities.Plan;
import com.plutospace.events.domain.repositories.PlanRepository;
import com.plutospace.events.services.PlanService;
import com.plutospace.events.services.mappers.PlanMapper;
import com.plutospace.events.validation.PlanValidator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class PlanServiceImpl implements PlanService {

	private final PlanValidator planValidator;
	private final PlanRepository planRepository;
	private final PlanMapper planMapper;

	@Override
	public PlanResponse createPlan(CreatePlanRequest request) {
		planValidator.validate(request);
		Plan plan = planMapper.toEntity(request);
		if (planRepository.existsByNameIgnoreCase(request.name()))
			throw new ResourceAlreadyExistsException("Plan already exists");

		try {
			Plan saved = planRepository.save(plan);

			return planMapper.toResponse(saved);
		} catch (DataIntegrityViolationException e) {
			throw new DataIntegrityViolationException(e.getLocalizedMessage());
		}
	}

	@Override
	public PlanResponse updatePlan(UpdatePlanRequest request) {
		planValidator.validate(request);
		Plan plan = retrievePlanById(request.id());
		if (StringUtils.isNotBlank(request.type()))
			plan.setType(PlanType.fromValue(request.type()));
		if (ObjectUtils.isNotEmpty(request.features()))
			plan.setFeatures(request.features());
		if (request.priceNaira() > 0)
			plan.setPriceNaira(request.priceNaira());
		if (request.priceUsd() > 0)
			plan.setPriceUsd(request.priceUsd());
		if (StringUtils.isNotBlank(request.name())) {
			Plan checkPlan = planRepository.findByNameIgnoreCase(request.name());
			if (!checkPlan.getId().equals(request.id()))
				throw new ResourceAlreadyExistsException("Plan already exists");
		}

		try {
			Plan saved = planRepository.save(plan);

			return planMapper.toResponse(saved);
		} catch (DataIntegrityViolationException e) {
			throw new DataIntegrityViolationException(e.getLocalizedMessage());
		}
	}

	@Override
	public PlanResponse retrievePlan(String id) {
		Plan plan = retrievePlanById(id);
		return planMapper.toResponse(plan);
	}

	@Override
	public CustomPageResponse<PlanResponse> retrieveAllPlans(int pageNo, int pageSize) {
		Pageable pageable = PageRequest.of(pageNo, pageSize);
		Page<Plan> plans = planRepository.findAll(pageable);

		return planMapper.toPagedResponse(plans);
	}

	private Plan retrievePlanById(String id) {
		return planRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Plan Not Found"));
	}
}
