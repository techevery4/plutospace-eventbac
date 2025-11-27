/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.domain.entities;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.data.mongodb.core.mapping.Document;

import com.plutospace.events.commons.entities.BaseEntity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Document
@NoArgsConstructor
@AllArgsConstructor(staticName = "instance")
public class PromoCodeRegistrationLog extends BaseEntity {

	private String promoCode;
	private String userEmail;
	private BigDecimal userPaidAmount;
	private BigDecimal planAmount;
	private Boolean hasSettled;
	private LocalDateTime settledDate;
}
