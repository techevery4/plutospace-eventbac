/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.domain.data.response;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.plutospace.events.domain.data.PollType;
import com.plutospace.events.domain.entities.Poll;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor(staticName = "instance")
public class PollResponse {

	private String id;
	private String accountId;
	private String title;
	private PollType type;
	private String publicId; // link to poll
	private Boolean closedEnded;
	private Boolean isPublished;
	private List<Poll.Body> bodies;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime createdOn;
}
