/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.services;

import java.util.List;

import com.plutospace.events.commons.data.CustomPageResponse;
import com.plutospace.events.domain.data.request.SavePermissionRequest;
import com.plutospace.events.domain.data.response.OperationalResponse;
import com.plutospace.events.domain.data.response.PermissionResponse;

public interface PermissionService {

	PermissionResponse createPermission(SavePermissionRequest savePermissionRequest);

	OperationalResponse createBulkPermissions(List<SavePermissionRequest> savePermissionRequests);

	PermissionResponse updatePermission(String id, SavePermissionRequest savePermissionRequest);

	CustomPageResponse<PermissionResponse> retrievePermissions(int pageNo, int pageSize);

	List<PermissionResponse> retrievePermission(List<String> ids);

	CustomPageResponse<PermissionResponse> searchPermission(String text, int pageNo, int pageSize);
}
