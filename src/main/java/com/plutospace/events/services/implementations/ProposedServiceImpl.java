/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.services.implementations;

import java.util.ArrayList;
import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.plutospace.events.commons.data.CustomPageResponse;
import com.plutospace.events.commons.definitions.GeneralConstants;
import com.plutospace.events.commons.definitions.PropertyConstants;
import com.plutospace.events.commons.exception.GeneralPlatformDomainRuleException;
import com.plutospace.events.commons.exception.GeneralPlatformServiceException;
import com.plutospace.events.commons.exception.ResourceAlreadyExistsException;
import com.plutospace.events.commons.exception.ResourceNotFoundException;
import com.plutospace.events.commons.utils.DocumentManager;
import com.plutospace.events.commons.utils.LinkGenerator;
import com.plutospace.events.domain.data.request.CreateProposalRequest;
import com.plutospace.events.domain.data.request.CreateProposalSubmissionRequest;
import com.plutospace.events.domain.data.response.OperationalResponse;
import com.plutospace.events.domain.data.response.ProposalResponse;
import com.plutospace.events.domain.data.response.ProposalSubmissionResponse;
import com.plutospace.events.domain.entities.Proposal;
import com.plutospace.events.domain.entities.ProposalSubmission;
import com.plutospace.events.domain.entities.ProposalSubmissionData;
import com.plutospace.events.domain.repositories.ProposalRepository;
import com.plutospace.events.domain.repositories.ProposalSubmissionDataRepository;
import com.plutospace.events.domain.repositories.ProposalSubmissionRepository;
import com.plutospace.events.domain.repositories.ProposalSubmissionSearchRepository;
import com.plutospace.events.services.ProposalService;
import com.plutospace.events.services.mappers.ProposalMapper;
import com.plutospace.events.validation.ProposalValidator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProposedServiceImpl implements ProposalService {

	private final ProposalRepository proposalRepository;
	private final ProposalSubmissionRepository proposalSubmissionRepository;
	private final ProposalSubmissionDataRepository proposalSubmissionDataRepository;
	private final ProposalSubmissionSearchRepository proposalSubmissionSearchRepository;
	private final ProposalMapper proposalMapper;
	private final ProposalValidator proposalValidator;
	private final LinkGenerator linkGenerator;
	private final PropertyConstants propertyConstants;
	private final DocumentManager documentManager;

	@Override
	public ProposalResponse createProposal(CreateProposalRequest createProposalRequest, String accountId) {
		proposalValidator.validate(createProposalRequest);

		if (proposalRepository.existsByAccountIdAndTitleIgnoreCase(accountId, createProposalRequest.title()))
			throw new ResourceAlreadyExistsException("Proposal already exists");

		Proposal proposal = proposalMapper.toEntity(createProposalRequest, accountId);

		try {
			Proposal savedProposal = proposalRepository.save(proposal);
			String publicId = linkGenerator.generatePublicLink(savedProposal.getId(), accountId,
					GeneralConstants.PROPOSAL, propertyConstants.getEventsEncryptionSecretKey());
			savedProposal.setPublicId(publicId);
			proposalRepository.save(savedProposal);

			return proposalMapper.toResponse(savedProposal);
		} catch (DataIntegrityViolationException e) {
			throw new DataIntegrityViolationException(e.getLocalizedMessage());
		}
	}

	@Override
	public ProposalResponse updateProposal(String id, String accountId, String title) {
		Proposal existingProposal = retrieveProposalById(id);
		if (!existingProposal.getAccountId().equals(accountId))
			throw new GeneralPlatformServiceException(GeneralConstants.MODIFY_NOT_ALLOWED_MESSAGE);

		if (proposalRepository.existsByAccountIdAndTitleIgnoreCase(existingProposal.getAccountId(), title))
			throw new ResourceAlreadyExistsException("Proposal already exists");

		existingProposal.setTitle(title);

		try {
			Proposal savedProposal = proposalRepository.save(existingProposal);

			return proposalMapper.toResponse(savedProposal);
		} catch (DataIntegrityViolationException e) {
			throw new DataIntegrityViolationException(e.getLocalizedMessage());
		}
	}

	@Override
	public CustomPageResponse<ProposalResponse> retrieveProposals(String accountId, int pageNo, int pageSize) {
		Pageable pageable = PageRequest.of(pageNo, pageSize);

		Page<Proposal> proposals = proposalRepository.findByAccountIdOrderByCreatedOnDesc(accountId, pageable);

		return proposalMapper.toPagedResponse(proposals);
	}

	@Override
	public CustomPageResponse<ProposalResponse> searchProposals(String accountId, String text, int pageNo,
			int pageSize) {
		Pageable pageable = PageRequest.of(pageNo, pageSize);

		Page<Proposal> proposals = proposalRepository
				.findByAccountIdAndTitleContainingIgnoreCaseOrderByCreatedOnDesc(accountId, text, pageable);

		return proposalMapper.toPagedResponse(proposals);
	}

	@Override
	public List<ProposalResponse> retrieveProposal(List<String> ids) {
		List<Proposal> proposals = proposalRepository.findByIdIn(ids);
		if (proposals.isEmpty())
			return new ArrayList<>();

		return proposals.stream().map(proposalMapper::toResponse).toList();
	}

	@Override
	public OperationalResponse openProposal(String id, String accountId) {
		Proposal existingProposal = retrieveProposalById(id);
		if (!existingProposal.getAccountId().equals(accountId))
			throw new GeneralPlatformServiceException(GeneralConstants.MODIFY_NOT_ALLOWED_MESSAGE);

		if (existingProposal.getIsOpen())
			throw new GeneralPlatformDomainRuleException("Proposal already opened");

		existingProposal.setIsOpen(true);

		try {
			proposalRepository.save(existingProposal);

			return OperationalResponse.instance(GeneralConstants.SUCCESS_MESSAGE);
		} catch (DataIntegrityViolationException e) {
			throw new DataIntegrityViolationException(e.getLocalizedMessage());
		}
	}

	@Override
	public OperationalResponse closeProposal(String id, String accountId) {
		Proposal existingProposal = retrieveProposalById(id);
		if (!existingProposal.getAccountId().equals(accountId))
			throw new GeneralPlatformServiceException(GeneralConstants.MODIFY_NOT_ALLOWED_MESSAGE);

		if (!existingProposal.getIsOpen())
			throw new GeneralPlatformDomainRuleException("Proposal already closed");

		existingProposal.setIsOpen(false);

		try {
			proposalRepository.save(existingProposal);

			return OperationalResponse.instance(GeneralConstants.SUCCESS_MESSAGE);
		} catch (DataIntegrityViolationException e) {
			throw new DataIntegrityViolationException(e.getLocalizedMessage());
		}
	}

	@Override
	public ProposalResponse retrieveProposalByPublicId(String publicId) {
		Proposal proposal = proposalRepository.findByPublicIdAndIsOpen(publicId, true);
		if (proposal == null)
			throw new ResourceNotFoundException("Proposal submission is closed");

		return proposalMapper.toResponse(proposal);
	}

	@Transactional
	@Override
	public OperationalResponse submitProposal(CreateProposalSubmissionRequest createProposalSubmissionRequest,
			String publicId) {
		proposalValidator.validate(createProposalSubmissionRequest);

		String decryptedPublicId = linkGenerator.extractDetailsFromPublicLink(publicId,
				propertyConstants.getEventsEncryptionSecretKey());
		String[] words = decryptedPublicId.split(":");
		if (words.length != 3)
			throw new GeneralPlatformDomainRuleException("Please check the proposal link as it appears corrupt");

		Proposal proposal = retrieveProposalById(words[0]);
		ProposalSubmission proposalSubmission = proposalMapper.toEntity(createProposalSubmissionRequest,
				proposal.getId());

		try {
			DocumentManager.TikaResult tikaResult = documentManager
					.extractContentAndMetadata(proposalSubmission.getMediaUrl());
			ProposalSubmission savedProposalSubmission = proposalSubmissionRepository.save(proposalSubmission);
			ProposalSubmissionData proposalSubmissionData = new ProposalSubmissionData();
			proposalSubmissionData.setProposalId(savedProposalSubmission.getProposalId());
			proposalSubmissionData.setProposalSubmissionId(savedProposalSubmission.getId());
			proposalSubmissionData.setContent(tikaResult.getContent());
			proposalSubmissionDataRepository.save(proposalSubmissionData);

			return OperationalResponse.instance(GeneralConstants.SUCCESS_MESSAGE);
		} catch (DataIntegrityViolationException e) {
			throw new DataIntegrityViolationException(e.getLocalizedMessage());
		} catch (Exception e) {
			throw new GeneralPlatformServiceException(
					"There is an issue processing your proposal submission at the moment. Please try again later");
		}
	}

	@Override
	public CustomPageResponse<ProposalSubmissionResponse> retrieveSubmittedProposals(String id, int pageNo,
			int pageSize) {
		Pageable pageable = PageRequest.of(pageNo, pageSize);

		Page<ProposalSubmission> proposalSubmissions = proposalSubmissionRepository
				.findByProposalIdOrderByCreatedOnDesc(id, pageable);

		return proposalMapper.toPagedSubmissionResponse(proposalSubmissions);
	}

	@Override
	public OperationalResponse deleteProposal(String id, String accountId) {
		Proposal existingProposal = retrieveProposalById(id);
		if (!existingProposal.getAccountId().equals(accountId))
			throw new GeneralPlatformServiceException(GeneralConstants.MODIFY_NOT_ALLOWED_MESSAGE);
		if (existingProposal.getIsOpen())
			throw new GeneralPlatformDomainRuleException(
					"Please close the proposal submissions before performing this action");

		try {
			proposalSubmissionRepository.deleteByProposalId(existingProposal.getId());
			proposalRepository.delete(existingProposal);

			return OperationalResponse.instance(GeneralConstants.SUCCESS_MESSAGE);
		} catch (DataIntegrityViolationException e) {
			throw new DataIntegrityViolationException(e.getLocalizedMessage());
		}
	}

	@Override
	public List<ProposalSubmissionResponse> searchProposalSubmissions(String accountId, String proposalId,
			List<String> texts, int pageNo, int pageSize) {
		if (pageSize > 30)
			throw new GeneralPlatformDomainRuleException("Kindly reduce page size");
		if (texts.isEmpty())
			throw new GeneralPlatformDomainRuleException("Please provide search texts");
		Proposal existingProposal = retrieveProposalById(proposalId);
		if (!accountId.equals(existingProposal.getAccountId()))
			throw new GeneralPlatformServiceException("This action is not allowed");

		List<ProposalSubmissionData> proposalSubmissionData = proposalSubmissionSearchRepository
				.searchMultipleSubstringsIgnoreCaseByProposalId(texts, existingProposal.getId(), pageNo, pageSize);
		if (proposalSubmissionData.isEmpty())
			return new ArrayList<>();

		List<String> proposalSubmissionIds = proposalSubmissionData.stream()
				.map(ProposalSubmissionData::getProposalSubmissionId).toList();
		List<ProposalSubmission> proposalSubmissions = proposalSubmissionRepository.findByIdIn(proposalSubmissionIds);

		return proposalSubmissions.stream().map(proposalMapper::toResponse).toList();
	}

	private Proposal retrieveProposalById(String id) {
		return proposalRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Poll Not Found"));
	}
}
