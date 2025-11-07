/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.domain.entities;

import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;

import com.plutospace.events.commons.entities.BaseEntity;
import com.plutospace.events.domain.data.FormType;

import lombok.*;

@Getter
@Setter
@Document
@NoArgsConstructor
@AllArgsConstructor(staticName = "instance")
public class EventForm extends BaseEntity {

	private String eventId;
	private String title;
	private FormType type;
	private List<String> options;
	private Boolean isRequired;
}
