/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.domain.repositories;

import org.springframework.stereotype.Repository;

import com.plutospace.events.commons.repositories.BaseRepository;
import com.plutospace.events.domain.entities.PollResult;

@Repository
public interface PollResultRepository extends BaseRepository<PollResult, String> {
	boolean existsByPollId(String pollId);
}
