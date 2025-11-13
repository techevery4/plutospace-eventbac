/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.domain.data.request;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateMeetingRequest {

	private String title;
	private String description;
	private LocalDate startDate;
	private LocalDate endDate;
	private String startTime;
	private String endTime;
	private Integer timezoneValue;
	private String timezoneString;
	private Boolean isRecurring;
	private List<DayOfWeek> recurringDaysOfTheWeek;
	private Integer maximumParticipants;

	private Boolean muteParticipantsOnEntry;
	private Boolean enableWaitingRoom;
}
