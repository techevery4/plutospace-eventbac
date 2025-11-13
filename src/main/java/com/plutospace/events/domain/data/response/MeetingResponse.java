/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.domain.data.response;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.plutospace.events.domain.entities.Meeting;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor(staticName = "instance")
public class MeetingResponse {

	private String id;
	private String title;
	private String accountId;
	private String description;

	@JsonFormat(pattern = "yyyy-MM-dd")
	private LocalDate startDate;

	@JsonFormat(pattern = "yyyy-MM-dd")
	private LocalDate endDate;

	private Long startTime;
	private Long endTime;
	private Meeting.Timezone timezone;
	private Boolean isRecurring;
	private List<String> recurringDaysOfTheWeek;
	private Integer maximumParticipants;
	private String publicId;

	private Boolean muteParticipantsOnEntry;
	private Boolean enableWaitingRoom;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime createdOn;
}
