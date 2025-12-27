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

import com.plutospace.events.commons.data.CustomPageResponse;
import com.plutospace.events.commons.definitions.GeneralConstants;
import com.plutospace.events.commons.definitions.PropertyConstants;
import com.plutospace.events.commons.exception.GeneralPlatformDomainRuleException;
import com.plutospace.events.commons.exception.GeneralPlatformServiceException;
import com.plutospace.events.commons.exception.ResourceNotFoundException;
import com.plutospace.events.commons.utils.LinkGenerator;
import com.plutospace.events.domain.data.request.AnswerQuestionRequest;
import com.plutospace.events.domain.data.request.AskQuestionRequest;
import com.plutospace.events.domain.data.request.CreateQuestionAndAnswerRequest;
import com.plutospace.events.domain.data.response.*;
import com.plutospace.events.domain.entities.QuestionAndAnswer;
import com.plutospace.events.domain.entities.QuestionAndAnswerDetail;
import com.plutospace.events.domain.repositories.QuestionAndAnswerDetailRepository;
import com.plutospace.events.domain.repositories.QuestionAndAnswerRepository;
import com.plutospace.events.intelligence.search.DatabaseSearchService;
import com.plutospace.events.services.EventService;
import com.plutospace.events.services.QuestionAndAnswerService;
import com.plutospace.events.services.mappers.QuestionAndAnswerMapper;
import com.plutospace.events.validation.QuestionAndAnswerValidator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class QuestionAndAnswerServiceImpl implements QuestionAndAnswerService {

	private final QuestionAndAnswerRepository questionAndAnswerRepository;
	private final QuestionAndAnswerDetailRepository questionAndAnswerDetailRepository;
	private final EventService eventService;
	private final DatabaseSearchService databaseSearchService;
	private final QuestionAndAnswerMapper questionAndAnswerMapper;
	private final QuestionAndAnswerValidator questionAndAnswerValidator;
	private final LinkGenerator linkGenerator;
	private final PropertyConstants propertyConstants;

	@Override
	public QuestionAndAnswerResponse createQuestionAndAnswer(
			CreateQuestionAndAnswerRequest createQuestionAndAnswerRequest, String accountId) {
		questionAndAnswerValidator.validate(createQuestionAndAnswerRequest);

		QuestionAndAnswer questionAndAnswer = questionAndAnswerMapper.toEntity(createQuestionAndAnswerRequest,
				accountId);
		if (StringUtils.isNotBlank(createQuestionAndAnswerRequest.eventId())) {
			EventResponse eventResponse = eventService.retrieveEvent(createQuestionAndAnswerRequest.eventId());

			log.info("event response {}", eventResponse);
			questionAndAnswer.setClosedEnded(true);
		}

		try {
			QuestionAndAnswer savedQuestionAndAnswer = questionAndAnswerRepository.save(questionAndAnswer);
			String publicId = linkGenerator.generatePublicLink(savedQuestionAndAnswer.getId(), accountId,
					GeneralConstants.Q_AND_A, propertyConstants.getEventsEncryptionSecretKey());
			savedQuestionAndAnswer.setPublicId(publicId);
			questionAndAnswerRepository.save(savedQuestionAndAnswer);

			// if tied to event
			if (StringUtils.isNotBlank(createQuestionAndAnswerRequest.eventId())) {
				OperationalResponse operationalResponse = eventService
						.updateEventWithQuestionAndAnswer(createQuestionAndAnswerRequest.eventId(), publicId);
				log.info("response {}", operationalResponse);
			}

			return questionAndAnswerMapper.toResponse(savedQuestionAndAnswer);
		} catch (DataIntegrityViolationException e) {
			throw new DataIntegrityViolationException(e.getLocalizedMessage());
		}
	}

	@Override
	public OperationalResponse askQuestion(AskQuestionRequest askQuestionRequest, String publicId) {
		questionAndAnswerValidator.validate(askQuestionRequest);

		QuestionAndAnswerResponse questionAndAnswerResponse = retrieveQuestionAndAnswerSessionByPublicId(publicId);
		QuestionAndAnswerDetail questionAndAnswerDetail = questionAndAnswerMapper.toEntity(askQuestionRequest,
				questionAndAnswerResponse.getId());

		try {
			questionAndAnswerDetailRepository.save(questionAndAnswerDetail);

			return OperationalResponse.instance(GeneralConstants.SUCCESS_MESSAGE);
		} catch (DataIntegrityViolationException e) {
			throw new DataIntegrityViolationException(e.getLocalizedMessage());
		}
	}

	@Override
	public QuestionAndAnswerResponse updateQuestionAndAnswer(String id, String title, String accountId) {
		if (StringUtils.isBlank(title))
			throw new GeneralPlatformDomainRuleException("Title is required");

		QuestionAndAnswer existingQuestionAndAnswer = retrieveQuestionAndAnswerById(id);
		if (!accountId.equals(existingQuestionAndAnswer.getAccountId()))
			throw new GeneralPlatformDomainRuleException(GeneralConstants.MODIFY_NOT_ALLOWED_MESSAGE);

		existingQuestionAndAnswer.setTitle(title);

		try {
			QuestionAndAnswer savedQuestionAndAnswer = questionAndAnswerRepository.save(existingQuestionAndAnswer);

			return questionAndAnswerMapper.toResponse(savedQuestionAndAnswer);
		} catch (DataIntegrityViolationException e) {
			throw new DataIntegrityViolationException(e.getLocalizedMessage());
		}
	}

	@Override
	public OperationalResponse editAskedQuestion(String id, String question) {
		if (StringUtils.isBlank(question))
			throw new GeneralPlatformDomainRuleException("Question is required");

		QuestionAndAnswerDetail existingQuestionAndAnswerDetail = retrieveQuestionAndAnswerDetailById(id);
		if (existingQuestionAndAnswerDetail.getIsAnswered())
			throw new GeneralPlatformDomainRuleException("Question cannot be edited any longer");

		existingQuestionAndAnswerDetail.setQuestion(question);

		try {
			questionAndAnswerDetailRepository.save(existingQuestionAndAnswerDetail);

			return OperationalResponse.instance(GeneralConstants.SUCCESS_MESSAGE);
		} catch (DataIntegrityViolationException e) {
			throw new DataIntegrityViolationException(e.getLocalizedMessage());
		}
	}

	@Override
	public OperationalResponse answerAQuestion(AnswerQuestionRequest answerQuestionRequest, String id,
			String accountId) {
		questionAndAnswerValidator.validate(answerQuestionRequest);

		QuestionAndAnswerDetail existingQuestionAndAnswerDetail = retrieveQuestionAndAnswerDetailById(id);
		QuestionAndAnswer existingQuestionAndAnswer = retrieveQuestionAndAnswerById(
				existingQuestionAndAnswerDetail.getQuestionAnswerId());
		if (!accountId.equals(existingQuestionAndAnswer.getAccountId()))
			throw new GeneralPlatformServiceException(GeneralConstants.MODIFY_NOT_ALLOWED_MESSAGE);

		existingQuestionAndAnswerDetail.setIsAnswered(true);
		existingQuestionAndAnswerDetail.setAnswer(answerQuestionRequest.answer());

		try {
			questionAndAnswerDetailRepository.save(existingQuestionAndAnswerDetail);

			return OperationalResponse.instance(GeneralConstants.SUCCESS_MESSAGE);
		} catch (DataIntegrityViolationException e) {
			throw new DataIntegrityViolationException(e.getLocalizedMessage());
		}
	}

	@Override
	public OperationalResponse markAsAnswered(List<String> ids, String accountId) {
		List<QuestionAndAnswerDetail> questionAndAnswerDetails = questionAndAnswerDetailRepository.findByIdIn(ids);
		if (questionAndAnswerDetails.isEmpty())
			throw new GeneralPlatformServiceException(GeneralConstants.MODIFY_NOT_ALLOWED_MESSAGE);

		String questionAndAnswerId = questionAndAnswerDetails.get(0).getQuestionAnswerId();
		QuestionAndAnswer existingQuestionAndAnswer = retrieveQuestionAndAnswerById(questionAndAnswerId);
		if (!accountId.equals(existingQuestionAndAnswer.getAccountId()))
			throw new GeneralPlatformServiceException(GeneralConstants.MODIFY_NOT_ALLOWED_MESSAGE);

		List<QuestionAndAnswerDetail> savedQuestionAndAnswerDetail = new ArrayList<>();
		for (QuestionAndAnswerDetail questionAndAnswerDetail : questionAndAnswerDetails) {
			if (!questionAndAnswerId.equals(questionAndAnswerDetail.getQuestionAnswerId()))
				throw new GeneralPlatformServiceException(GeneralConstants.MODIFY_NOT_ALLOWED_MESSAGE);
			if (questionAndAnswerDetail.getIsAnswered())
				throw new GeneralPlatformDomainRuleException(
						"One of the questions is already answered. Please check and retry the process.");

			questionAndAnswerDetail.setIsAnswered(true);
			questionAndAnswerDetail.setAnswer(GeneralConstants.QUESTION_ANSWERED_MESSAGE);
			savedQuestionAndAnswerDetail.add(questionAndAnswerDetail);
		}

		try {
			questionAndAnswerDetailRepository.saveAll(savedQuestionAndAnswerDetail);

			return OperationalResponse.instance(GeneralConstants.SUCCESS_MESSAGE);
		} catch (DataIntegrityViolationException e) {
			throw new DataIntegrityViolationException(e.getLocalizedMessage());
		}
	}

	@Override
	public QuestionAndAnswerResponse retrieveQuestionAndAnswerSessionByPublicId(String publicId) {
		QuestionAndAnswer existingQuestionAndAnswer = retrieveQuestionAndAnswerFromPublicId(publicId);
		if (!existingQuestionAndAnswer.getIsPublished())
			throw new GeneralPlatformDomainRuleException("Question and answer session is unavailable");

		return questionAndAnswerMapper.toResponse(existingQuestionAndAnswer);
	}

	@Override
	public CustomPageResponse<QuestionAndAnswerResponse> retrieveQuestionAndAnswerSessions(String accountId, int pageNo,
			int pageSize) {
		Pageable pageable = PageRequest.of(pageNo, pageSize);

		Page<QuestionAndAnswer> questionAndAnswers = questionAndAnswerRepository
				.findByAccountIdOrderByCreatedOnDesc(accountId, pageable);

		return questionAndAnswerMapper.toPagedResponse(questionAndAnswers);
	}

	@Override
	public CustomPageResponse<QuestionAndAnswerResponse> searchQuestionAndAnswerSessions(String text, String accountId,
			int pageNo, int pageSize) {
		Pageable pageable = PageRequest.of(pageNo, pageSize);

		List<String> fields = List.of("title", "publicId");
		Page<QuestionAndAnswer> questionAndAnswers = databaseSearchService
				.findQuestionAndAnswerByDynamicFilter(accountId, text, fields, pageable);
		if (questionAndAnswers.getTotalElements() == 0)
			return new CustomPageResponse<>();

		return questionAndAnswerMapper.toPagedResponse(questionAndAnswers);
	}

	@Override
	public CustomPageResponse<QuestionAndAnswerDetailResponse> retrieveUnansweredQuestions(String id, int pageNo,
			int pageSize) {
		Pageable pageable = PageRequest.of(pageNo, pageSize);

		Page<QuestionAndAnswerDetail> questionAndAnswerDetails = questionAndAnswerDetailRepository
				.findByQuestionAnswerIdAndIsAnsweredOrderByCreatedOnDesc(id, false, pageable);

		return questionAndAnswerMapper.toPagedDetailResponse(questionAndAnswerDetails);
	}

	@Override
	public CustomPageResponse<QuestionAndAnswerDetailResponse> searchUnansweredQuestions(String text, String id,
			int pageNo, int pageSize) {
		Pageable pageable = PageRequest.of(pageNo, pageSize);

		List<String> fields = List.of("name", "question");
		Page<QuestionAndAnswerDetail> questionAndAnswerDetails = databaseSearchService
				.findQuestionsByAnswerStatusByDynamicFilter(id, false, text, fields, pageable);
		if (questionAndAnswerDetails.getTotalElements() == 0)
			return new CustomPageResponse<>();

		return questionAndAnswerMapper.toPagedDetailResponse(questionAndAnswerDetails);
	}

	@Override
	public CustomPageResponse<QuestionAndAnswerDetailResponse> retrieveAllQuestions(String id, int pageNo,
			int pageSize) {
		Pageable pageable = PageRequest.of(pageNo, pageSize);

		Page<QuestionAndAnswerDetail> questionAndAnswerDetails = questionAndAnswerDetailRepository
				.findByQuestionAnswerIdOrderByCreatedOnDesc(id, pageable);

		return questionAndAnswerMapper.toPagedDetailResponse(questionAndAnswerDetails);
	}

	@Override
	public CustomPageResponse<QuestionAndAnswerDetailResponse> searchAllQuestions(String text, String id, int pageNo,
			int pageSize) {
		Pageable pageable = PageRequest.of(pageNo, pageSize);

		List<String> fields = List.of("name", "question");
		Page<QuestionAndAnswerDetail> questionAndAnswerDetails = databaseSearchService.findQuestionsByDynamicFilter(id,
				text, fields, pageable);
		if (questionAndAnswerDetails.getTotalElements() == 0)
			return new CustomPageResponse<>();

		return questionAndAnswerMapper.toPagedDetailResponse(questionAndAnswerDetails);
	}

	@Override
	public OperationalResponse deletedAskedQuestion(String id) {
		QuestionAndAnswerDetail existingQuestionAndAnswerDetail = retrieveQuestionAndAnswerDetailById(id);
		if (existingQuestionAndAnswerDetail.getIsAnswered())
			throw new GeneralPlatformDomainRuleException("Question cannot be deleted any longer");

		try {
			questionAndAnswerDetailRepository.delete(existingQuestionAndAnswerDetail);

			return OperationalResponse.instance(GeneralConstants.SUCCESS_MESSAGE);
		} catch (DataIntegrityViolationException e) {
			throw new DataIntegrityViolationException(e.getLocalizedMessage());
		}
	}

	@Override
	public OperationalResponse deleteQuestionAndAnswer(String id, String accountId) {
		QuestionAndAnswer existingQuestionAndAnswer = retrieveQuestionAndAnswerById(id);
		if (existingQuestionAndAnswer.getIsPublished())
			throw new GeneralPlatformDomainRuleException("Question and answer session cannot be deleted any longer");
		if (questionAndAnswerDetailRepository.existsByQuestionAnswerId(id))
			throw new GeneralPlatformDomainRuleException("Question and answer session is already used");
		if (!accountId.equals(existingQuestionAndAnswer.getAccountId()))
			throw new GeneralPlatformServiceException(GeneralConstants.MODIFY_NOT_ALLOWED_MESSAGE);

		try {
			questionAndAnswerRepository.delete(existingQuestionAndAnswer);

			return OperationalResponse.instance(GeneralConstants.SUCCESS_MESSAGE);
		} catch (DataIntegrityViolationException e) {
			throw new DataIntegrityViolationException(e.getLocalizedMessage());
		}
	}

	@Override
	public OperationalResponse publishQuestionAndAnswer(String id, String accountId) {
		QuestionAndAnswer existingQuestionAndAnswer = retrieveQuestionAndAnswerById(id);
		if (!existingQuestionAndAnswer.getAccountId().equals(accountId))
			throw new GeneralPlatformServiceException(GeneralConstants.MODIFY_NOT_ALLOWED_MESSAGE);

		if (existingQuestionAndAnswer.getIsPublished())
			throw new GeneralPlatformDomainRuleException("Question and answer was already published");

		existingQuestionAndAnswer.setIsPublished(true);

		try {
			questionAndAnswerRepository.save(existingQuestionAndAnswer);

			return OperationalResponse.instance(GeneralConstants.SUCCESS_MESSAGE);
		} catch (DataIntegrityViolationException e) {
			throw new DataIntegrityViolationException(e.getLocalizedMessage());
		}
	}

	@Override
	public OperationalResponse unpublishQuestionAndAnswer(String id, String accountId) {
		QuestionAndAnswer existingQuestionAndAnswer = retrieveQuestionAndAnswerById(id);
		if (!existingQuestionAndAnswer.getAccountId().equals(accountId))
			throw new GeneralPlatformServiceException(GeneralConstants.MODIFY_NOT_ALLOWED_MESSAGE);

		if (!existingQuestionAndAnswer.getIsPublished())
			throw new GeneralPlatformDomainRuleException("Question and answer was already unpublished");

		existingQuestionAndAnswer.setIsPublished(false);

		try {
			questionAndAnswerRepository.save(existingQuestionAndAnswer);

			return OperationalResponse.instance(GeneralConstants.SUCCESS_MESSAGE);
		} catch (DataIntegrityViolationException e) {
			throw new DataIntegrityViolationException(e.getLocalizedMessage());
		}
	}

	private QuestionAndAnswer retrieveQuestionAndAnswerById(String id) {
		return questionAndAnswerRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Question And Answer Session Not Found"));
	}

	private QuestionAndAnswerDetail retrieveQuestionAndAnswerDetailById(String id) {
		return questionAndAnswerDetailRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Question And Answer Detail Not Found"));
	}

	private QuestionAndAnswer retrieveQuestionAndAnswerFromPublicId(String publicId) {
		String decryptedLink = linkGenerator.extractDetailsFromPublicLink(publicId,
				propertyConstants.getEventsEncryptionSecretKey());
		String[] words = decryptedLink.split(":");
		if (words.length < 1)
			throw new GeneralPlatformDomainRuleException("Question and answer link has been corrupted");

		return retrieveQuestionAndAnswerById(words[0]);
	}
}
