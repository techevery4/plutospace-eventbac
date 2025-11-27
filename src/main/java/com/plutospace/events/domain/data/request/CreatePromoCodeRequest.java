/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.domain.data.request;

import java.time.LocalDateTime;

public record CreatePromoCodeRequest(String code, String owner, Integer discountPercentage, LocalDateTime startTime,
		LocalDateTime endTime) {
}
