/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.domain.repositories;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.plutospace.events.commons.repositories.BaseRepository;
import com.plutospace.events.domain.entities.Event;

@Repository
public interface EventRepository extends BaseRepository<Event, String> {
	List<Event> findByIdIn(List<String> ids);

	Page<Event> findByAccountIdOrderByCreatedOnDesc(String accountId, Pageable pageable);

	long countByCategoryId(String categoryId);

	Event findByPollsLink(String publicId);

	List<Event> findByAccountIdAndCreatedByAndDateBetweenOrderByStartTimeAsc(String accountId, String accountUserId,
			LocalDate startDate, LocalDate endDate);

	Long countByAccountIdAndCreatedOnBetween(String accountId, LocalDateTime startTime, LocalDateTime endTime);

	Long countByAccountId(String accountId);
}
