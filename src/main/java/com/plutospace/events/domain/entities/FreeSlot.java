/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.domain.entities;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.data.mongodb.core.mapping.Document;

import com.plutospace.events.commons.entities.BaseEntity;

import lombok.*;

@Getter
@Setter
@Document
@NoArgsConstructor
@AllArgsConstructor(staticName = "instance")
public class FreeSlot extends BaseEntity {

	private String accountId;
	private String title;
	private LocalDate date;
	private LocalDateTime startTime;
	private LocalDateTime endTime;
	private Boolean isAvailable;
	private Timezone timezone;

	@Data
	public static class Timezone {
		private Integer value;
		private String representation;
	}
}
