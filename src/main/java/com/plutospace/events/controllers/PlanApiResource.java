/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.controllers;

import java.net.URI;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.plutospace.events.commons.data.CustomPageResponse;
import com.plutospace.events.domain.data.request.PlanRequest;
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
	public ResponseEntity<CustomPageResponse<PlanResponse>> retrieveAllPlans(@RequestParam(value = "pageNo") int pageNo,
			@RequestParam(value = "pageSize") int pageSize) {
		return ResponseEntity.ok(planService.retrieveAllPlans(pageNo, pageSize));
	}
}
