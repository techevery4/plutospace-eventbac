/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.domain.data.request;

import java.math.BigDecimal;

public record UpdateEventPaymentSettingsRequest(Boolean isPaidEvent, BigDecimal amount, String currency) {
}
