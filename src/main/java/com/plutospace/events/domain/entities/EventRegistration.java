/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.domain.entities;

import java.time.LocalDate;

import org.springframework.data.mongodb.core.mapping.Document;

import com.plutospace.events.commons.entities.BaseEntity;
import com.plutospace.events.domain.data.EventRegistrationStatus;

import lombok.*;

@Getter
@Setter
@Document
@NoArgsConstructor
@AllArgsConstructor(staticName = "instance")
public class EventRegistration extends BaseEntity {

	private String email; // email of the person filling the form
	private String eventId;
	private LocalDate eventDate;
	private EventRegistrationStatus eventRegistrationStatus;
}
