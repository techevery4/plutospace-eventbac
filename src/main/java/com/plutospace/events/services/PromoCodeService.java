/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.services;

import com.plutospace.events.commons.data.CustomPageResponse;
import com.plutospace.events.domain.data.request.CreatePromoCodeRequest;
import com.plutospace.events.domain.data.request.RegisterWithPromoCodeRequest;
import com.plutospace.events.domain.data.request.RenewPromoCodeRequest;
import com.plutospace.events.domain.data.response.OperationalResponse;
import com.plutospace.events.domain.data.response.PromoCodeRegistrationLogResponse;
import com.plutospace.events.domain.data.response.PromoCodeResponse;

public interface PromoCodeService {

	PromoCodeResponse createPromoCode(CreatePromoCodeRequest createPromoCodeRequest);

	PromoCodeResponse renewPromoCode(String id, RenewPromoCodeRequest renewPromoCodeRequest);

	CustomPageResponse<PromoCodeResponse> retrievePromoCodes(int pageNo, int pageSize);

	OperationalResponse registerWithPromoCode(RegisterWithPromoCodeRequest registerWithPromoCodeRequest);

	CustomPageResponse<PromoCodeRegistrationLogResponse> retrieveRegistrationLogsOfPromoCode(String promoCode,
			int pageNo, int pageSize);

	OperationalResponse markAsSettled(String registrationLogId);

	OperationalResponse markAsNotSettled(String registrationLogId);

	CustomPageResponse<PromoCodeResponse> searchPromoCodes(String text, int pageNo, int pageSize);
}
