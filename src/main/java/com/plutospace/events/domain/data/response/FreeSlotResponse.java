/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.domain.data.response;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.plutospace.events.domain.entities.FreeSlot;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor(staticName = "instance")
public class FreeSlotResponse {

	private String id;
	private String accountId;
	private String title;
	private LocalDate date;
	private LocalDateTime startTime;
	private LocalDateTime endTime;
	private Boolean isAvailable;
	private FreeSlot.Timezone timezone;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime createdOn;
}
