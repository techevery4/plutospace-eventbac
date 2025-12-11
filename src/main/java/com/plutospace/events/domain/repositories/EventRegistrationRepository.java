/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.domain.repositories;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Update;
import org.springframework.stereotype.Repository;

import com.plutospace.events.commons.repositories.BaseRepository;
import com.plutospace.events.domain.entities.EventRegistration;

@Repository
public interface EventRegistrationRepository extends BaseRepository<EventRegistration, String> {
	boolean existsByEmailIgnoreCase(String email);

	Page<EventRegistration> findByEventIdOrderByCreatedOnDesc(String eventId, Pageable pageable);

	List<EventRegistration> findByEmailIgnoreCaseAndEventDateBetweenOrderByEventDateAsc(String email,
			LocalDate startDate, LocalDate endDate);

	@Query(value = "{ 'eventId' : ?0 }")
	@Update(value = "{ '$set' : { 'eventDate' : ?1 } }")
	void updateEventDateByEventId(String eventId, LocalDate newEventDate);
}
