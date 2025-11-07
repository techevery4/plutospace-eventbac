/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.domain.repositories;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.plutospace.events.commons.repositories.BaseRepository;
import com.plutospace.events.domain.entities.Event;

@Repository
public interface EventRepository extends BaseRepository<Event, String> {
	List<Event> findByIdIn(List<String> ids);
}
