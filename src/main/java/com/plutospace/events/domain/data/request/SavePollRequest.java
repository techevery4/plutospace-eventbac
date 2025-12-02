/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.domain.data.request;

import java.util.List;

import com.plutospace.events.domain.entities.Poll;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor(staticName = "instance")
public class SavePollRequest {

	private String title;
	private String type;
	private String eventId; // signal to be close-ended
	private List<Poll.Body> bodies;
}
