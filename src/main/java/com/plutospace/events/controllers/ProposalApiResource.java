/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.controllers;

import java.net.URI;
import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import com.plutospace.events.commons.data.CustomPageResponse;
import com.plutospace.events.commons.definitions.GeneralConstants;
import com.plutospace.events.commons.definitions.PropertyConstants;
import com.plutospace.events.commons.utils.SecurityMapper;
import com.plutospace.events.domain.data.request.*;
import com.plutospace.events.domain.data.response.*;
import com.plutospace.events.services.ProposalService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

import static com.plutospace.events.commons.definitions.ApiResourceConstants.*;

@RestController
@RequestMapping(PROPOSALS)
@Tag(name = "Proposal Endpoints", description = "These endpoints manages proposals on PlutoSpace Events")
@RequiredArgsConstructor
public class ProposalApiResource {

	private final ProposalService proposalService;
	private final SecurityMapper securityMapper;
	private final PropertyConstants propertyConstants;
	private final HttpServletRequest request;

	@PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	@Operation(description = "This endpoint creates a new proposal on PlutoSpace Events")
	public ResponseEntity<ProposalResponse> createProposal(@RequestBody CreateProposalRequest createProposalRequest,
			UriComponentsBuilder uriComponentsBuilder) {
		String accountId = securityMapper.retrieveAccountId(request.getHeader(GeneralConstants.TOKEN_KEY),
				propertyConstants.getEventsLoginEncryptionSecretKey());
		ProposalResponse proposalResponse = proposalService.createProposal(createProposalRequest, accountId);

		String location = uriComponentsBuilder.path(PROPOSALS_RESOURCE_ID).buildAndExpand(proposalResponse.getId())
				.toUriString();
		URI uri = URI.create(location);
		return ResponseEntity.created(uri).body(proposalResponse);
	}

	@PutMapping(path = RESOURCE_ID, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	@Operation(description = "This endpoint updates a proposal")
	public ResponseEntity<ProposalResponse> updateProposal(@PathVariable String id,
			@RequestBody CreateProposalRequest createProposalRequest) {
		String accountId = securityMapper.retrieveAccountId(request.getHeader(GeneralConstants.TOKEN_KEY),
				propertyConstants.getEventsLoginEncryptionSecretKey());
		return ResponseEntity.ok(proposalService.updateProposal(id, accountId, createProposalRequest.title()));
	}

	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(description = "This endpoint retrieves all proposals")
	public ResponseEntity<CustomPageResponse<ProposalResponse>> retrieveProposals(
			@RequestParam(name = "pageNo") int pageNo, @RequestParam(name = "pageSize") int pageSize) {
		String accountId = securityMapper.retrieveAccountId(request.getHeader(GeneralConstants.TOKEN_KEY),
				propertyConstants.getEventsLoginEncryptionSecretKey());
		return ResponseEntity.ok(proposalService.retrieveProposals(accountId, pageNo, pageSize));
	}

	@GetMapping(path = "/search", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(description = "This endpoint searches through proposals")
	public ResponseEntity<CustomPageResponse<ProposalResponse>> searchProposals(
			@RequestParam(name = "text") String text, @RequestParam(name = "pageNo") int pageNo,
			@RequestParam(name = "pageSize") int pageSize) {
		String accountId = securityMapper.retrieveAccountId(request.getHeader(GeneralConstants.TOKEN_KEY),
				propertyConstants.getEventsLoginEncryptionSecretKey());
		return ResponseEntity.ok(proposalService.searchProposals(accountId, text, pageNo, pageSize));
	}

	@PostMapping(path = "/bulk-ids", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(description = "This endpoint retrieves bulk proposals by ids")
	public ResponseEntity<List<ProposalResponse>> retrieveProposal(@RequestBody List<String> ids) {
		return ResponseEntity.ok(proposalService.retrieveProposal(ids));
	}

	@GetMapping(path = RESOURCE_ID + "/open", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(description = "This endpoint opens a proposal for submissions")
	public ResponseEntity<OperationalResponse> openProposal(@PathVariable String id) {
		String accountId = securityMapper.retrieveAccountId(request.getHeader(GeneralConstants.TOKEN_KEY),
				propertyConstants.getEventsLoginEncryptionSecretKey());
		return ResponseEntity.ok(proposalService.openProposal(id, accountId));
	}

	@GetMapping(path = RESOURCE_ID + "/close", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(description = "This endpoint closes a proposal for submission")
	public ResponseEntity<OperationalResponse> closeProposal(@PathVariable String id) {
		String accountId = securityMapper.retrieveAccountId(request.getHeader(GeneralConstants.TOKEN_KEY),
				propertyConstants.getEventsLoginEncryptionSecretKey());
		return ResponseEntity.ok(proposalService.closeProposal(id, accountId));
	}

	@GetMapping(path = "/details", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(description = "This endpoint retrieves proposal by the public id")
	public ResponseEntity<ProposalResponse> retrieveProposalByPublicId(@RequestParam(name = "pid") String pid) {
		return ResponseEntity.ok(proposalService.retrieveProposalByPublicId(pid));
	}

	@PostMapping(path = "/submissions", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	@Operation(description = "This endpoint submits a client's proposal using the proposal public link")
	public ResponseEntity<OperationalResponse> submitProposal(
			@RequestBody CreateProposalSubmissionRequest createProposalSubmissionRequest,
			@RequestParam(name = "pid") String pid) {
		return ResponseEntity.ok(proposalService.submitProposal(createProposalSubmissionRequest, pid));
	}

	@GetMapping(path = RESOURCE_ID + "/submissions", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(description = "This endpoint retrieves all submissions of a proposal")
	public ResponseEntity<CustomPageResponse<ProposalSubmissionResponse>> retrieveSubmittedProposals(
			@PathVariable String id, @RequestParam(name = "pageNo") int pageNo,
			@RequestParam(name = "pageSize") int pageSize) {
		return ResponseEntity.ok(proposalService.retrieveSubmittedProposals(id, pageNo, pageSize));
	}

	@DeleteMapping(path = RESOURCE_ID, produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(description = "This endpoint deletes a proposal")
	public ResponseEntity<OperationalResponse> deleteProposal(@PathVariable String id) {
		String accountId = securityMapper.retrieveAccountId(request.getHeader(GeneralConstants.TOKEN_KEY),
				propertyConstants.getEventsLoginEncryptionSecretKey());
		return ResponseEntity.ok(proposalService.deleteProposal(id, accountId));
	}

	@PostMapping(path = "/submissions/search", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(description = "This endpoint searches through proposal submissions")
	public ResponseEntity<List<ProposalSubmissionResponse>> searchProposalSubmissions(@RequestBody List<String> texts,
			@RequestParam(name = "proposalId") String proposalId, @RequestParam(name = "pageNo") int pageNo,
			@RequestParam(name = "pageSize") int pageSize) {
		String accountId = securityMapper.retrieveAccountId(request.getHeader(GeneralConstants.TOKEN_KEY),
				propertyConstants.getEventsLoginEncryptionSecretKey());
		return ResponseEntity
				.ok(proposalService.searchProposalSubmissions(accountId, proposalId, texts, pageNo, pageSize));
	}
}
