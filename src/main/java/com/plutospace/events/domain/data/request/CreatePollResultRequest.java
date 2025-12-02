/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.domain.data.request;

import java.util.List;

import com.plutospace.events.domain.entities.PollResult;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor(staticName = "instance")
public class CreatePollResultRequest {

	private String email; // The email of the person casting his vote
	private List<PollResult.Result> results;
}
