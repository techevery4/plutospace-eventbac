/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.domain.data.response;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.plutospace.events.domain.data.MeetingType;
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
	private MeetingType type;
	private String description;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime startTime;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime endTime;

	private Meeting.Timezone timezone;
	private Integer maximumParticipants;
	private String publicId;

	private Boolean muteParticipantsOnEntry;
	private Boolean enableWaitingRoom;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime createdOn;
}
