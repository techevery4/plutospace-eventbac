/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.services.implementations;

import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.plutospace.events.commons.data.CustomPageResponse;
import com.plutospace.events.commons.definitions.GeneralConstants;
import com.plutospace.events.commons.exception.GeneralPlatformDomainRuleException;
import com.plutospace.events.commons.exception.GeneralPlatformServiceException;
import com.plutospace.events.commons.exception.ResourceAlreadyExistsException;
import com.plutospace.events.commons.exception.ResourceNotFoundException;
import com.plutospace.events.domain.data.PlanType;
import com.plutospace.events.domain.data.request.PlanRequest;
import com.plutospace.events.domain.data.response.OperationalResponse;
import com.plutospace.events.domain.data.response.PlanResponse;
import com.plutospace.events.domain.entities.Account;
import com.plutospace.events.domain.entities.Plan;
import com.plutospace.events.domain.repositories.AccountRepository;
import com.plutospace.events.domain.repositories.AccountSessionRepository;
import com.plutospace.events.domain.repositories.AccountUserRepository;
import com.plutospace.events.domain.repositories.PlanRepository;
import com.plutospace.events.intelligence.search.DatabaseSearchService;
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
	private final AccountRepository accountRepository;
	private final AccountSessionRepository accountSessionRepository;
	private final AccountUserRepository accountUserRepository;
	private final PlanMapper planMapper;
	private final DatabaseSearchService databaseSearchService;
	private static final String PLAN_EXISTS = "Plan already exists";

	@Override
	public PlanResponse createPlan(PlanRequest request) {
		planValidator.validate(request);
		Plan plan = planMapper.toEntity(request);
		if (planRepository.existsByNameIgnoreCase(request.getName()))
			throw new ResourceAlreadyExistsException(PLAN_EXISTS);
		if (planRepository.existsByTypeAndPriceNaira(plan.getType(), request.getPriceNaira()))
			throw new ResourceAlreadyExistsException(PLAN_EXISTS);
		if (planRepository.existsByTypeAndPriceUsd(plan.getType(), request.getPriceUsd()))
			throw new ResourceAlreadyExistsException(PLAN_EXISTS);

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

		if (StringUtils.isNotBlank(request.getType())) {
			PlanType planType = PlanType.fromValue(request.getType());
			plan.setType(planType);
		}
		if (StringUtils.isNotBlank(request.getName())) {
			Plan checkPlan = planRepository.findByNameIgnoreCase(request.getName());
			if (!checkPlan.getId().equals(id))
				throw new ResourceAlreadyExistsException(PLAN_EXISTS);
			plan.setName(request.getName());
		}
		if (ObjectUtils.isNotEmpty(request.getPriceNaira())) {
			Plan checkPlan = planRepository.findByTypeAndPriceNaira(plan.getType(), request.getPriceNaira());
			if (!checkPlan.getId().equals(id))
				throw new ResourceAlreadyExistsException(PLAN_EXISTS);
			plan.setPriceNaira(request.getPriceNaira());
		}
		if (ObjectUtils.isNotEmpty(request.getPriceUsd())) {
			Plan checkPlan = planRepository.findByTypeAndPriceUsd(plan.getType(), request.getPriceUsd());
			if (!checkPlan.getId().equals(id))
				throw new ResourceAlreadyExistsException(PLAN_EXISTS);
			plan.setPriceUsd(request.getPriceUsd());
		}
		if (ObjectUtils.isNotEmpty(request.getFeatures()))
			plan.setFeatures(request.getFeatures());

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
	public PlanResponse retrieveFreePlan(String type) {
		PlanType planType = PlanType.fromValue(type);
		Plan plan = planRepository.findByTypeAndPriceNairaAndPriceUsd(planType, 0.0, 0.0);
		if (plan == null)
			throw new GeneralPlatformServiceException(
					"There is no free plan at the moment. Please contact the TechEveryWhere team");

		return planMapper.toResponse(plan);
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

	@Override
	public CustomPageResponse<PlanResponse> searchPlan(String text, int pageNo, int pageSize) {
		Pageable pageable = PageRequest.of(pageNo, pageSize);

		List<String> fields = List.of("name", "type");
		Page<Plan> plans = databaseSearchService.findPlanByDynamicFilter(text, fields, pageable);

		return planMapper.toPagedResponse(plans);
	}

	@Override
	public OperationalResponse checkPlansCompatibility(String accountId, String newPlanId) {
		Plan newPlan = retrievePlanById(newPlanId);
		if (ObjectUtils.isEmpty(newPlan.getIsActive()) || !newPlan.getIsActive())
			throw new GeneralPlatformDomainRuleException("This plan is not active");

		Optional<Account> accountOptional = accountRepository.findById(accountId);
		if (accountOptional.isEmpty())
			throw new ResourceNotFoundException("Account Not Found");
		Plan oldPlan = retrievePlanById(accountOptional.get().getPlanId());

		if (newPlan.getFeatures().getAccountFeature().getNumberOfSessions() < oldPlan.getFeatures().getAccountFeature()
				.getNumberOfSessions()) {
			Long sessionCount = accountSessionRepository.countByAccountId(accountId);
			if (newPlan.getFeatures().getAccountFeature().getNumberOfSessions() < sessionCount)
				throw new GeneralPlatformDomainRuleException("Please logout from at least "
						+ (sessionCount - newPlan.getFeatures().getAccountFeature().getNumberOfSessions())
						+ " session(s) before purchasing this plan");
		}
		if (newPlan.getFeatures().getAccountFeature().getNumberOfInvites() < oldPlan.getFeatures().getAccountFeature()
				.getNumberOfInvites()) {
			Long userCount = accountUserRepository.countByAccountId(accountId);
			if (newPlan.getFeatures().getAccountFeature().getNumberOfInvites() < userCount)
				throw new GeneralPlatformDomainRuleException("Please remove at least "
						+ (userCount - newPlan.getFeatures().getAccountFeature().getNumberOfInvites())
						+ " user(s) before purchasing this plan");
		}

		return OperationalResponse.instance(GeneralConstants.SUCCESS_MESSAGE);
	}

	private Plan retrievePlanById(String id) {
		return planRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Plan Not Found"));
	}
}
