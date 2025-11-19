/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.controllers;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.plutospace.events.domain.data.request.SaveAdminRolePermissionRequest;
import com.plutospace.events.domain.data.response.OperationalResponse;
import com.plutospace.events.domain.data.response.PermissionResponse;
import com.plutospace.events.services.AdminRolePermissionService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import static com.plutospace.events.commons.definitions.ApiResourceConstants.*;

@RestController
@RequestMapping(ADMIN_PERMISSIONS)
@Tag(name = "Admin Permissions Endpoints", description = "These endpoints manages admin permissions on PlutoSpace Events")
@RequiredArgsConstructor
public class AdminRolePermissionApiResource {

	private final AdminRolePermissionService adminRolePermissionService;

	@PostMapping(path = "/assign", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	@Operation(description = "This endpoint assigns permissions to a role")
	public ResponseEntity<OperationalResponse> assignPermissionsToRole(
			@RequestBody SaveAdminRolePermissionRequest saveAdminRolePermissionRequest) {
		return ResponseEntity.ok(adminRolePermissionService.assignPermissionsToRole(saveAdminRolePermissionRequest));
	}

	@GetMapping(path = "/{role}/permissions", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(description = "This endpoint retrieves all permissions tied to a role")
	public ResponseEntity<List<PermissionResponse>> retrievePermissionsForAdminRole(@PathVariable String role) {
		return ResponseEntity.ok(adminRolePermissionService.retrievePermissionsForAdminRole(role));
	}

	@PostMapping(path = "/remove", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	@Operation(description = "This endpoint removes permissions from a role")
	public ResponseEntity<OperationalResponse> removePermissionFromRole(
			@RequestBody SaveAdminRolePermissionRequest saveAdminRolePermissionRequest) {
		return ResponseEntity.ok(adminRolePermissionService.removePermissionFromRole(saveAdminRolePermissionRequest));
	}
}
