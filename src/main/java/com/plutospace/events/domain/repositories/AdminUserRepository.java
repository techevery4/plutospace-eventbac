/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.domain.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.plutospace.events.commons.repositories.BaseRepository;
import com.plutospace.events.domain.entities.AdminUser;

@Repository
public interface AdminUserRepository extends BaseRepository<AdminUser, String> {
	boolean existsByEmailIgnoreCase(String email);

	Optional<AdminUser> findByEmailIgnoreCase(String email);

	List<AdminUser> findByIdIn(List<String> ids);
}
