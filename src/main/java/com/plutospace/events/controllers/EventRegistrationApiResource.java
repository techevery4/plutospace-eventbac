/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.controllers;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.plutospace.events.commons.data.CustomPageResponse;
import com.plutospace.events.domain.data.request.CreateEventRegistrationRequest;
import com.plutospace.events.domain.data.response.EventRegistrationLogResponse;
import com.plutospace.events.domain.data.response.EventRegistrationResponse;
import com.plutospace.events.domain.data.response.OperationalResponse;
import com.plutospace.events.services.EventRegistrationService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import static com.plutospace.events.commons.definitions.ApiResourceConstants.*;

@RestController
@RequestMapping(EVENT_REGISTRATIONS)
@Tag(name = "Event Registration Endpoints", description = "These endpoints manages event registrations on PlutoSpace Events")
@RequiredArgsConstructor
public class EventRegistrationApiResource {

	private final EventRegistrationService eventRegistrationService;

	@PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	@Operation(description = "This endpoint registers for an event")
	public ResponseEntity<OperationalResponse> registerForAnEvent(
			@RequestBody CreateEventRegistrationRequest createEventRegistrationRequest) {
		return ResponseEntity.ok(eventRegistrationService.registerForAnEvent(createEventRegistrationRequest));
	}

	@GetMapping(path = "/{eventId}", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(description = "This endpoint retrieves registrations for an event")
	public ResponseEntity<CustomPageResponse<EventRegistrationResponse>> retrieveEventRegistrations(
			@PathVariable String eventId, @RequestParam(name = "pageNo") int pageNo,
			@RequestParam(name = "pageSize") int pageSize) {
		return ResponseEntity.ok(eventRegistrationService.retrieveEventRegistrations(eventId, pageNo, pageSize));
	}

	@GetMapping(path = RESOURCE_ID + "/approve", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(description = "This endpoint approves a registration for an event")
	public ResponseEntity<OperationalResponse> approveRegistration(@PathVariable String id) {
		return ResponseEntity.ok(eventRegistrationService.approveRegistration(id));
	}

	@GetMapping(path = RESOURCE_ID + "/decline", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(description = "This endpoint declines a registration for an event")
	public ResponseEntity<OperationalResponse> declineRegistration(@PathVariable String id,
			@RequestParam(name = "reason") String reason) {
		return ResponseEntity.ok(eventRegistrationService.declineRegistration(id, reason));
	}

	@GetMapping(path = RESOURCE_ID + "/sign-in", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(description = "This endpoint signs in an attendee for an event")
	public ResponseEntity<OperationalResponse> signInAttendee(@PathVariable String id) {
		return ResponseEntity.ok(eventRegistrationService.signInAttendee(id));
	}

	@GetMapping(path = RESOURCE_ID + "/deny-entry", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(description = "This endpoint denies entry to an attendee for an event")
	public ResponseEntity<OperationalResponse> denyAttendeeEntry(@PathVariable String id,
			@RequestParam(name = "reason") String reason) {
		return ResponseEntity.ok(eventRegistrationService.denyAttendeeEntry(id, reason));
	}

	@GetMapping(path = RESOURCE_ID + "/sign-out", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(description = "This endpoint signs out an attendee for an event")
	public ResponseEntity<OperationalResponse> signOutAttendee(@PathVariable String id) {
		return ResponseEntity.ok(eventRegistrationService.signOutAttendee(id));
	}

	@GetMapping(path = RESOURCE_ID + "/view-logs", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(description = "This endpoint view logs around an event registration")
	public ResponseEntity<List<EventRegistrationLogResponse>> viewLogsAroundRegistration(@PathVariable String id) {
		return ResponseEntity.ok(eventRegistrationService.viewLogsAroundRegistration(id));
	}
}
