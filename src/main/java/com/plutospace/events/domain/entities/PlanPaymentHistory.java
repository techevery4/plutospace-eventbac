/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.domain.entities;

import java.math.BigDecimal;

import org.springframework.data.mongodb.core.mapping.Document;

import com.plutospace.events.commons.entities.BaseEntity;
import com.plutospace.events.domain.data.response.PaystackVerifyPaymentResponse;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Document
@NoArgsConstructor
@AllArgsConstructor(staticName = "instance")
public class PlanPaymentHistory extends BaseEntity {

	private String accountId;
	private String planId;
	private BigDecimal planAmount;
	private BigDecimal paidAmount;
	private String currency;
	private String channel;
	private String email; // email of the person who made the payment

	// specific logs with respect to paystack
	private String paystackReference;
	private PaystackVerifyPaymentResponse paystackVerifyPaymentResponse;
}
