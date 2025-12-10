/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.domain.data.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.plutospace.events.domain.data.EventType;
import com.plutospace.events.domain.data.LocationType;
import com.plutospace.events.domain.data.VisibilityType;
import com.plutospace.events.domain.entities.Event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor(staticName = "instance")
public class EventResponse {

	private String id;
	private String name;
	private String accountId;
	private EventType type;
	private String categoryId;
	private EventCategoryResponse eventCategoryResponse;
	private String description;

	@JsonFormat(pattern = "yyyy-MM-dd")
	private LocalDate date;

	private Long startTime;
	private Long endTime;
	private Event.Timezone timezone;
	private LocationType locationType;
	private String virtualRoomName;
	private Event.PhysicalAddress physicalAddress;
	private String additionalInstructions;
	private VisibilityType visibilityType;
	private Boolean requireApproval;
	private Boolean enableRegistration;
	private Boolean enableWaitlist;
	private Long attendeeSize;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
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

	private Boolean isCanceled;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime createdOn;
}
