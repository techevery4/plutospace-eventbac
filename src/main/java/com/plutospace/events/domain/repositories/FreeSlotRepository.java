/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.domain.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.plutospace.events.commons.repositories.BaseRepository;
import com.plutospace.events.domain.entities.FreeSlot;

@Repository
public interface FreeSlotRepository extends BaseRepository<FreeSlot, String> {
	Page<FreeSlot> findByAccountIdAndCreatedByOrderByStartTimeDesc(String accountId, String createdBy,
			Pageable pageable);

	List<FreeSlot> findByAccountIdAndCreatedByAndIsAvailableOrderByStartTimeDesc(String accountId, String createdBy,
			boolean isAvailable);
}
