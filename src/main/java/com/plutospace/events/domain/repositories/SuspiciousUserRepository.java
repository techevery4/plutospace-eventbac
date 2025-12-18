/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.domain.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.plutospace.events.commons.repositories.BaseRepository;
import com.plutospace.events.domain.entities.SuspiciousUser;

@Repository
public interface SuspiciousUserRepository extends BaseRepository<SuspiciousUser, String> {
	SuspiciousUser findByCreatedByAndUserAgentIgnoreCase(String createdBy, String userAgent);

	SuspiciousUser findFirstByUserAgentIgnoreCaseAndIsBlocked(String userAgent, boolean isBlocked);

	Page<SuspiciousUser> findAllByOrderByCreatedOnDesc(Pageable pageable);
}
