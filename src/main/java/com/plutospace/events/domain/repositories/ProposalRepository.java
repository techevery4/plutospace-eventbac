/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.domain.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.plutospace.events.commons.repositories.BaseRepository;
import com.plutospace.events.domain.entities.Proposal;

@Repository
public interface ProposalRepository extends BaseRepository<Proposal, String> {
	boolean existsByAccountIdAndTitleIgnoreCase(String accountId, String title);

	Page<Proposal> findByAccountIdOrderByCreatedOnDesc(String accountId, Pageable pageable);

	Page<Proposal> findByAccountIdAndTitleContainingIgnoreCaseOrderByCreatedOnDesc(String accountId, String title,
			Pageable pageable);

	List<Proposal> findByIdIn(List<String> ids);

	Proposal findByPublicIdAndIsOpen(String publicId, boolean isOpen);
}
