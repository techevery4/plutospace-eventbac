/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.domain.repositories;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.plutospace.events.commons.repositories.BaseRepository;
import com.plutospace.events.domain.entities.EventCategory;

@Repository
public interface EventCategoryRepository extends BaseRepository<EventCategory, String> {
	boolean existsByNameIgnoreCase(String name);

	EventCategory findByNameIgnoreCase(String name);

	List<EventCategory> findByIdIn(List<String> ids);
}
