/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.domain.data.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor(staticName = "instance")
public class PlanPaymentHistoryResponse {

	private String id;
	private String accountId;
	private String planId;
	private PlanResponse planResponse;
	private BigDecimal planAmount;
	private BigDecimal paidAmount;
	private String currency;
	private String channel;
	private String email;
	private String createdBy;
	private AccountUserResponse accountOwnerResponse;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime createdOn;
}
