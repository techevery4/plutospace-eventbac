/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.domain.entities;

import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;

import com.plutospace.events.commons.entities.BaseEntity;
import com.plutospace.events.domain.data.PollType;

import lombok.*;

@Getter
@Setter
@Document
@NoArgsConstructor
@AllArgsConstructor(staticName = "instance")
public class Poll extends BaseEntity {

	private String accountId;
	private String title;
	private PollType type;
	private String publicId; // link to poll
	private Boolean closedEnded;
	private Boolean isPublished;
	private List<Body> bodies;

	@Data
	public static class Body {
		private String question;
		private List<Option> options;
	}

	@Data
	public static class Option {
		private String text;
		private Long vote;
	}
}
