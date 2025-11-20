/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.domain.data.request;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateMeetingInviteRequest {

	private String meetingId;
	private List<Invitee> invitees;

	@Data
	public static class Invitee {
		private String firstName;
		private String lastName;
		private String email;
	}
}
