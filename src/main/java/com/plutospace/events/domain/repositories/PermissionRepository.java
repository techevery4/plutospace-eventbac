/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.domain.repositories;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.plutospace.events.commons.repositories.BaseRepository;
import com.plutospace.events.domain.entities.Permission;

@Repository
public interface PermissionRepository extends BaseRepository<Permission, String> {
	boolean existsByEndpointIgnoreCaseAndMethodIgnoreCase(String endpoint, String method);

	Permission findByEndpointIgnoreCaseAndMethodIgnoreCase(String endpoint, String method);

	List<Permission> findByIdIn(List<String> ids);
}
