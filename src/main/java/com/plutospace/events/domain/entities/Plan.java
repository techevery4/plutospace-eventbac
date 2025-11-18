/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.domain.entities;

import org.springframework.data.mongodb.core.mapping.Document;

import com.plutospace.events.commons.entities.BaseEntity;
import com.plutospace.events.domain.data.PlanType;

import lombok.*;

@Getter
@Setter
@Document
@NoArgsConstructor
@AllArgsConstructor(staticName = "instance")
public class Plan extends BaseEntity {

	private PlanType type;
	private String name;
	private Features features;
	private Double priceNaira;
	private Double priceUsd;
	private Boolean isActive; // can be activated or deactivated

	@Data
	public static class Features {
		private MeetingFeature meetingFeature;
		private EventFeature eventFeature;
		private CalendarFeature calendarFeature;
		private ProposalFeature proposalFeature;
		private AccountFeature accountFeature;
		private PollFeature pollFeature;
	}

	@Data
	public static class MeetingFeature {
		private Long numberAllowed;
		private Boolean canRecord;
		private Integer numberOfParticipants;
	}

	@Data
	public static class EventFeature {
		private Long numberAllowed;
	}

	@Data
	public static class CalendarFeature {
		private Integer numberOfSynchronization;
		private Long numberOfAppointmentSlots;
	}

	@Data
	public static class ProposalFeature {
		private Long numberOfProposalsReceived;
		private Boolean canQueryProposalSearch;
	}

	@Data
	public static class AccountFeature {
		private Long numberOfInvites;
		private Long numberOfSessions;
	}

	@Data
	public static class PollFeature {
		private Long numberOfPolls;
		private Long numberOfQuestionAndAnswerSessions;
	}
}
