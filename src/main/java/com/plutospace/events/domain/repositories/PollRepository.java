/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.domain.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.plutospace.events.commons.repositories.BaseRepository;
import com.plutospace.events.domain.entities.Poll;

@Repository
public interface PollRepository extends BaseRepository<Poll, String> {
	Page<Poll> findByAccountIdOrderByCreatedOnDesc(String accountId, Pageable pageable);

	List<Poll> findByIdIn(List<String> ids);

	Poll findByPublicIdAndIsPublished(String publicId, boolean isPublished);
}
