/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.services.implementations;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.plutospace.events.commons.data.CustomPageResponse;
import com.plutospace.events.commons.definitions.GeneralConstants;
import com.plutospace.events.commons.exception.GeneralPlatformDomainRuleException;
import com.plutospace.events.commons.exception.ResourceAlreadyExistsException;
import com.plutospace.events.commons.exception.ResourceNotFoundException;
import com.plutospace.events.domain.data.PlanType;
import com.plutospace.events.domain.data.request.PlanRequest;
import com.plutospace.events.domain.data.response.OperationalResponse;
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
	public PlanResponse createPlan(PlanRequest request) {
		planValidator.validate(request);
		Plan plan = planMapper.toEntity(request);
		if (planRepository.existsByNameIgnoreCase(request.getName()))
			throw new ResourceAlreadyExistsException("Plan already exists");

		try {
			Plan saved = planRepository.save(plan);

			return planMapper.toResponse(saved);
		} catch (DataIntegrityViolationException e) {
			throw new DataIntegrityViolationException(e.getLocalizedMessage());
		}
	}

	@Override
	public PlanResponse updatePlan(PlanRequest request, String id) {
		planValidator.validate(request);
		Plan plan = retrievePlanById(id);
		if (StringUtils.isNotBlank(request.getName())) {
			Plan checkPlan = planRepository.findByNameIgnoreCase(request.getName());
			if (!checkPlan.getId().equals(id))
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

	@Override
	public List<PlanResponse> retrievePlan(List<String> ids) {
		List<Plan> plans = planRepository.findByIdIn(ids);

		return plans.stream().map(planMapper::toResponse).toList();
	}

	@Override
	public CustomPageResponse<PlanResponse> retrieveActivePlanByType(String type, int pageNo, int pageSize) {
		PlanType planType = PlanType.fromValue(type);
		Pageable pageable = PageRequest.of(pageNo, pageSize);

		Page<Plan> plans = planRepository.findByTypeAndIsActiveOrderByPriceNairaDesc(planType, true, pageable);

		return planMapper.toPagedResponse(plans);
	}

	@Override
	public OperationalResponse setPlanAsActive(String id) {
		Plan existingPlan = retrievePlanById(id);
		if (existingPlan.getIsActive())
			throw new GeneralPlatformDomainRuleException("Plan Already Active");

		existingPlan.setIsActive(true);

		try {
			planRepository.save(existingPlan);

			return OperationalResponse.instance(GeneralConstants.SUCCESS_MESSAGE);
		} catch (DataIntegrityViolationException e) {
			throw new DataIntegrityViolationException(e.getLocalizedMessage());
		}
	}

	@Override
	public OperationalResponse setPlanAsInactive(String id) {
		Plan existingPlan = retrievePlanById(id);
		if (!existingPlan.getIsActive())
			throw new GeneralPlatformDomainRuleException("Plan Already inactive");

		existingPlan.setIsActive(false);

		try {
			planRepository.save(existingPlan);

			return OperationalResponse.instance(GeneralConstants.SUCCESS_MESSAGE);
		} catch (DataIntegrityViolationException e) {
			throw new DataIntegrityViolationException(e.getLocalizedMessage());
		}
	}

	private Plan retrievePlanById(String id) {
		return planRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Plan Not Found"));
	}
}
