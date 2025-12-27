/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.services;

import java.util.List;

import com.plutospace.events.commons.data.CustomPageResponse;
import com.plutospace.events.domain.data.request.AnswerQuestionRequest;
import com.plutospace.events.domain.data.request.AskQuestionRequest;
import com.plutospace.events.domain.data.request.CreateQuestionAndAnswerRequest;
import com.plutospace.events.domain.data.response.OperationalResponse;
import com.plutospace.events.domain.data.response.QuestionAndAnswerDetailResponse;
import com.plutospace.events.domain.data.response.QuestionAndAnswerResponse;

public interface QuestionAndAnswerService {

	QuestionAndAnswerResponse createQuestionAndAnswer(CreateQuestionAndAnswerRequest createQuestionAndAnswerRequest,
			String accountId);

	OperationalResponse askQuestion(AskQuestionRequest askQuestionRequest, String publicId);

	QuestionAndAnswerResponse updateQuestionAndAnswer(String id, String title, String accountId);

	OperationalResponse editAskedQuestion(String id, String question);

	OperationalResponse answerAQuestion(AnswerQuestionRequest answerQuestionRequest, String id, String accountId);

	OperationalResponse markAsAnswered(List<String> ids, String accountId);

	QuestionAndAnswerResponse retrieveQuestionAndAnswerSessionByPublicId(String publicId);

	CustomPageResponse<QuestionAndAnswerResponse> retrieveQuestionAndAnswerSessions(String accountId, int pageNo,
			int pageSize);

	CustomPageResponse<QuestionAndAnswerResponse> searchQuestionAndAnswerSessions(String text, String accountId,
			int pageNo, int pageSize);

	CustomPageResponse<QuestionAndAnswerDetailResponse> retrieveUnansweredQuestions(String id, int pageNo,
			int pageSize);

	CustomPageResponse<QuestionAndAnswerDetailResponse> searchUnansweredQuestions(String text, String id, int pageNo,
			int pageSize);

	CustomPageResponse<QuestionAndAnswerDetailResponse> retrieveAllQuestions(String id, int pageNo, int pageSize);

	CustomPageResponse<QuestionAndAnswerDetailResponse> searchAllQuestions(String text, String id, int pageNo,
			int pageSize);

	OperationalResponse deletedAskedQuestion(String id);

	OperationalResponse deleteQuestionAndAnswer(String id, String accountId);

	OperationalResponse publishQuestionAndAnswer(String id, String accountId);

	OperationalResponse unpublishQuestionAndAnswer(String id, String accountId);
}
