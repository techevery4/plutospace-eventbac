/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.services.implementations;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.plutospace.events.commons.data.CustomPageResponse;
import com.plutospace.events.commons.definitions.GeneralConstants;
import com.plutospace.events.commons.exception.GeneralPlatformServiceException;
import com.plutospace.events.commons.exception.ResourceNotFoundException;
import com.plutospace.events.commons.utils.CurrencyManager;
import com.plutospace.events.commons.utils.DateConverter;
import com.plutospace.events.domain.data.request.PayForPlanRequest;
import com.plutospace.events.domain.data.response.*;
import com.plutospace.events.domain.entities.Account;
import com.plutospace.events.domain.entities.PlanPaymentHistory;
import com.plutospace.events.domain.repositories.AccountRepository;
import com.plutospace.events.domain.repositories.PlanPaymentHistoryRepository;
import com.plutospace.events.integrations.PaystackService;
import com.plutospace.events.intelligence.search.DatabaseSearchService;
import com.plutospace.events.services.AccountUserService;
import com.plutospace.events.services.PlanPaymentHistoryService;
import com.plutospace.events.services.PlanService;
import com.plutospace.events.services.mappers.PlanPaymentHistoryMapper;
import com.plutospace.events.validation.PlanPaymentHistoryValidator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class PlanPaymentHistoryServiceImpl implements PlanPaymentHistoryService {

	private final PlanPaymentHistoryRepository planPaymentHistoryRepository;
	private final AccountRepository accountRepository;
	private final DatabaseSearchService databaseSearchService;
	private final PlanService planService;
	private final AccountUserService accountUserService;
	private final PaystackService paystackService;
	private final PlanPaymentHistoryMapper planPaymentHistoryMapper;
	private final PlanPaymentHistoryValidator planPaymentHistoryValidator;
	private final DateConverter dateConverter;
	private final CurrencyManager currencyManager;

	@Override
	public OperationalResponse createPlanPaymentHistory(PlanPaymentHistory planPaymentHistory) {
		Optional<Account> accountOptional = accountRepository.findById(planPaymentHistory.getAccountId());
		if (accountOptional.isEmpty())
			throw new ResourceNotFoundException("Account Not Found");

		LocalDateTime startTime = LocalDateTime.now();
		if (ObjectUtils.isNotEmpty(accountOptional.get().getPlanDueDate())) {
			if (startTime.isBefore(accountOptional.get().getPlanDueDate()))
				startTime = accountOptional.get().getPlanDueDate();
		}

		accountOptional.get().setPlanId(planPaymentHistory.getPlanId());
		accountOptional.get().setPlanDueDate(dateConverter.getXDaysTime(startTime, GeneralConstants.PLAN_LIFETIME));
		accountOptional.get().setIsDefaulted(false);

		try {
			accountRepository.save(accountOptional.get());
			planPaymentHistoryRepository.save(planPaymentHistory);

			return OperationalResponse.instance(GeneralConstants.SUCCESS_MESSAGE);
		} catch (DataIntegrityViolationException e) {
			throw new DataIntegrityViolationException(e.getLocalizedMessage());
		}
	}

	@Override
	public OperationalResponse buyPlanWithPaystack(PayForPlanRequest payForPlanRequest, String accountId) {
		planPaymentHistoryValidator.validate(payForPlanRequest);
		currencyManager.checkCurrency(payForPlanRequest.currency());

		PlanResponse planResponse = planService.retrievePlan(payForPlanRequest.planId());
		if (ObjectUtils.isEmpty(planResponse.getIsActive()) || !planResponse.getIsActive())
			throw new GeneralPlatformServiceException(GeneralConstants.MODIFY_NOT_ALLOWED_MESSAGE);
		if (payForPlanRequest.currency().equalsIgnoreCase("NGN")) {
			if (payForPlanRequest.planAmount().doubleValue() != planResponse.getPriceNaira())
				throw new GeneralPlatformServiceException(GeneralConstants.MODIFY_NOT_ALLOWED_MESSAGE);
		}
		if (payForPlanRequest.currency().equalsIgnoreCase("USD")) {
			if (payForPlanRequest.planAmount().doubleValue() != planResponse.getPriceUsd())
				throw new GeneralPlatformServiceException(GeneralConstants.MODIFY_NOT_ALLOWED_MESSAGE);
		}
		if (planPaymentHistoryRepository.existsByPaystackReference(payForPlanRequest.reference()))
			throw new GeneralPlatformServiceException(GeneralConstants.MODIFY_NOT_ALLOWED_MESSAGE);
		OperationalResponse checkPlanCompatibilityResponse = planService.checkPlansCompatibility(accountId,
				payForPlanRequest.planId());
		log.info("checkPlanCompatibilityResponse {}", checkPlanCompatibilityResponse);

		PaystackVerifyPaymentResponse verifyPaymentResponse = paystackService
				.verifyPayment(payForPlanRequest.reference());

		if (verifyPaymentResponse == null || !verifyPaymentResponse.isStatus())
			throw new GeneralPlatformServiceException("Could not verify payment at this time. Please try again later.");
		if (!verifyPaymentResponse.getData().getStatus().equalsIgnoreCase("success"))
			throw new GeneralPlatformServiceException(GeneralConstants.MODIFY_NOT_ALLOWED_MESSAGE);
		if ((payForPlanRequest.paidAmount().doubleValue() * 100) != verifyPaymentResponse.getData().getAmount())
			throw new GeneralPlatformServiceException(GeneralConstants.MODIFY_NOT_ALLOWED_MESSAGE);
		if (LocalDateTime.now().minusMinutes(GeneralConstants.PAYMENT_RECONCILIATION_PERIOD)
				.isAfter(dateConverter.convertPaystackVerifyTime(verifyPaymentResponse.getData().getPaidAt())))
			throw new GeneralPlatformServiceException(GeneralConstants.MODIFY_NOT_ALLOWED_MESSAGE);

		PlanPaymentHistory planPaymentHistory = planPaymentHistoryMapper.toEntity(payForPlanRequest, accountId,
				GeneralConstants.PAYSTACK, verifyPaymentResponse);

		return createPlanPaymentHistory(planPaymentHistory);
	}

	@Override
	public CustomPageResponse<PlanPaymentHistoryResponse> retrievePlanPaymentHistories(int pageNo, int pageSize) {
		Pageable pageable = PageRequest.of(pageNo, pageSize);

		Page<PlanPaymentHistory> planPaymentHistories = planPaymentHistoryRepository
				.findAllByOrderByCreatedOnDesc(pageable);
		if (planPaymentHistories.getTotalElements() == 0)
			return new CustomPageResponse<>();

		List<String> accountUserIds = planPaymentHistories.getContent().stream().map(PlanPaymentHistory::getCreatedBy)
				.toList();
		List<String> planIds = planPaymentHistories.getContent().stream().map(PlanPaymentHistory::getPlanId).toList();
		List<AccountUserResponse> accountUserResponses = accountUserService.retrieveAccountUser(accountUserIds);
		List<PlanResponse> planResponses = planService.retrievePlan(planIds);

		Map<String, AccountUserResponse> accountUserResponseMap = new HashMap<>();
		for (AccountUserResponse accountUserResponse : accountUserResponses) {
			accountUserResponseMap.putIfAbsent(accountUserResponse.getId(), accountUserResponse);
		}
		Map<String, PlanResponse> planResponseMap = new HashMap<>();
		for (PlanResponse planResponse : planResponses) {
			planResponseMap.putIfAbsent(planResponse.getId(), planResponse);
		}

		return planPaymentHistoryMapper.toPagedResponse(planPaymentHistories, planResponseMap, accountUserResponseMap);
	}

	@Override
	public CustomPageResponse<PlanPaymentHistoryResponse> retrieveMyPlanPaymentHistories(String accountId, int pageNo,
			int pageSize) {
		Pageable pageable = PageRequest.of(pageNo, pageSize);

		Page<PlanPaymentHistory> planPaymentHistories = planPaymentHistoryRepository
				.findByAccountIdOrderByCreatedOnDesc(accountId, pageable);
		if (planPaymentHistories.getTotalElements() == 0)
			return new CustomPageResponse<>();

		List<String> accountUserIds = planPaymentHistories.getContent().stream().map(PlanPaymentHistory::getCreatedBy)
				.toList();
		List<String> planIds = planPaymentHistories.getContent().stream().map(PlanPaymentHistory::getPlanId).toList();
		List<AccountUserResponse> accountUserResponses = accountUserService.retrieveAccountUser(accountUserIds);
		List<PlanResponse> planResponses = planService.retrievePlan(planIds);

		Map<String, AccountUserResponse> accountUserResponseMap = new HashMap<>();
		for (AccountUserResponse accountUserResponse : accountUserResponses) {
			accountUserResponseMap.putIfAbsent(accountUserResponse.getId(), accountUserResponse);
		}
		Map<String, PlanResponse> planResponseMap = new HashMap<>();
		for (PlanResponse planResponse : planResponses) {
			planResponseMap.putIfAbsent(planResponse.getId(), planResponse);
		}

		return planPaymentHistoryMapper.toPagedResponse(planPaymentHistories, planResponseMap, accountUserResponseMap);
	}

	@Override
	public CustomPageResponse<PlanPaymentHistoryResponse> searchPlanPaymentHistories(String text, int pageNo,
			int pageSize) {
		Pageable pageable = PageRequest.of(pageNo, pageSize);

		List<String> fields = List.of("currency", "channel", "email");
		Page<PlanPaymentHistory> planPaymentHistories = databaseSearchService
				.findPlanPaymentHistoryByDynamicFilter(text, fields, pageable);
		if (planPaymentHistories.getTotalElements() == 0)
			return new CustomPageResponse<>();

		List<String> accountUserIds = planPaymentHistories.getContent().stream().map(PlanPaymentHistory::getCreatedBy)
				.toList();
		List<String> planIds = planPaymentHistories.getContent().stream().map(PlanPaymentHistory::getPlanId).toList();
		List<AccountUserResponse> accountUserResponses = accountUserService.retrieveAccountUser(accountUserIds);
		List<PlanResponse> planResponses = planService.retrievePlan(planIds);

		Map<String, AccountUserResponse> accountUserResponseMap = new HashMap<>();
		for (AccountUserResponse accountUserResponse : accountUserResponses) {
			accountUserResponseMap.putIfAbsent(accountUserResponse.getId(), accountUserResponse);
		}
		Map<String, PlanResponse> planResponseMap = new HashMap<>();
		for (PlanResponse planResponse : planResponses) {
			planResponseMap.putIfAbsent(planResponse.getId(), planResponse);
		}

		return planPaymentHistoryMapper.toPagedResponse(planPaymentHistories, planResponseMap, accountUserResponseMap);
	}

	@Override
	public CustomPageResponse<PlanPaymentHistoryResponse> searchMyPlanPaymentHistories(String accountId, String text,
			int pageNo, int pageSize) {
		Pageable pageable = PageRequest.of(pageNo, pageSize);

		List<String> fields = List.of("currency", "channel", "email");
		Page<PlanPaymentHistory> planPaymentHistories = databaseSearchService
				.findUserPlanPaymentHistoryByDynamicFilter(accountId, text, fields, pageable);
		if (planPaymentHistories.getTotalElements() == 0)
			return new CustomPageResponse<>();

		List<String> accountUserIds = planPaymentHistories.getContent().stream().map(PlanPaymentHistory::getCreatedBy)
				.toList();
		List<String> planIds = planPaymentHistories.getContent().stream().map(PlanPaymentHistory::getPlanId).toList();
		List<AccountUserResponse> accountUserResponses = accountUserService.retrieveAccountUser(accountUserIds);
		List<PlanResponse> planResponses = planService.retrievePlan(planIds);

		Map<String, AccountUserResponse> accountUserResponseMap = new HashMap<>();
		for (AccountUserResponse accountUserResponse : accountUserResponses) {
			accountUserResponseMap.putIfAbsent(accountUserResponse.getId(), accountUserResponse);
		}
		Map<String, PlanResponse> planResponseMap = new HashMap<>();
		for (PlanResponse planResponse : planResponses) {
			planResponseMap.putIfAbsent(planResponse.getId(), planResponse);
		}

		return planPaymentHistoryMapper.toPagedResponse(planPaymentHistories, planResponseMap, accountUserResponseMap);
	}
}
