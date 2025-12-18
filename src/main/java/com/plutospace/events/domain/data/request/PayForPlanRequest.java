/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.domain.data.request;

import java.math.BigDecimal;

public record PayForPlanRequest(String planId, BigDecimal planAmount, BigDecimal paidAmount, String currency,
		String email, String reference) {
}
