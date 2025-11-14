/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.domain.repositories;

import java.util.List;

import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.plutospace.events.commons.repositories.BaseRepository;
import com.plutospace.events.domain.entities.EventRegistrationData;

@Repository
public interface EventRegistrationDataRepository extends BaseRepository<EventRegistrationData, String> {
	@Query("SELECT u FROM EventRegistrationData e WHERE LOWER(e.email) IN :emails")
	List<EventRegistrationData> findByEmailIgnoreCaseIn(@Param("emails") List<String> eventRegistrationEmails);
}
