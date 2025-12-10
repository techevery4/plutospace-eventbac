/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.domain.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.plutospace.events.commons.repositories.BaseRepository;
import com.plutospace.events.domain.entities.ProposalSubmission;

@Repository
public interface ProposalSubmissionRepository extends BaseRepository<ProposalSubmission, String> {
	Page<ProposalSubmission> findByProposalIdOrderByCreatedOnDesc(String proposalId, Pageable pageable);

	void deleteByProposalId(String proposalId);

	List<ProposalSubmission> findByIdIn(List<String> proposalSubmissionIds);
}
