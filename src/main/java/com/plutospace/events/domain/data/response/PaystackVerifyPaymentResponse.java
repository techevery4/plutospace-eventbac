/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.domain.data.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaystackVerifyPaymentResponse {

	private boolean status;
	private String message;
	private PaymentData data;

	@Data
	public static class PaymentData {
		private long id;
		private String domain;
		private String status;
		private String reference;
		private long amount;

		@JsonProperty("gateway_response")
		private String gatewayResponse;

		@JsonProperty("paid_at")
		private String paidAt;

		@JsonProperty("created_at")
		private String createdAt;

		private String channel;
		private String currency;

		@JsonProperty("ip_address")
		private String ipAddress;
	}
}
