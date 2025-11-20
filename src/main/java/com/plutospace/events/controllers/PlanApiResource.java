/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.controllers;

import java.net.URI;
import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.plutospace.events.commons.data.CustomPageResponse;
import com.plutospace.events.domain.data.request.PlanRequest;
import com.plutospace.events.domain.data.response.OperationalResponse;
import com.plutospace.events.domain.data.response.PlanResponse;
import com.plutospace.events.services.PlanService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import static com.plutospace.events.commons.definitions.ApiResourceConstants.*;

@RestController
@RequestMapping(PLANS)
@Tag(name = "Plans Endpoints", description = "These endpoints manages plans on PlutoSpace Events")
@RequiredArgsConstructor
public class PlanApiResource {

	private final PlanService planService;

	@PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	@Operation(description = "This endpoint creates a new plan on PlutoSpace Events")
	public ResponseEntity<PlanResponse> createPlan(@RequestBody PlanRequest planRequest,
			UriComponentsBuilder uriComponentsBuilder) throws JsonProcessingException {
		PlanResponse planResponse = planService.createPlan(planRequest);

		String location = uriComponentsBuilder.path(PLANS_RESOURCE_ID).buildAndExpand(planResponse.getId())
				.toUriString();
		URI uri = URI.create(location);
		return ResponseEntity.created(uri).body(planResponse);
	}

	@PutMapping(path = RESOURCE_ID, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	@Operation(description = "This endpoint updates a plan")
	public ResponseEntity<PlanResponse> editPlan(@RequestBody PlanRequest planRequest, @PathVariable String id) {
		return ResponseEntity.ok(planService.updatePlan(planRequest, id));
	}

	@GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(description = "This endpoint retrieves a single plan")
	public ResponseEntity<PlanResponse> retrievePlan(@PathVariable String id) {
		return ResponseEntity.ok(planService.retrievePlan(id));
	}

	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(description = "This endpoint retrieves all plans")
	public ResponseEntity<CustomPageResponse<PlanResponse>> retrieveAllPlans(@RequestParam(name = "pageNo") int pageNo,
			@RequestParam(name = "pageSize") int pageSize) {
		return ResponseEntity.ok(planService.retrieveAllPlans(pageNo, pageSize));
	}

	@PostMapping(path = "/bulk-ids", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(description = "This endpoint retrieves bulk plans by ids")
	public ResponseEntity<List<PlanResponse>> retrievePlan(@RequestBody List<String> ids) {
		return ResponseEntity.ok(planService.retrievePlan(ids));
	}

	@GetMapping(path = "/{type}/active-type", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(description = "This endpoint retrieves all active plans by type")
	public ResponseEntity<CustomPageResponse<PlanResponse>> retrieveActivePlanByType(@PathVariable String type,
			@RequestParam(name = "pageNo") int pageNo, @RequestParam(name = "pageSize") int pageSize) {
		return ResponseEntity.ok(planService.retrieveActivePlanByType(type, pageNo, pageSize));
	}

	@GetMapping(path = RESOURCE_ID + "/activate", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(description = "This endpoint activates a plan")
	public ResponseEntity<OperationalResponse> setPlanAsActive(@PathVariable String id) {
		return ResponseEntity.ok(planService.setPlanAsActive(id));
	}

	@GetMapping(path = RESOURCE_ID + "/deactivate", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(description = "This endpoint deactivates a plan")
	public ResponseEntity<OperationalResponse> setPlanAsInactive(@PathVariable String id) {
		return ResponseEntity.ok(planService.setPlanAsInactive(id));
	}
}
