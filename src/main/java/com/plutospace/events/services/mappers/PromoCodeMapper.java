/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.services.mappers;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import com.plutospace.events.commons.data.CustomPageResponse;
import com.plutospace.events.domain.data.request.CreatePromoCodeRequest;
import com.plutospace.events.domain.data.request.RegisterWithPromoCodeRequest;
import com.plutospace.events.domain.data.response.*;
import com.plutospace.events.domain.entities.PromoCode;
import com.plutospace.events.domain.entities.PromoCodeRegistrationLog;

@Component
public class PromoCodeMapper {

	public PromoCodeResponse toResponse(PromoCode promoCode, AdminUserResponse adminUserResponse) {
		return PromoCodeResponse.instance(promoCode.getId(), promoCode.getCode(), promoCode.getOwner(),
				promoCode.getDiscountPercentage(), promoCode.getStartTime(), promoCode.getEndTime(),
				promoCode.getCreatedOn(), adminUserResponse);
	}

	public PromoCodeRegistrationLogResponse toResponse(PromoCodeRegistrationLog promoCodeRegistrationLog,
			AdminUserResponse adminUserResponse) {
		return PromoCodeRegistrationLogResponse.instance(promoCodeRegistrationLog.getId(),
				promoCodeRegistrationLog.getPromoCode(), promoCodeRegistrationLog.getUserEmail(),
				promoCodeRegistrationLog.getUserPaidAmount(), promoCodeRegistrationLog.getPlanAmount(),
				promoCodeRegistrationLog.getHasSettled(), promoCodeRegistrationLog.getSettledDate(),
				promoCodeRegistrationLog.getCreatedOn(), adminUserResponse);
	}

	public PromoCode toEntity(CreatePromoCodeRequest createPromoCodeRequest) {
		return PromoCode.instance(createPromoCodeRequest.code(), createPromoCodeRequest.owner(),
				createPromoCodeRequest.discountPercentage(), createPromoCodeRequest.startTime(),
				createPromoCodeRequest.endTime());
	}

	public PromoCodeRegistrationLog toEntity(RegisterWithPromoCodeRequest registerWithPromoCodeRequest) {
		return PromoCodeRegistrationLog.instance(registerWithPromoCodeRequest.code(),
				registerWithPromoCodeRequest.userEmail(), registerWithPromoCodeRequest.userPaidAmount(),
				registerWithPromoCodeRequest.planAmount(), false, null);
	}

	public CustomPageResponse<PromoCodeRegistrationLogResponse> toPagedResponse(
			Page<PromoCodeRegistrationLog> promoCodeRegistrationLogs,
			Map<String, AdminUserResponse> adminUserResponseMap) {
		List<PromoCodeRegistrationLogResponse> promoCodeRegistrationLogResponses = promoCodeRegistrationLogs
				.getContent().stream().map(registrationLog -> {
					AdminUserResponse adminUserResponse = adminUserResponseMap.get(registrationLog.getUpdatedBy());
					return toResponse(registrationLog, adminUserResponse);
				}).toList();
		long totalElements = promoCodeRegistrationLogs.getTotalElements();
		Pageable pageable = promoCodeRegistrationLogs.getPageable();
		return CustomPageResponse.resolvePageResponse(promoCodeRegistrationLogResponses, totalElements, pageable);
	}

	public CustomPageResponse<PromoCodeResponse> toPagedResponse(Map<String, AdminUserResponse> adminUserResponseMap,
			Page<PromoCode> promoCodes) {
		List<PromoCodeResponse> promoCodeResponses = promoCodes.getContent().stream().map(promoCode -> {
			AdminUserResponse adminUserResponse = adminUserResponseMap.get(promoCode.getCreatedBy());
			return toResponse(promoCode, adminUserResponse);
		}).toList();
		long totalElements = promoCodes.getTotalElements();
		Pageable pageable = promoCodes.getPageable();
		return CustomPageResponse.resolvePageResponse(promoCodeResponses, totalElements, pageable);
	}
}
