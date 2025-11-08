/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.domain.repositories;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.plutospace.events.commons.repositories.BaseRepository;
import com.plutospace.events.domain.entities.Meeting;

@Repository
public interface MeetingRepository extends BaseRepository<Meeting, String> {
	List<Meeting> findByIdIn(List<String> ids);
}
