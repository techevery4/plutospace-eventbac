/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.domain.repositories;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.plutospace.events.commons.repositories.BaseRepository;
import com.plutospace.events.domain.data.response.MeetingResponse;
import com.plutospace.events.domain.entities.Meeting;

@Repository
public interface MeetingRepository extends BaseRepository<Meeting, String> {
	List<Meeting> findByIdIn(List<String> ids);

	List<MeetingResponse> findByAccountIdAndCreatedOnBetweenOrderByCreatedOnDesc(String accountId,
			LocalDateTime startDate, LocalDateTime endDate);
}
