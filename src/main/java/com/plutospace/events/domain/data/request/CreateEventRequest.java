/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.domain.data.request;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record CreateEventRequest(String name, String type, String categoryId, String description, LocalDate date,
		Long startTime, Long endTime, Integer timezoneValue, String timezoneString, String locationType,
		String virtualRoomName, String street, String city, String state, String country, String additionalInstructions,
		String visibilityType, Boolean requireApproval, Boolean enableRegistration, Boolean enableWaitlist,
		Long attendeeSize, LocalDateTime registrationCutOffTime, Boolean isPaidEvent, BigDecimal amount,
		String currency, String confirmationMessage, String termsAndConditions, Boolean sendReminder,
		Integer reminderHour, String logo, String thumbnail, List<CreateEventFormRequest> eventFormRequests) {
}
