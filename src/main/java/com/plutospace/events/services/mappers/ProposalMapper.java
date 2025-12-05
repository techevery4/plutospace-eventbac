/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.services.mappers;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import com.plutospace.events.commons.data.CustomPageResponse;
import com.plutospace.events.domain.data.request.CreateProposalRequest;
import com.plutospace.events.domain.data.request.CreateProposalSubmissionRequest;
import com.plutospace.events.domain.data.response.ProposalResponse;
import com.plutospace.events.domain.data.response.ProposalSubmissionResponse;
import com.plutospace.events.domain.entities.Proposal;
import com.plutospace.events.domain.entities.ProposalSubmission;

@Component
public class ProposalMapper {

	public ProposalResponse toResponse(Proposal proposal) {
		return ProposalResponse.instance(proposal.getId(), proposal.getAccountId(), proposal.getTitle(),
				proposal.getPublicId(), proposal.getIsOpen(), proposal.getCreatedOn());
	}

	public ProposalSubmissionResponse toResponse(ProposalSubmission proposalSubmission) {
		return ProposalSubmissionResponse.instance(proposalSubmission.getId(), proposalSubmission.getProposalId(),
				proposalSubmission.getName(), proposalSubmission.getEmail(), proposalSubmission.getPhoneNumber(),
				proposalSubmission.getCalledForPresentation(), proposalSubmission.getMediaId(),
				proposalSubmission.getMediaUrl(), proposalSubmission.getCreatedOn());
	}

	public Proposal toEntity(CreateProposalRequest createProposalRequest, String accountId) {
		return Proposal.instance(accountId, createProposalRequest.title(), null, false);
	}

	public ProposalSubmission toEntity(CreateProposalSubmissionRequest createProposalSubmissionRequest,
			String purposeId) {
		ProposalSubmission.PhoneNumber phoneNumber = new ProposalSubmission.PhoneNumber();
		phoneNumber.setCountryCode(createProposalSubmissionRequest.countryCode());
		phoneNumber.setNumber(createProposalSubmissionRequest.phoneNumber());
		return ProposalSubmission.instance(purposeId, createProposalSubmissionRequest.name(),
				createProposalSubmissionRequest.email(), phoneNumber, false, createProposalSubmissionRequest.mediaId(),
				createProposalSubmissionRequest.mediaUrl());
	}

	public CustomPageResponse<ProposalResponse> toPagedResponse(Page<Proposal> proposals) {
		List<ProposalResponse> proposalResponses = proposals.getContent().stream().map(this::toResponse).toList();
		long totalElements = proposals.getTotalElements();
		Pageable pageable = proposals.getPageable();
		return CustomPageResponse.resolvePageResponse(proposalResponses, totalElements, pageable);
	}

	public CustomPageResponse<ProposalSubmissionResponse> toPagedSubmissionResponse(
			Page<ProposalSubmission> proposalSubmissions) {
		List<ProposalSubmissionResponse> proposalSubmissionResponses = proposalSubmissions.getContent().stream()
				.map(this::toResponse).toList();
		long totalElements = proposalSubmissions.getTotalElements();
		Pageable pageable = proposalSubmissions.getPageable();
		return CustomPageResponse.resolvePageResponse(proposalSubmissionResponses, totalElements, pageable);
	}
}
