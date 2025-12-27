/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.domain.entities;

import org.springframework.data.mongodb.core.mapping.Document;

import com.plutospace.events.commons.entities.BaseEntity;

import lombok.*;

@Getter
@Setter
@Document
@NoArgsConstructor
@AllArgsConstructor(staticName = "instance")
public class QuestionAndAnswer extends BaseEntity {

	private String accountId;
	private String title;
	private String publicId; // link to Q and A
	private Boolean closedEnded;
	private Boolean isPublished;
}
