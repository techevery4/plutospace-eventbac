/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.domain.data.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor(staticName = "instance")
public class CalendarResponse {

	private List<MeetingResponse> meetingResponses;
	private List<EventResponse> eventResponses;
}
