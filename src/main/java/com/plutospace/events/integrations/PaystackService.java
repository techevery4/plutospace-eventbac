/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.integrations;

import org.springframework.stereotype.Service;

import com.plutospace.events.commons.config.restclient.GeneralRestClient;
import com.plutospace.events.commons.definitions.PropertyConstants;
import com.plutospace.events.domain.data.response.PaystackVerifyPaymentResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Service
@Slf4j
public class PaystackService {

	private final PropertyConstants propertyConstants;
	private final GeneralRestClient generalRestClient;

	public PaystackVerifyPaymentResponse verifyPayment(String reference) {
		String url = propertyConstants.getPaystackBaseUrl() + propertyConstants.getPaystackVerifyUrl() + reference;
		return generalRestClient.get(url, PaystackVerifyPaymentResponse.class);
	}
}
