/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.controllers;

import java.net.URI;
import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import com.plutospace.events.commons.data.CustomPageResponse;
import com.plutospace.events.domain.data.request.CreateEventCategoryRequest;
import com.plutospace.events.domain.data.request.UpdateEventCategoryRequest;
import com.plutospace.events.domain.data.response.EventCategoryResponse;
import com.plutospace.events.domain.data.response.OperationalResponse;
import com.plutospace.events.services.EventCategoryService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import static com.plutospace.events.commons.definitions.ApiResourceConstants.*;

@RestController
@RequestMapping(EVENT_CATEGORIES)
@Tag(name = "Event Categories Endpoints", description = "These endpoints manages event categories on PlutoSpace Events")
@RequiredArgsConstructor
public class EventCategoryApiResource {

	private final EventCategoryService eventCategoryService;

	@PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	@Operation(description = "This endpoint creates a new event category on PlutoSpace Events")
	public ResponseEntity<EventCategoryResponse> createEventCategory(
			@RequestBody CreateEventCategoryRequest createEventCategoryRequest,
			UriComponentsBuilder uriComponentsBuilder) {
		EventCategoryResponse eventCategoryResponse = eventCategoryService
				.createEventCategory(createEventCategoryRequest);

		String location = uriComponentsBuilder.path(EVENT_CATEGORIES_RESOURCE_ID)
				.buildAndExpand(eventCategoryResponse.getId()).toUriString();
		URI uri = URI.create(location);
		return ResponseEntity.created(uri).body(eventCategoryResponse);
	}

	@PutMapping(path = RESOURCE_ID, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	@Operation(description = "This endpoint edits a particular event category")
	public ResponseEntity<EventCategoryResponse> updateEventCategory(@PathVariable String id,
			@RequestBody UpdateEventCategoryRequest updateEventCategoryRequest) {
		return ResponseEntity.ok(eventCategoryService.updateEventCategory(id, updateEventCategoryRequest));
	}

	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(description = "This endpoint retrieves event categories")
	public ResponseEntity<CustomPageResponse<EventCategoryResponse>> retrieveEventCategories(
			@RequestParam(name = "pageNo") int pageNo, @RequestParam(name = "pageSize") int pageSize) {
		return ResponseEntity.ok(eventCategoryService.retrieveEventCategories(pageNo, pageSize));
	}

	@GetMapping(path = "/list-all", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(description = "This endpoint retrieves list of all event categories")
	public ResponseEntity<List<EventCategoryResponse>> retrieveAllEventCategories() {
		return ResponseEntity.ok(eventCategoryService.retrieveAllEventCategories());
	}

	@PostMapping(path = "/bulk-ids", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(description = "This endpoint retrieves bulk event categories by ids")
	public ResponseEntity<List<EventCategoryResponse>> retrieveEventCategory(@RequestBody List<String> ids) {
		return ResponseEntity.ok(eventCategoryService.retrieveEventCategory(ids));
	}

	@DeleteMapping(path = RESOURCE_ID, produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(description = "This endpoint deletes an event category")
	public ResponseEntity<OperationalResponse> deleteEventCategory(@PathVariable String id) {
		return ResponseEntity.ok(eventCategoryService.deleteEventCategory(id));
	}

	@GetMapping(path = "/search", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(description = "This endpoint searches through event categories")
	public ResponseEntity<CustomPageResponse<EventCategoryResponse>> searchEventCategory(
			@RequestParam(name = "text") String text, @RequestParam(name = "pageNo") int pageNo,
			@RequestParam(name = "pageSize") int pageSize) {
		return ResponseEntity.ok(eventCategoryService.searchEventCategory(text, pageNo, pageSize));
	}
}
