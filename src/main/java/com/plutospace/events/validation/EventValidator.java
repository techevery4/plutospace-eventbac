/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.validation;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.plutospace.events.commons.exception.GeneralPlatformDomainRuleException;
import com.plutospace.events.domain.data.request.CreateEventRequest;

@Component
public class EventValidator {

	public void validate(CreateEventRequest createEventRequest, Long currentTimestamp) {
		String nameCannotBeNullValidationMessage = "Name cannot be empty";
		String dateCannotBeNullValidationMessage = "Date cannot be empty";
		String dateCannotBeInvalidValidationMessage = "Date cannot be invalid";
		String timeCannotBeNullValidationMessage = "Time cannot be empty";
		String timeCannotBeInvalidValidationMessage = "Start time cannot be greater than end time";
		String addressCannotBeInvalidValidationMessage = "Address is incomplete";
		String registrationCutOffCannotBeInvalidValidationMessage = "Registration cut off time is invalid";
		String registrationCutOffCannotBeNullValidationMessage = "Registration cut off time cannot be empty";
		String reminderHourCannotBeNullValidationMessage = "Reminder hour cannot be empty";
		String reminderHourCannotBeInvalidValidationMessage = "Reminder hour cannot be invalid";
		String amountCannotBeNullValidationMessage = "Amount cannot be empty";
		String amountCannotBeInvalidValidationMessage = "Amount cannot be invalid";

		if (StringUtils.isBlank(createEventRequest.name())) {
			throw new GeneralPlatformDomainRuleException(nameCannotBeNullValidationMessage);
		}

		if (ObjectUtils.isEmpty(createEventRequest.date())) {
			throw new GeneralPlatformDomainRuleException(dateCannotBeNullValidationMessage);
		}

		if (createEventRequest.date().isBefore(LocalDate.now())) {
			throw new GeneralPlatformDomainRuleException(dateCannotBeInvalidValidationMessage);
		}

		if (ObjectUtils.isEmpty(createEventRequest.startTime()) || createEventRequest.startTime() < currentTimestamp) {
			throw new GeneralPlatformDomainRuleException(timeCannotBeNullValidationMessage);
		}

		if (ObjectUtils.isNotEmpty(createEventRequest.endTime())
				&& createEventRequest.startTime() >= createEventRequest.endTime()) {
			throw new GeneralPlatformDomainRuleException(timeCannotBeInvalidValidationMessage);
		}

		if (StringUtils.isNotBlank(createEventRequest.street()) || StringUtils.isNotBlank(createEventRequest.city())
				|| StringUtils.isNotBlank(createEventRequest.state())
				|| StringUtils.isNotBlank(createEventRequest.country())) {
			if (StringUtils.isBlank(createEventRequest.street()) || StringUtils.isBlank(createEventRequest.city())
					|| StringUtils.isBlank(createEventRequest.state())
					|| StringUtils.isBlank(createEventRequest.country()))
				throw new GeneralPlatformDomainRuleException(addressCannotBeInvalidValidationMessage);
		}

		if (ObjectUtils.isNotEmpty(createEventRequest.enableRegistration())
				&& createEventRequest.enableRegistration()) {
			if (ObjectUtils.isEmpty(createEventRequest.registrationCutOffTime()))
				throw new GeneralPlatformDomainRuleException(registrationCutOffCannotBeNullValidationMessage);
			if (createEventRequest.registrationCutOffTime().isAfter(createEventRequest.date().atStartOfDay()))
				throw new GeneralPlatformDomainRuleException(registrationCutOffCannotBeInvalidValidationMessage);
		}

		if (ObjectUtils.isNotEmpty(createEventRequest.sendReminder()) && createEventRequest.sendReminder()) {
			if (ObjectUtils.isEmpty(createEventRequest.reminderHour()))
				throw new GeneralPlatformDomainRuleException(reminderHourCannotBeNullValidationMessage);
			if (createEventRequest.reminderHour() <= 0)
				throw new GeneralPlatformDomainRuleException(reminderHourCannotBeInvalidValidationMessage);
		}

		if (ObjectUtils.isNotEmpty(createEventRequest.isPaidEvent()) && createEventRequest.isPaidEvent()) {
			if (ObjectUtils.isEmpty(createEventRequest.amount()))
				throw new GeneralPlatformDomainRuleException(amountCannotBeNullValidationMessage);
			if (createEventRequest.amount().compareTo(BigDecimal.ZERO) <= 0)
				throw new GeneralPlatformDomainRuleException(amountCannotBeInvalidValidationMessage);
		}
	}
}
