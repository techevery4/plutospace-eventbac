/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.domain.data.request;

import java.math.BigDecimal;

public record RegisterWithPromoCodeRequest(String code, String userEmail, String planId, BigDecimal userPaidAmount,
		BigDecimal planAmount) {
}
