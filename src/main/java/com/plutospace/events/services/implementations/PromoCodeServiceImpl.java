/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.services.implementations;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.plutospace.events.commons.data.CustomPageResponse;
import com.plutospace.events.commons.definitions.GeneralConstants;
import com.plutospace.events.commons.exception.GeneralPlatformDomainRuleException;
import com.plutospace.events.commons.exception.ResourceNotFoundException;
import com.plutospace.events.domain.data.request.CreatePromoCodeRequest;
import com.plutospace.events.domain.data.request.RegisterWithPromoCodeRequest;
import com.plutospace.events.domain.data.request.RenewPromoCodeRequest;
import com.plutospace.events.domain.data.response.*;
import com.plutospace.events.domain.entities.*;
import com.plutospace.events.domain.repositories.PromoCodeRegistrationLogRepository;
import com.plutospace.events.domain.repositories.PromoCodeRepository;
import com.plutospace.events.intelligence.search.DatabaseSearchService;
import com.plutospace.events.services.AccountUserService;
import com.plutospace.events.services.AdminUserService;
import com.plutospace.events.services.PromoCodeService;
import com.plutospace.events.services.mappers.PromoCodeMapper;
import com.plutospace.events.validation.PromoCodeValidator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class PromoCodeServiceImpl implements PromoCodeService {

	private final PromoCodeRepository promoCodeRepository;
	private final PromoCodeRegistrationLogRepository promoCodeRegistrationLogRepository;
	private final DatabaseSearchService databaseSearchService;
	private final AdminUserService adminUserService;
	private final AccountUserService accountUserService;
	private final PromoCodeMapper promoCodeMapper;
	private final PromoCodeValidator promoCodeValidator;

	@Override
	public PromoCodeResponse createPromoCode(CreatePromoCodeRequest createPromoCodeRequest) {
		promoCodeValidator.validate(createPromoCodeRequest);

		if (promoCodeRepository.existsByCodeIgnoreCase(createPromoCodeRequest.code()))
			throw new GeneralPlatformDomainRuleException("Code already exist");

		PromoCode promoCode = promoCodeMapper.toEntity(createPromoCodeRequest);

		try {
			PromoCode savedPromoCode = promoCodeRepository.save(promoCode);
			List<AdminUserResponse> adminUserResponses = adminUserService
					.retrieveAdminUser(List.of(savedPromoCode.getCreatedBy()));

			return promoCodeMapper.toResponse(savedPromoCode, adminUserResponses.get(0));
		} catch (DataIntegrityViolationException e) {
			throw new DataIntegrityViolationException(e.getLocalizedMessage());
		}
	}

	@Override
	public PromoCodeResponse renewPromoCode(String id, RenewPromoCodeRequest renewPromoCodeRequest) {
		PromoCode existingPromoCode = retrievePromoCodeById(id);

		if (ObjectUtils.isNotEmpty(renewPromoCodeRequest.discountPercentage()))
			existingPromoCode.setDiscountPercentage(renewPromoCodeRequest.discountPercentage());
		if (ObjectUtils.isEmpty(renewPromoCodeRequest.startTime()))
			throw new GeneralPlatformDomainRuleException("Start time cannot be empty");
		if (renewPromoCodeRequest.startTime().isAfter(renewPromoCodeRequest.endTime()))
			throw new GeneralPlatformDomainRuleException("Start time cannot be after end time");

		existingPromoCode.setStartTime(renewPromoCodeRequest.startTime());
		existingPromoCode.setEndTime(renewPromoCodeRequest.endTime());

		try {
			PromoCode savedPromoCode = promoCodeRepository.save(existingPromoCode);
			List<AdminUserResponse> adminUserResponses = adminUserService
					.retrieveAdminUser(List.of(savedPromoCode.getCreatedBy()));

			return promoCodeMapper.toResponse(savedPromoCode, adminUserResponses.get(0));
		} catch (DataIntegrityViolationException e) {
			throw new DataIntegrityViolationException(e.getLocalizedMessage());
		}
	}

	@Override
	public CustomPageResponse<PromoCodeResponse> retrievePromoCodes(int pageNo, int pageSize) {
		Pageable pageable = PageRequest.of(pageNo, pageSize);

		Page<PromoCode> promoCodes = promoCodeRepository.findAllByOrderByCreatedOnDesc(pageable);
		if (promoCodes.getTotalElements() == 0)
			return new CustomPageResponse<>();

		List<String> adminUserIds = promoCodes.getContent().stream().map(PromoCode::getCreatedBy).toList();
		List<AdminUserResponse> adminUserResponses = adminUserService.retrieveAdminUser(adminUserIds);
		Map<String, AdminUserResponse> adminUserResponseMap = new HashMap<>();
		for (AdminUserResponse adminUserResponse : adminUserResponses) {
			adminUserResponseMap.putIfAbsent(adminUserResponse.getId(), adminUserResponse);
		}

		return promoCodeMapper.toPagedResponse(adminUserResponseMap, promoCodes);
	}

	@Override
	public OperationalResponse registerWithPromoCode(RegisterWithPromoCodeRequest registerWithPromoCodeRequest) {
		List<AccountUserResponse> accountUserResponses = accountUserService
				.retrieveAccountUserByEmail(List.of(registerWithPromoCodeRequest.userEmail()));
		if (accountUserResponses.isEmpty())
			throw new GeneralPlatformDomainRuleException("This user is invalid");
		AccountResponse accountResponse = accountUserService
				.retrieveMyAccount(accountUserResponses.get(0).getAccountId());
		if (!registerWithPromoCodeRequest.planId().equals(accountResponse.getPlanId()))
			throw new GeneralPlatformDomainRuleException("You did not purchase this plan");
		PromoCodeRegistrationLog promoCodeRegistrationLog = promoCodeMapper.toEntity(registerWithPromoCodeRequest);

		try {
			promoCodeRegistrationLogRepository.save(promoCodeRegistrationLog);

			return OperationalResponse.instance(GeneralConstants.SUCCESS_MESSAGE);
		} catch (DataIntegrityViolationException e) {
			throw new DataIntegrityViolationException(e.getLocalizedMessage());
		}
	}

	@Override
	public CustomPageResponse<PromoCodeRegistrationLogResponse> retrieveRegistrationLogsOfPromoCode(String promoCode,
			int pageNo, int pageSize) {
		Pageable pageable = PageRequest.of(pageNo, pageSize);

		Page<PromoCodeRegistrationLog> promoCodeRegistrationLogs = promoCodeRegistrationLogRepository
				.findByPromoCodeOrderByCreatedOnDesc(promoCode, pageable);
		if (promoCodeRegistrationLogs.getTotalElements() == 0)
			return new CustomPageResponse<>();

		List<String> adminUserIds = promoCodeRegistrationLogs.getContent().stream()
				.map(PromoCodeRegistrationLog::getUpdatedBy).toList();
		List<AdminUserResponse> adminUserResponses = adminUserService.retrieveAdminUser(adminUserIds);
		Map<String, AdminUserResponse> adminUserResponseMap = new HashMap<>();
		for (AdminUserResponse adminUserResponse : adminUserResponses) {
			adminUserResponseMap.putIfAbsent(adminUserResponse.getId(), adminUserResponse);
		}

		return promoCodeMapper.toPagedResponse(promoCodeRegistrationLogs, adminUserResponseMap);
	}

	@Override
	public OperationalResponse markAsSettled(String registrationLogId) {
		PromoCodeRegistrationLog existingPromoCodeRegistrationLog = retrievePromoCodeRegistrationLogById(
				registrationLogId);
		if (existingPromoCodeRegistrationLog.getHasSettled())
			throw new GeneralPlatformDomainRuleException("Already marked as settled");

		existingPromoCodeRegistrationLog.setHasSettled(true);
		existingPromoCodeRegistrationLog.setSettledDate(LocalDateTime.now());

		try {
			promoCodeRegistrationLogRepository.save(existingPromoCodeRegistrationLog);

			return OperationalResponse.instance(GeneralConstants.SUCCESS_MESSAGE);
		} catch (DataIntegrityViolationException e) {
			throw new DataIntegrityViolationException(e.getLocalizedMessage());
		}
	}

	@Override
	public OperationalResponse markAsNotSettled(String registrationLogId) {
		PromoCodeRegistrationLog existingPromoCodeRegistrationLog = retrievePromoCodeRegistrationLogById(
				registrationLogId);
		if (!existingPromoCodeRegistrationLog.getHasSettled())
			throw new GeneralPlatformDomainRuleException("Already marked as not settled");

		existingPromoCodeRegistrationLog.setHasSettled(false);

		try {
			promoCodeRegistrationLogRepository.save(existingPromoCodeRegistrationLog);

			return OperationalResponse.instance(GeneralConstants.SUCCESS_MESSAGE);
		} catch (DataIntegrityViolationException e) {
			throw new DataIntegrityViolationException(e.getLocalizedMessage());
		}
	}

	@Override
	public CustomPageResponse<PromoCodeResponse> searchPromoCodes(String text, int pageNo, int pageSize) {
		Pageable pageable = PageRequest.of(pageNo, pageSize);

		List<String> fields = List.of("code", "owner");
		Page<PromoCode> promoCodes = databaseSearchService.findPromoCodeByDynamicFilter(text, fields, pageable);
		if (promoCodes.getTotalElements() == 0)
			return new CustomPageResponse<>();

		List<String> adminUserIds = promoCodes.getContent().stream().map(PromoCode::getCreatedBy).toList();
		List<AdminUserResponse> adminUserResponses = adminUserService.retrieveAdminUser(adminUserIds);
		Map<String, AdminUserResponse> adminUserResponseMap = new HashMap<>();
		for (AdminUserResponse adminUserResponse : adminUserResponses) {
			adminUserResponseMap.putIfAbsent(adminUserResponse.getId(), adminUserResponse);
		}

		return promoCodeMapper.toPagedResponse(adminUserResponseMap, promoCodes);
	}

	@Override
	public CustomPageResponse<PromoCodeRegistrationLogResponse> searchPromoCodeRegistrationLogs(String text,
			String code, int pageNo, int pageSize) {
		Pageable pageable = PageRequest.of(pageNo, pageSize);

		List<String> fields = List.of("userEmail", "userPaidAmount");
		Page<PromoCodeRegistrationLog> promoCodeRegistrationLogs = databaseSearchService
				.findPromoCodeRegistrationLogByDynamicFilter(code, text, fields, pageable);
		if (promoCodeRegistrationLogs.getTotalElements() == 0)
			return new CustomPageResponse<>();

		List<String> adminUserIds = promoCodeRegistrationLogs.getContent().stream()
				.map(PromoCodeRegistrationLog::getUpdatedBy).toList();
		List<AdminUserResponse> adminUserResponses = adminUserService.retrieveAdminUser(adminUserIds);
		Map<String, AdminUserResponse> adminUserResponseMap = new HashMap<>();
		for (AdminUserResponse adminUserResponse : adminUserResponses) {
			adminUserResponseMap.putIfAbsent(adminUserResponse.getId(), adminUserResponse);
		}

		return promoCodeMapper.toPagedResponse(promoCodeRegistrationLogs, adminUserResponseMap);
	}

	private PromoCode retrievePromoCodeById(String id) {
		return promoCodeRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Promo Code Not Found"));
	}

	private PromoCodeRegistrationLog retrievePromoCodeRegistrationLogById(String id) {
		return promoCodeRegistrationLogRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Promo Code Registration Log Not Found"));
	}
}
