/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.services.mappers;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import com.plutospace.events.commons.data.CustomPageResponse;
import com.plutospace.events.domain.data.AdminRoleType;
import com.plutospace.events.domain.data.AdminUserStatus;
import com.plutospace.events.domain.data.request.CreateAdminUserRequest;
import com.plutospace.events.domain.data.response.AdminUserResponse;
import com.plutospace.events.domain.entities.AdminUser;

@Component
public class AdminUserMapper {

	public AdminUserResponse toResponse(AdminUser adminUser) {
		return AdminUserResponse.instance(adminUser.getId(), adminUser.getEmail(), adminUser.getFirstName(),
				adminUser.getLastName(), adminUser.getIsPendingUser(), adminUser.getRoleType(),
				adminUser.getLastLogin(), adminUser.getCreatedOn());
	}

	public AdminUser toEntity(CreateAdminUserRequest createAdminUserRequest) {
		AdminRoleType roleType = AdminRoleType.fromValue(createAdminUserRequest.role());
		return AdminUser.instance(createAdminUserRequest.email(), createAdminUserRequest.firstName(),
				createAdminUserRequest.lastName(), createAdminUserRequest.password(), false, roleType, null,
				AdminUserStatus.ACTIVE);
	}

	public CustomPageResponse<AdminUserResponse> toPagedResponse(Page<AdminUser> adminUsers) {
		List<AdminUserResponse> adminUserResponses = adminUsers.getContent().stream().map(this::toResponse).toList();
		long totalElements = adminUsers.getTotalElements();
		Pageable pageable = adminUsers.getPageable();
		return CustomPageResponse.resolvePageResponse(adminUserResponses, totalElements, pageable);
	}
}
