/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.domain.data.response;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.plutospace.events.domain.data.EventRegistrationStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor(staticName = "instance")
public class EventRegistrationResponse {

	private String id;
	private String email; // email of the person filling the form
	private String eventId;
	private LocalDate eventDate;
	private EventRegistrationStatus eventRegistrationStatus;
	private List<EventRegistrationDataResponse> eventRegistrationDataResponses;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime createdOn;

	@Data
	public static class EventRegistrationDataResponse {
		private String formId;
		private EventFormResponse eventFormResponse;
		private String response;
	}
}
