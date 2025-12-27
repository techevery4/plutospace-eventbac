/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.services.implementations;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
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
import com.plutospace.events.commons.utils.LinkGenerator;
import com.plutospace.events.domain.data.PollType;
import com.plutospace.events.domain.data.request.CreatePollResultRequest;
import com.plutospace.events.domain.data.request.SavePollRequest;
import com.plutospace.events.domain.data.response.EventResponse;
import com.plutospace.events.domain.data.response.OperationalResponse;
import com.plutospace.events.domain.data.response.PollResponse;
import com.plutospace.events.domain.data.response.PollResultResponse;
import com.plutospace.events.domain.entities.Poll;
import com.plutospace.events.domain.entities.PollResult;
import com.plutospace.events.domain.repositories.PollRepository;
import com.plutospace.events.domain.repositories.PollResultRepository;
import com.plutospace.events.services.EventService;
import com.plutospace.events.services.PollService;
import com.plutospace.events.services.mappers.PollMapper;
import com.plutospace.events.validation.PollValidator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class PollServiceImpl implements PollService {

	private final PollRepository pollRepository;
	private final PollResultRepository pollResultRepository;
	private final EventService eventService;
	private final PollMapper pollMapper;
	private final PollValidator pollValidator;
	private final LinkGenerator linkGenerator;
	private final PropertyConstants propertyConstants;

	@Transactional
	@Override
	public PollResponse createPoll(SavePollRequest savePollRequest, String accountId) {
		pollValidator.validate(savePollRequest);

		Poll poll = pollMapper.toEntity(savePollRequest, accountId);
		if (StringUtils.isNotBlank(savePollRequest.getEventId())) {
			EventResponse eventResponse = eventService.retrieveEvent(savePollRequest.getEventId());

			log.info("event response {}", eventResponse);
			poll.setClosedEnded(true);
		}

		try {
			Poll savedPoll = pollRepository.save(poll);
			String publicId = linkGenerator.generatePublicLink(savedPoll.getId(), accountId, GeneralConstants.POLL,
					propertyConstants.getEventsEncryptionSecretKey());
			savedPoll.setPublicId(publicId);
			pollRepository.save(savedPoll);

			// if tied to event
			if (StringUtils.isNotBlank(savePollRequest.getEventId())) {
				OperationalResponse operationalResponse = eventService.updateEventWithPoll(savePollRequest.getEventId(),
						publicId);
				log.info("response {}", operationalResponse);
			}

			return pollMapper.toResponse(savedPoll);
		} catch (DataIntegrityViolationException e) {
			throw new DataIntegrityViolationException(e.getLocalizedMessage());
		}
	}

	@Override
	public PollResponse updatePoll(String id, String accountId, SavePollRequest savePollRequest) {
		pollValidator.validate(savePollRequest);
		PollType pollType = PollType.fromValue(savePollRequest.getType());

		Poll existingPoll = retrievePollById(id);
		if (!existingPoll.getAccountId().equals(accountId))
			throw new GeneralPlatformServiceException(GeneralConstants.MODIFY_NOT_ALLOWED_MESSAGE);
		if (pollResultRepository.existsByPollId(id))
			throw new GeneralPlatformDomainRuleException("You can no longer edit this poll");

		existingPoll.setTitle(savePollRequest.getTitle());
		existingPoll.setType(pollType);
		existingPoll.setBodies(savePollRequest.getBodies());

		try {
			if (existingPoll.getClosedEnded()) {
				EventResponse eventResponse = eventService.retrieveEventByForeignPublicId(existingPoll.getPublicId(),
						1);
				if (eventResponse == null)
					throw new GeneralPlatformDomainRuleException(
							"You cannot complete this action. Please reach out to support");
				if (StringUtils.isNotBlank(savePollRequest.getEventId())
						&& !savePollRequest.getEventId().equals(eventResponse.getId())) {
					// remove from old event
					OperationalResponse removeResponse = eventService.removePollFromEvent(eventResponse.getId());
					log.info("remove from event {}", removeResponse);
					// update to new event
					OperationalResponse operationalResponse = eventService
							.updateEventWithPoll(savePollRequest.getEventId(), existingPoll.getPublicId());
					log.info("update to event {}", operationalResponse);
				}
			}
			Poll savedPoll = pollRepository.save(existingPoll);

			return pollMapper.toResponse(savedPoll);
		} catch (DataIntegrityViolationException e) {
			throw new DataIntegrityViolationException(e.getLocalizedMessage());
		}
	}

	@Override
	public CustomPageResponse<PollResponse> retrievePolls(String accountId, int pageNo, int pageSize) {
		Pageable pageable = PageRequest.of(pageNo, pageSize);

		Page<Poll> polls = pollRepository.findByAccountIdOrderByCreatedOnDesc(accountId, pageable);

		return pollMapper.toPagedResponse(polls);
	}

	@Override
	public List<PollResponse> retrievePoll(List<String> ids) {
		List<Poll> polls = pollRepository.findByIdIn(ids);
		if (polls.isEmpty())
			return new ArrayList<>();

		return polls.stream().map(pollMapper::toResponse).toList();
	}

	@Override
	public PollResponse retrievePublishedPollByPublicId(String publicId) {
		Poll poll = pollRepository.findByPublicIdAndIsPublished(publicId, true);
		if (poll == null)
			throw new ResourceNotFoundException("Poll not available");
		Poll publicPoll = retrievePollFromPublicId(publicId);

		return pollMapper.toResponse(publicPoll);
	}

	@Override
	public OperationalResponse publishPoll(String id, String accountId) {
		Poll existingPoll = retrievePollById(id);
		if (!existingPoll.getAccountId().equals(accountId))
			throw new GeneralPlatformServiceException(GeneralConstants.MODIFY_NOT_ALLOWED_MESSAGE);

		if (existingPoll.getIsPublished())
			throw new GeneralPlatformDomainRuleException("Poll was already published");

		existingPoll.setIsPublished(true);

		try {
			pollRepository.save(existingPoll);

			return OperationalResponse.instance(GeneralConstants.SUCCESS_MESSAGE);
		} catch (DataIntegrityViolationException e) {
			throw new DataIntegrityViolationException(e.getLocalizedMessage());
		}
	}

	@Override
	public OperationalResponse unpublishPoll(String id, String accountId) {
		Poll existingPoll = retrievePollById(id);
		if (!existingPoll.getAccountId().equals(accountId))
			throw new GeneralPlatformServiceException(GeneralConstants.MODIFY_NOT_ALLOWED_MESSAGE);

		if (!existingPoll.getIsPublished())
			throw new GeneralPlatformDomainRuleException("Poll was already unpublished");

		existingPoll.setIsPublished(false);

		try {
			pollRepository.save(existingPoll);

			return OperationalResponse.instance(GeneralConstants.SUCCESS_MESSAGE);
		} catch (DataIntegrityViolationException e) {
			throw new DataIntegrityViolationException(e.getLocalizedMessage());
		}
	}

	@Override
	public OperationalResponse createPollResult(String publicId, CreatePollResultRequest createPollResultRequest) {
		pollValidator.validate(createPollResultRequest);
		String decryptedPublicId = linkGenerator.extractDetailsFromPublicLink(publicId,
				propertyConstants.getEventsEncryptionSecretKey());
		String[] words = decryptedPublicId.split(":");

		Poll poll = retrievePollById(words[0]);

		if (pollResultRepository.existsByPollIdAndEmailIgnoreCase(words[0], createPollResultRequest.getEmail()))
			throw new ResourceAlreadyExistsException("You already took this poll");
		if (!poll.getIsPublished())
			throw new GeneralPlatformDomainRuleException("Poll is not yet opened by the owner");

		PollResult pollResult = pollMapper.toEntity(createPollResultRequest, poll.getId());

		try {
			pollResultRepository.save(pollResult);

			return OperationalResponse.instance(GeneralConstants.SUCCESS_MESSAGE);
		} catch (DataIntegrityViolationException e) {
			throw new DataIntegrityViolationException(e.getLocalizedMessage());
		}
	}

	@Override
	public OperationalResponse checkIfUserAlreadySubmittedResponse(String publicId, String email) {
		String decryptedPublicId = linkGenerator.extractDetailsFromPublicLink(publicId,
				propertyConstants.getEventsEncryptionSecretKey());
		String[] words = decryptedPublicId.split(":");

		Poll poll = retrievePollById(words[0]);

		if (pollResultRepository.existsByPollIdAndEmailIgnoreCase(poll.getId(), email))
			throw new ResourceAlreadyExistsException("You already took this poll");

		return OperationalResponse.instance(GeneralConstants.SUCCESS_MESSAGE);
	}

	@Override
	public OperationalResponse deleteResponse(String publicId, String email) {
		String decryptedPublicId = linkGenerator.extractDetailsFromPublicLink(publicId,
				propertyConstants.getEventsEncryptionSecretKey());
		String[] words = decryptedPublicId.split(":");
		if (words.length < 1)
			throw new GeneralPlatformDomainRuleException("Poll link has been corrupted");

		Poll poll = retrievePollById(words[0]);
		if (!poll.getIsPublished())
			throw new GeneralPlatformDomainRuleException("Poll is not yet opened by the owner");

		PollResult existingPollResult = pollResultRepository.findByPollIdAndEmailIgnoreCase(poll.getId(), email);

		if (existingPollResult == null)
			throw new ResourceAlreadyExistsException("You have not taken this poll before");

		try {
			pollResultRepository.delete(existingPollResult);

			return OperationalResponse.instance(GeneralConstants.SUCCESS_MESSAGE);
		} catch (DataIntegrityViolationException e) {
			throw new DataIntegrityViolationException(e.getLocalizedMessage());
		}
	}

	@Override
	public CustomPageResponse<PollResultResponse> retrievePollResults(String pollId, int pageNo, int pageSize) {
		Pageable pageable = PageRequest.of(pageNo, pageSize);

		Page<PollResult> pollResults = pollResultRepository.findByPollIdOrderByCreatedOnDesc(pollId, pageable);

		return pollMapper.toPagedResultResponse(pollResults);
	}

	private Poll retrievePollById(String id) {
		return pollRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Poll Not Found"));
	}

	private Poll retrievePollFromPublicId(String publicId) {
		String decryptedLink = linkGenerator.extractDetailsFromPublicLink(publicId,
				propertyConstants.getEventsEncryptionSecretKey());
		String[] words = decryptedLink.split(":");
		if (words.length < 1)
			throw new GeneralPlatformDomainRuleException("Poll link has been corrupted");

		return retrievePollById(words[0]);
	}
}
