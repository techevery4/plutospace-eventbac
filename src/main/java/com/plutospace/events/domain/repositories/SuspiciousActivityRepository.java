/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.domain.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.plutospace.events.commons.repositories.BaseRepository;
import com.plutospace.events.domain.entities.SuspiciousActivity;

@Repository
public interface SuspiciousActivityRepository extends BaseRepository<SuspiciousActivity, String> {
	Page<SuspiciousActivity> findAllByOrderByCreatedOnDesc(Pageable pageable);
}
