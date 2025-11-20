/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.domain.entities;

import java.time.LocalDateTime;

import org.springframework.data.mongodb.core.mapping.Document;

import com.plutospace.events.commons.entities.BaseEntity;
import com.plutospace.events.domain.data.MeetingAcceptanceStatus;

import lombok.*;

@Getter
@Setter
@Document
@NoArgsConstructor
@AllArgsConstructor(staticName = "instance")
public class MeetingInvitee extends BaseEntity {

	private String meetingId;
	private String firstName;
	private String lastName;
	private String email;
	private MeetingAcceptanceStatus meetingAcceptanceStatus;
	private LocalDateTime lastStatusTime;
}
