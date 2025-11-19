/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.domain.repositories;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.plutospace.events.commons.repositories.BaseRepository;
import com.plutospace.events.domain.data.AdminRoleType;
import com.plutospace.events.domain.entities.AdminRolePermission;

@Repository
public interface AdminRolePermissionRepository extends BaseRepository<AdminRolePermission, String> {
	boolean existsByRoleAndPermissionId(AdminRoleType role, String permissionId);

	List<AdminRolePermission> findByRoleAndPermissionIdIn(AdminRoleType role, List<String> permissionIds);

	List<AdminRolePermission> findByRole(AdminRoleType roleType);
}
