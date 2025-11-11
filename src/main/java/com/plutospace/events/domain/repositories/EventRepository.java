/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.domain.repositories;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.plutospace.events.commons.repositories.BaseRepository;
import com.plutospace.events.domain.data.response.EventResponse;
import com.plutospace.events.domain.entities.Event;

@Repository
public interface EventRepository extends BaseRepository<Event, String> {
	List<Event> findByIdIn(List<String> ids);

	List<EventResponse> findByAccountIdAndCreatedOnBetweenOrderByCreatedOnDesc(String accountId,
			LocalDateTime startDate, LocalDateTime endDate);

	Page<Event> findByAccountIdOrderByCreatedOnDesc(String accountId, Pageable pageable);
}
