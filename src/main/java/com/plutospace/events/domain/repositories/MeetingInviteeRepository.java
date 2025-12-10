/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.domain.repositories;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.plutospace.events.commons.repositories.BaseRepository;
import com.plutospace.events.domain.entities.MeetingInvitee;

@Repository
public interface MeetingInviteeRepository extends BaseRepository<MeetingInvitee, String> {
	boolean existsByMeetingIdAndEmailIgnoreCase(String meetingId, String email);

	Page<MeetingInvitee> findByMeetingId(String meetingId, Pageable pageable);

	MeetingInvitee findByMeetingIdAndEmailIgnoreCase(String meetingId, String email);

	List<MeetingInvitee> findByEmailIgnoreCaseAndMeetingStartTimeBetweenOrderByMeetingStartTimeAsc(String email,
			LocalDateTime startDate, LocalDateTime endDate);
}
