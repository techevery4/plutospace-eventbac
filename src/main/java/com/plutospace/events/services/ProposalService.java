/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.services;

import java.util.List;

import com.plutospace.events.commons.data.CustomPageResponse;
import com.plutospace.events.domain.data.request.CreateProposalRequest;
import com.plutospace.events.domain.data.request.CreateProposalSubmissionRequest;
import com.plutospace.events.domain.data.response.OperationalResponse;
import com.plutospace.events.domain.data.response.ProposalResponse;
import com.plutospace.events.domain.data.response.ProposalSubmissionResponse;

public interface ProposalService {

	ProposalResponse createProposal(CreateProposalRequest createProposalRequest, String accountId);

	ProposalResponse updateProposal(String id, String title);

	CustomPageResponse<ProposalResponse> retrieveProposals(String accountId, int pageNo, int pageSize);

	CustomPageResponse<ProposalResponse> searchProposals(String accountId, String text, int pageNo, int pageSize);

	List<ProposalResponse> retrieveProposal(List<String> ids);

	OperationalResponse openProposal(String id);

	OperationalResponse closeProposal(String id);

	ProposalResponse retrieveProposalByPublicId(String publicId);

	OperationalResponse submitProposal(CreateProposalSubmissionRequest createProposalSubmissionRequest,
			String publicId);

	CustomPageResponse<ProposalSubmissionResponse> retrieveSubmittedProposals(String id, int pageNo, int pageSize);

	OperationalResponse deleteProposal(String id);
}
