/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.domain.entities;

import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;

import com.plutospace.events.commons.entities.BaseEntity;

import lombok.*;

@Getter
@Setter
@Document
@NoArgsConstructor
@AllArgsConstructor(staticName = "instance")
public class PollResult extends BaseEntity {

	private String pollId;
	private String email; // The email of the person casting his vote
	private List<Result> results;

	@Data
	public static class Result {
		private String question;
		private List<String> answers;
	}
}
