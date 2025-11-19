/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.services.mappers;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import com.plutospace.events.commons.data.CustomPageResponse;
import com.plutospace.events.domain.data.request.SavePermissionRequest;
import com.plutospace.events.domain.data.response.PermissionResponse;
import com.plutospace.events.domain.entities.Permission;

@Component
public class PermissionMapper {

	public PermissionResponse toResponse(Permission permission) {
		return PermissionResponse.instance(permission.getId(), permission.getName(), permission.getModule(),
				permission.getDescription(), permission.getEndpoint(), permission.getMethod(),
				permission.getTiedToPlan(), permission.getIsGeneral(), permission.getCreatedOn());
	}

	public Permission toEntity(SavePermissionRequest savePermissionRequest) {
		return Permission.instance(savePermissionRequest.name(), savePermissionRequest.module(),
				savePermissionRequest.description(), savePermissionRequest.endpoint(), savePermissionRequest.method(),
				savePermissionRequest.tiedToPlan(), savePermissionRequest.isGeneral());
	}

	public CustomPageResponse<PermissionResponse> toPagedResponse(Page<Permission> permissions) {
		List<PermissionResponse> permissionResponses = permissions.getContent().stream().map(this::toResponse).toList();
		long totalElements = permissions.getTotalElements();
		Pageable pageable = permissions.getPageable();
		return CustomPageResponse.resolvePageResponse(permissionResponses, totalElements, pageable);
	}
}
