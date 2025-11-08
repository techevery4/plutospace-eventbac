/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.domain.entities;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.data.mongodb.core.mapping.Document;

import com.plutospace.events.commons.entities.BaseEntity;
import com.plutospace.events.domain.data.EventType;
import com.plutospace.events.domain.data.LocationType;
import com.plutospace.events.domain.data.VisibilityType;

import lombok.*;

@Getter
@Setter
@Document
@NoArgsConstructor
@AllArgsConstructor(staticName = "instance")
public class Event extends BaseEntity {

	private String name;
	private String accountId;
	private EventType type;
	private String categoryId;
	private String description;
	private LocalDate date;
	private Long startTime;
	private Long endTime;
	private Timezone timezone;
	private LocationType locationType;
	private String virtualRoomName;
	private PhysicalAddress physicalAddress;
	private String additionalInstructions;
	private VisibilityType visibilityType;
	private Boolean requireApproval;
	private Boolean enableRegistration;
	private Boolean enableWaitlist;
	private Long attendeeSize;
	private LocalDateTime registrationCutOffTime;
	private Boolean isPaidEvent;
	private BigDecimal amount;
	private String currency; // NGN, USD
	private String confirmationMessage;
	private String termsAndConditions;
	private Boolean sendReminder;
	private Integer reminderHour;
	private String logo;
	private String thumbnail;

	private String meetingLink;
	private String qAndALink;
	private String pollsLink;
	private String registrationLink;

	@Data
	public static class Timezone {
		private Integer value;
		private String representation;
	}

	@Data
	public static class PhysicalAddress {
		private String street;
		private String city;
		private String state;
		private String country;
	}
}
