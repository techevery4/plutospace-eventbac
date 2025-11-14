/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.domain.repositories;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.plutospace.events.commons.repositories.BaseRepository;
import com.plutospace.events.domain.entities.EventRegistrationLog;

@Repository
public interface EventRegistrationLogRepository extends BaseRepository<EventRegistrationLog, String> {
	List<EventRegistrationLog> findByRegistrationIdOrderByCreatedOnDesc(String registrationId);
}
