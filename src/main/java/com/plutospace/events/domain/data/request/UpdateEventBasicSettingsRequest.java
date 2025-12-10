/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.domain.data.request;

import java.time.LocalDateTime;

public record UpdateEventBasicSettingsRequest(Boolean requireApproval, Boolean enableWaitlist, Long attendeeSize,
		LocalDateTime registrationCutOffTime, String confirmationMessage, String termsAndConditions,
		Boolean sendReminder, Integer reminderHour, String logo, String thumbnail) {
}
