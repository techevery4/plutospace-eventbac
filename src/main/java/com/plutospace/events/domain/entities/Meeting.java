/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.domain.entities;

import java.time.LocalDateTime;

import org.springframework.data.mongodb.core.mapping.Document;

import com.plutospace.events.commons.entities.BaseEntity;

import lombok.*;

@Getter
@Setter
@Document
@NoArgsConstructor
@AllArgsConstructor(staticName = "instance")
public class Meeting extends BaseEntity {

	private String title;
	private String accountId;
	private String description;
	private LocalDateTime startTime;
	private LocalDateTime endTime;
	private Timezone timezone;
	private Integer maximumParticipants;
	private String publicId;

	private Boolean muteParticipantsOnEntry;
	private Boolean enableWaitingRoom;

	@Data
	public static class Timezone {
		private Integer value;
		private String representation;
	}
}
