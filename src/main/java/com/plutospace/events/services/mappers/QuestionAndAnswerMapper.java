/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.services.mappers;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import com.plutospace.events.commons.data.CustomPageResponse;
import com.plutospace.events.domain.data.request.AskQuestionRequest;
import com.plutospace.events.domain.data.request.CreateQuestionAndAnswerRequest;
import com.plutospace.events.domain.data.response.QuestionAndAnswerDetailResponse;
import com.plutospace.events.domain.data.response.QuestionAndAnswerResponse;
import com.plutospace.events.domain.entities.QuestionAndAnswer;
import com.plutospace.events.domain.entities.QuestionAndAnswerDetail;

@Component
public class QuestionAndAnswerMapper {

	public QuestionAndAnswerResponse toResponse(QuestionAndAnswer questionAndAnswer) {
		return QuestionAndAnswerResponse.instance(questionAndAnswer.getId(), questionAndAnswer.getAccountId(),
				questionAndAnswer.getTitle(), questionAndAnswer.getPublicId(), questionAndAnswer.getClosedEnded(),
				questionAndAnswer.getIsPublished(), questionAndAnswer.getCreatedOn());
	}

	public QuestionAndAnswerDetailResponse toResponse(QuestionAndAnswerDetail questionAndAnswerDetail) {
		return QuestionAndAnswerDetailResponse.instance(questionAndAnswerDetail.getId(),
				questionAndAnswerDetail.getQuestionAnswerId(), questionAndAnswerDetail.getName(),
				questionAndAnswerDetail.getQuestion(), questionAndAnswerDetail.getAnswer(),
				questionAndAnswerDetail.getIsAnswered(), questionAndAnswerDetail.getCreatedOn());
	}

	public QuestionAndAnswer toEntity(CreateQuestionAndAnswerRequest createQuestionAndAnswerRequest, String accountId) {
		return QuestionAndAnswer.instance(accountId, createQuestionAndAnswerRequest.title(), null,
				StringUtils.isNotBlank(createQuestionAndAnswerRequest.eventId()), false);
	}

	public QuestionAndAnswerDetail toEntity(AskQuestionRequest askQuestionRequest, String id) {
		return QuestionAndAnswerDetail.instance(id, askQuestionRequest.name(), askQuestionRequest.question(), null,
				false);
	}

	public CustomPageResponse<QuestionAndAnswerResponse> toPagedResponse(Page<QuestionAndAnswer> questionAndAnswers) {
		List<QuestionAndAnswerResponse> questionAndAnswerResponses = questionAndAnswers.getContent().stream()
				.map(this::toResponse).toList();
		long totalElements = questionAndAnswers.getTotalElements();
		Pageable pageable = questionAndAnswers.getPageable();
		return CustomPageResponse.resolvePageResponse(questionAndAnswerResponses, totalElements, pageable);
	}

	public CustomPageResponse<QuestionAndAnswerDetailResponse> toPagedDetailResponse(
			Page<QuestionAndAnswerDetail> questionAndAnswerDetails) {
		List<QuestionAndAnswerDetailResponse> questionAndAnswerDetailResponses = questionAndAnswerDetails.getContent()
				.stream().map(this::toResponse).toList();
		long totalElements = questionAndAnswerDetails.getTotalElements();
		Pageable pageable = questionAndAnswerDetails.getPageable();
		return CustomPageResponse.resolvePageResponse(questionAndAnswerDetailResponses, totalElements, pageable);
	}
}
