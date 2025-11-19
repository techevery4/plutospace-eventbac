/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.services;

import java.util.List;

import com.plutospace.events.domain.data.request.CheckAdminRolePermissionRequest;
import com.plutospace.events.domain.data.request.SaveAdminRolePermissionRequest;
import com.plutospace.events.domain.data.response.OperationalResponse;
import com.plutospace.events.domain.data.response.PermissionResponse;

public interface AdminRolePermissionService {

	OperationalResponse checkIfHasPermission(CheckAdminRolePermissionRequest checkAdminRolePermissionRequest);

	List<PermissionResponse> retrievePermissionsForAdminRole(String role);

	OperationalResponse assignPermissionsToRole(SaveAdminRolePermissionRequest saveAdminRolePermissionRequest);

	OperationalResponse removePermissionFromRole(SaveAdminRolePermissionRequest saveAdminRolePermissionRequest);
}
