/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.controllers;

import java.net.URI;
import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import com.plutospace.events.commons.data.CustomPageResponse;
import com.plutospace.events.domain.data.request.SavePermissionRequest;
import com.plutospace.events.domain.data.response.OperationalResponse;
import com.plutospace.events.domain.data.response.PermissionResponse;
import com.plutospace.events.services.PermissionService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import static com.plutospace.events.commons.definitions.ApiResourceConstants.*;

@RestController
@RequestMapping(PERMISSIONS)
@Tag(name = "Permissions Endpoints", description = "These endpoints manages permissions on PlutoSpace Events")
@RequiredArgsConstructor
public class PermissionApiResource {

	private final PermissionService permissionService;

	@PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	@Operation(description = "This endpoint creates a new permission on PlutoSpace Events")
	public ResponseEntity<PermissionResponse> createPermission(@RequestBody SavePermissionRequest savePermissionRequest,
			UriComponentsBuilder uriComponentsBuilder) {
		PermissionResponse permissionResponse = permissionService.createPermission(savePermissionRequest);

		String location = uriComponentsBuilder.path(PERMISSIONS_RESOURCE_ID).buildAndExpand(permissionResponse.getId())
				.toUriString();
		URI uri = URI.create(location);
		return ResponseEntity.created(uri).body(permissionResponse);
	}

	@PostMapping(path = "/bulk", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	@Operation(description = "This endpoint creates bulk permissions on PlutoSpace Events")
	public ResponseEntity<OperationalResponse> createBulkPermissions(
			@RequestBody List<SavePermissionRequest> savePermissionRequests) {
		return ResponseEntity.ok(permissionService.createBulkPermissions(savePermissionRequests));
	}

	@PutMapping(path = RESOURCE_ID, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	@Operation(description = "This endpoint updates a permission on PlutoSpace Events")
	public ResponseEntity<PermissionResponse> updatePermission(@PathVariable String id,
			@RequestBody SavePermissionRequest savePermissionRequest) {
		return ResponseEntity.ok(permissionService.updatePermission(id, savePermissionRequest));
	}

	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(description = "This endpoint retrieves all permissions")
	public ResponseEntity<CustomPageResponse<PermissionResponse>> retrievePermissions(
			@RequestParam(name = "pageNo") int pageNo, @RequestParam(name = "pageSize") int pageSize) {
		return ResponseEntity.ok(permissionService.retrievePermissions(pageNo, pageSize));
	}

	@PostMapping(path = "/bulk-ids", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(description = "This endpoint retrieves bulk permissions by ids")
	public ResponseEntity<List<PermissionResponse>> retrievePermission(@RequestBody List<String> ids) {
		return ResponseEntity.ok(permissionService.retrievePermission(ids));
	}
}
