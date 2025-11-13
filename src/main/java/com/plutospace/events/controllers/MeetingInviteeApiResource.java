/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.controllers;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.plutospace.events.commons.data.CustomPageResponse;
import com.plutospace.events.domain.data.request.CreateMeetingInviteRequest;
import com.plutospace.events.domain.data.response.MeetingInviteeResponse;
import com.plutospace.events.domain.data.response.OperationalResponse;
import com.plutospace.events.services.MeetingInviteeService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import static com.plutospace.events.commons.definitions.ApiResourceConstants.*;

@RestController
@RequestMapping(MEETING_INVITEES)
@Tag(name = "Meeting Invitee Endpoints", description = "These endpoints manages meeting invitees on PlutoSpace Events")
@RequiredArgsConstructor
public class MeetingInviteeApiResource {

	private final MeetingInviteeService meetingInviteeService;

	@PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	@Operation(description = "This endpoint creates meeting invites")
	public ResponseEntity<OperationalResponse> createMeetingInvite(
			@RequestBody CreateMeetingInviteRequest createMeetingInviteRequest) {
		return ResponseEntity.ok(meetingInviteeService.createMeetingInvite(createMeetingInviteRequest));
	}

	@GetMapping(path = "/{meetingId}", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(description = "This endpoint retrieves all meeting invitees")
	public ResponseEntity<CustomPageResponse<MeetingInviteeResponse>> retrieveMeetingInvitees(
			@PathVariable String meetingId, @RequestParam(value = "pageNo") int pageNo,
			@RequestParam(value = "pageSize") int pageSize) {
		return ResponseEntity.ok(meetingInviteeService.retrieveMeetingInvitees(meetingId, pageNo, pageSize));
	}

	@GetMapping(path = "/{meetingId}/check", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(description = "This endpoint checks if invite already sent to a specific email")
	public ResponseEntity<OperationalResponse> checkIfAlreadyInvited(@PathVariable String meetingId,
			@RequestParam(value = "email") String email) {
		return ResponseEntity.ok(meetingInviteeService.checkIfAlreadyInvited(meetingId, email));
	}

	@GetMapping(path = "/{meetingId}/change-status", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(description = "This endpoint changes the status of an invitee")
	public ResponseEntity<OperationalResponse> changeInviteeStatus(@PathVariable String meetingId,
			@RequestParam(value = "email") String email, @RequestParam(value = "status") String status) {
		return ResponseEntity.ok(meetingInviteeService.changeInviteeStatus(meetingId, email, status));
	}
}
