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
import com.plutospace.events.services.QuestionAndAnswerService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

import static com.plutospace.events.commons.definitions.ApiResourceConstants.*;

@RestController
@RequestMapping(QUESTIONS_AND_ANSWERS)
@Tag(name = "Question And Answer Endpoints", description = "These endpoints manages questions and answers on PlutoSpace Events")
@RequiredArgsConstructor
public class QuestionAndAnswerApiResource {

	private final QuestionAndAnswerService questionAndAnswerService;
	private final SecurityMapper securityMapper;
	private final PropertyConstants propertyConstants;
	private final HttpServletRequest request;

	@PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	@Operation(description = "This endpoint creates a new question and answer session on PlutoSpace Events")
	public ResponseEntity<QuestionAndAnswerResponse> createQuestionAndAnswer(
			@RequestBody CreateQuestionAndAnswerRequest createQuestionAndAnswerRequest,
			UriComponentsBuilder uriComponentsBuilder) {
		String accountId = securityMapper.retrieveAccountId(request.getHeader(GeneralConstants.TOKEN_KEY),
				propertyConstants.getEventsLoginEncryptionSecretKey());
		QuestionAndAnswerResponse questionAndAnswerResponse = questionAndAnswerService
				.createQuestionAndAnswer(createQuestionAndAnswerRequest, accountId);

		String location = uriComponentsBuilder.path(QUESTIONS_AND_ANSWERS_RESOURCE_ID)
				.buildAndExpand(questionAndAnswerResponse.getId()).toUriString();
		URI uri = URI.create(location);
		return ResponseEntity.created(uri).body(questionAndAnswerResponse);
	}

	@PutMapping(path = "/ask", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	@Operation(description = "This endpoint asks a question")
	public ResponseEntity<OperationalResponse> askQuestion(@RequestParam(name = "pid") String pid,
			@RequestBody AskQuestionRequest askQuestionRequest) {
		return ResponseEntity.ok(questionAndAnswerService.askQuestion(askQuestionRequest, pid));
	}

	@PutMapping(path = RESOURCE_ID, produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(description = "This endpoint updates a question and answer session")
	public ResponseEntity<QuestionAndAnswerResponse> updateQuestionAndAnswer(@PathVariable String id,
			@RequestParam(name = "title") String title) {
		String accountId = securityMapper.retrieveAccountId(request.getHeader(GeneralConstants.TOKEN_KEY),
				propertyConstants.getEventsLoginEncryptionSecretKey());
		return ResponseEntity.ok(questionAndAnswerService.updateQuestionAndAnswer(id, title, accountId));
	}

	@PutMapping(path = RESOURCE_ID + "/edit-ask", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(description = "This endpoint updates a question previously asked")
	public ResponseEntity<OperationalResponse> editAskedQuestion(@PathVariable String id,
			@RequestParam(name = "question") String question) {
		return ResponseEntity.ok(questionAndAnswerService.editAskedQuestion(id, question));
	}

	@PatchMapping(path = RESOURCE_ID
			+ "/answer", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	@Operation(description = "This endpoint answers a question")
	public ResponseEntity<OperationalResponse> answerAQuestion(@PathVariable String id,
			@RequestBody AnswerQuestionRequest answerQuestionRequest) {
		String accountId = securityMapper.retrieveAccountId(request.getHeader(GeneralConstants.TOKEN_KEY),
				propertyConstants.getEventsLoginEncryptionSecretKey());
		return ResponseEntity.ok(questionAndAnswerService.answerAQuestion(answerQuestionRequest, id, accountId));
	}

	@PostMapping(path = "/mark-as-answered", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(description = "This endpoint marks bulk questions as answered")
	public ResponseEntity<OperationalResponse> markAsAnswered(@RequestBody List<String> ids) {
		String accountId = securityMapper.retrieveAccountId(request.getHeader(GeneralConstants.TOKEN_KEY),
				propertyConstants.getEventsLoginEncryptionSecretKey());
		return ResponseEntity.ok(questionAndAnswerService.markAsAnswered(ids, accountId));
	}

	@GetMapping(path = "/details", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(description = "This endpoint retrieves question and answer session by the public id")
	public ResponseEntity<QuestionAndAnswerResponse> retrieveQuestionAndAnswerSessionByPublicId(
			@RequestParam(name = "pid") String pid) {
		return ResponseEntity.ok(questionAndAnswerService.retrieveQuestionAndAnswerSessionByPublicId(pid));
	}

	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(description = "This endpoint retrieves all question and answer sessions")
	public ResponseEntity<CustomPageResponse<QuestionAndAnswerResponse>> retrieveQuestionAndAnswerSessions(
			@RequestParam(name = "pageNo") int pageNo, @RequestParam(name = "pageSize") int pageSize) {
		String accountId = securityMapper.retrieveAccountId(request.getHeader(GeneralConstants.TOKEN_KEY),
				propertyConstants.getEventsLoginEncryptionSecretKey());
		return ResponseEntity
				.ok(questionAndAnswerService.retrieveQuestionAndAnswerSessions(accountId, pageNo, pageSize));
	}

	@GetMapping(path = "/search", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(description = "This endpoint searches through question and answer sessions")
	public ResponseEntity<CustomPageResponse<QuestionAndAnswerResponse>> searchQuestionAndAnswerSessions(
			@RequestParam(name = "text") String text, @RequestParam(name = "pageNo") int pageNo,
			@RequestParam(name = "pageSize") int pageSize) {
		String accountId = securityMapper.retrieveAccountId(request.getHeader(GeneralConstants.TOKEN_KEY),
				propertyConstants.getEventsLoginEncryptionSecretKey());
		return ResponseEntity
				.ok(questionAndAnswerService.searchQuestionAndAnswerSessions(text, accountId, pageNo, pageSize));
	}

	@GetMapping(path = RESOURCE_ID + "/unanswered-questions", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(description = "This endpoint retrieves all unanswered questions")
	public ResponseEntity<CustomPageResponse<QuestionAndAnswerDetailResponse>> retrieveUnansweredQuestions(
			@PathVariable String id, @RequestParam(name = "pageNo") int pageNo,
			@RequestParam(name = "pageSize") int pageSize) {
		return ResponseEntity.ok(questionAndAnswerService.retrieveUnansweredQuestions(id, pageNo, pageSize));
	}

	@GetMapping(path = RESOURCE_ID + "/search-unanswered-questions", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(description = "This endpoint searches through unanswered questions")
	public ResponseEntity<CustomPageResponse<QuestionAndAnswerDetailResponse>> searchUnansweredQuestions(
			@PathVariable String id, @RequestParam(name = "text") String text,
			@RequestParam(name = "pageNo") int pageNo, @RequestParam(name = "pageSize") int pageSize) {
		return ResponseEntity.ok(questionAndAnswerService.searchUnansweredQuestions(text, id, pageNo, pageSize));
	}

	@GetMapping(path = RESOURCE_ID + "/all-questions", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(description = "This endpoint retrieves all questions")
	public ResponseEntity<CustomPageResponse<QuestionAndAnswerDetailResponse>> retrieveAllQuestions(
			@PathVariable String id, @RequestParam(name = "pageNo") int pageNo,
			@RequestParam(name = "pageSize") int pageSize) {
		return ResponseEntity.ok(questionAndAnswerService.retrieveAllQuestions(id, pageNo, pageSize));
	}

	@GetMapping(path = RESOURCE_ID + "/search-all-questions", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(description = "This endpoint searches through all questions")
	public ResponseEntity<CustomPageResponse<QuestionAndAnswerDetailResponse>> searchAllQuestions(
			@PathVariable String id, @RequestParam(name = "text") String text,
			@RequestParam(name = "pageNo") int pageNo, @RequestParam(name = "pageSize") int pageSize) {
		return ResponseEntity.ok(questionAndAnswerService.searchAllQuestions(text, id, pageNo, pageSize));
	}

	@DeleteMapping(path = RESOURCE_ID + "/ask", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(description = "This endpoint deletes asked question")
	public ResponseEntity<OperationalResponse> deletedAskedQuestion(@PathVariable String id) {
		return ResponseEntity.ok(questionAndAnswerService.deletedAskedQuestion(id));
	}

	@DeleteMapping(path = RESOURCE_ID, produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(description = "This endpoint deletes a question and answer session")
	public ResponseEntity<OperationalResponse> deleteQuestionAndAnswer(@PathVariable String id) {
		String accountId = securityMapper.retrieveAccountId(request.getHeader(GeneralConstants.TOKEN_KEY),
				propertyConstants.getEventsLoginEncryptionSecretKey());
		return ResponseEntity.ok(questionAndAnswerService.deleteQuestionAndAnswer(id, accountId));
	}

	@GetMapping(path = RESOURCE_ID + "/publish", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(description = "This endpoint publishes a question and answer session")
	public ResponseEntity<OperationalResponse> publishQuestionAndAnswer(@PathVariable String id) {
		String accountId = securityMapper.retrieveAccountId(request.getHeader(GeneralConstants.TOKEN_KEY),
				propertyConstants.getEventsLoginEncryptionSecretKey());
		return ResponseEntity.ok(questionAndAnswerService.publishQuestionAndAnswer(id, accountId));
	}

	@GetMapping(path = RESOURCE_ID + "/unpublish", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(description = "This endpoint unpublishes a question and answer session")
	public ResponseEntity<OperationalResponse> unpublishQuestionAndAnswer(@PathVariable String id) {
		String accountId = securityMapper.retrieveAccountId(request.getHeader(GeneralConstants.TOKEN_KEY),
				propertyConstants.getEventsLoginEncryptionSecretKey());
		return ResponseEntity.ok(questionAndAnswerService.unpublishQuestionAndAnswer(id, accountId));
	}
}
