/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.controllers;

import java.net.URI;
import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import com.plutospace.events.commons.data.CustomPageResponse;
import com.plutospace.events.commons.definitions.GeneralConstants;
import com.plutospace.events.commons.definitions.PropertyConstants;
import com.plutospace.events.commons.utils.SecurityMapper;
import com.plutospace.events.domain.data.request.CreateEventRequest;
import com.plutospace.events.domain.data.response.EventFormResponse;
import com.plutospace.events.domain.data.response.EventResponse;
import com.plutospace.events.services.EventService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

import static com.plutospace.events.commons.definitions.ApiResourceConstants.*;

@RestController
@RequestMapping(EVENTS)
@Tag(name = "Events Endpoints", description = "These endpoints manages events on PlutoSpace Events")
@RequiredArgsConstructor
public class EventApiResource {

	private final EventService eventService;
	private final SecurityMapper securityMapper;
	private final PropertyConstants propertyConstants;
	private final HttpServletRequest request;

	@PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	@Operation(description = "This endpoint creates a new event on PlutoSpace Events")
	public ResponseEntity<EventResponse> createEvent(@RequestBody CreateEventRequest createEventRequest,
			UriComponentsBuilder uriComponentsBuilder) {
		String id = securityMapper.retrieveAccountId(request.getHeader(GeneralConstants.TOKEN_KEY),
				propertyConstants.getEventsLoginEncryptionSecretKey());
		EventResponse eventResponse = eventService.createEvent(createEventRequest, id);

		String location = uriComponentsBuilder.path(EVENTS_RESOURCE_ID).buildAndExpand(eventResponse.getId())
				.toUriString();
		URI uri = URI.create(location);
		return ResponseEntity.created(uri).body(eventResponse);
	}

	@GetMapping(path = "/all", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(description = "This endpoint retrieves events")
	public ResponseEntity<CustomPageResponse<EventResponse>> retrieveEvents(@RequestParam(name = "pageNo") int pageNo,
			@RequestParam(name = "pageSize") int pageSize) {
		return ResponseEntity.ok(eventService.retrieveEvents(pageNo, pageSize));
	}

	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(description = "This endpoint retrieves events for a particular account")
	public ResponseEntity<CustomPageResponse<EventResponse>> retrieveEventsForAccount(
			@RequestParam(name = "pageNo") int pageNo, @RequestParam(name = "pageSize") int pageSize) {
		String accountId = securityMapper.retrieveAccountId(request.getHeader(GeneralConstants.TOKEN_KEY),
				propertyConstants.getEventsLoginEncryptionSecretKey());
		return ResponseEntity.ok(eventService.retrieveEventsForAccount(accountId, pageNo, pageSize));
	}

	@GetMapping(path = RESOURCE_ID + "/forms", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(description = "This endpoint retrieves forms of a particular event")
	public ResponseEntity<CustomPageResponse<EventFormResponse>> retrieveEventForms(@PathVariable String id,
			@RequestParam(name = "pageNo") int pageNo, @RequestParam(name = "pageSize") int pageSize) {
		return ResponseEntity.ok(eventService.retrieveEventForms(id, pageNo, pageSize));
	}

	@PostMapping(path = "/bulk-ids", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(description = "This endpoint retrieves bulk events by ids")
	public ResponseEntity<List<EventResponse>> retrieveEvent(@RequestBody List<String> ids) {
		return ResponseEntity.ok(eventService.retrieveEvent(ids));
	}
}
