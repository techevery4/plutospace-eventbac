/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.domain.data.response;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.plutospace.events.domain.data.EventRegistrationStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor(staticName = "instance")
public class EventRegistrationLogResponse {

	private String id;
	private String registrationId;
	private EventRegistrationStatus previousState;
	private EventRegistrationStatus currentState;
	private String additionalInformation;
	private String treatedBy;
	private AccountUserResponse treatedByUser;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime createdOn;
}
