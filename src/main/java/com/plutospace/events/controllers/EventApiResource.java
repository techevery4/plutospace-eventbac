/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.controllers;

import java.net.URI;
import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import com.plutospace.events.commons.data.CustomPageResponse;
import com.plutospace.events.domain.data.request.CreateEventRequest;
import com.plutospace.events.domain.data.response.EventFormResponse;
import com.plutospace.events.domain.data.response.EventResponse;
import com.plutospace.events.services.EventService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import static com.plutospace.events.commons.definitions.ApiResourceConstants.*;

@RestController
@RequestMapping(EVENTS)
@Tag(name = "Events Endpoints", description = "These endpoints manages events on PlutoSpace Events")
@RequiredArgsConstructor
public class EventApiResource {

	private final EventService eventService;

	@PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	@Operation(description = "This endpoint creates a new event on PlutoSpace Events")
	public ResponseEntity<EventResponse> createEvent(@RequestBody CreateEventRequest createEventRequest,
			UriComponentsBuilder uriComponentsBuilder) {
		EventResponse eventResponse = eventService.createEvent(createEventRequest);

		String location = uriComponentsBuilder.path(EVENTS_RESOURCE_ID).buildAndExpand(eventResponse.getId())
				.toUriString();
		URI uri = URI.create(location);
		return ResponseEntity.created(uri).body(eventResponse);
	}

	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(description = "This endpoint retrieves events")
	public ResponseEntity<CustomPageResponse<EventResponse>> retrieveEvents(@RequestParam(value = "pageNo") int pageNo,
			@RequestParam(value = "pageSize") int pageSize) {
		return ResponseEntity.ok(eventService.retrieveEvents(pageNo, pageSize));
	}

	@GetMapping(path = RESOURCE_ID + "/forms", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(description = "This endpoint retrieves forms of a particular event")
	public ResponseEntity<CustomPageResponse<EventFormResponse>> retrieveEventForms(@PathVariable String id,
			@RequestParam(value = "pageNo") int pageNo, @RequestParam(value = "pageSize") int pageSize) {
		return ResponseEntity.ok(eventService.retrieveEventForms(id, pageNo, pageSize));
	}

	@PostMapping(path = "/bulk-ids", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(description = "This endpoint retrieves bulk events by ids")
	public ResponseEntity<List<EventResponse>> retrieveEvent(@RequestBody List<String> ids) {
		return ResponseEntity.ok(eventService.retrieveEvent(ids));
	}
}
