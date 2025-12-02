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
import com.plutospace.events.domain.data.request.SavePollRequest;
import com.plutospace.events.domain.data.response.*;
import com.plutospace.events.services.PollService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

import static com.plutospace.events.commons.definitions.ApiResourceConstants.*;

@RestController
@RequestMapping(POLLS)
@Tag(name = "Poll Endpoints", description = "These endpoints manages polls on PlutoSpace Events")
@RequiredArgsConstructor
public class PollApiResource {

	private final PollService pollService;
	private final SecurityMapper securityMapper;
	private final PropertyConstants propertyConstants;
	private final HttpServletRequest request;

	@PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	@Operation(description = "This endpoint creates a new poll on PlutoSpace Events")
	public ResponseEntity<PollResponse> createPoll(@RequestBody SavePollRequest savePollRequest,
			UriComponentsBuilder uriComponentsBuilder) {
		String accountId = securityMapper.retrieveAccountId(request.getHeader(GeneralConstants.TOKEN_KEY),
				propertyConstants.getEventsLoginEncryptionSecretKey());
		PollResponse pollResponse = pollService.createPoll(savePollRequest, accountId);

		String location = uriComponentsBuilder.path(POLLS_RESOURCE_ID).buildAndExpand(pollResponse.getId())
				.toUriString();
		URI uri = URI.create(location);
		return ResponseEntity.created(uri).body(pollResponse);
	}

	@PutMapping(path = RESOURCE_ID, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	@Operation(description = "This endpoint updates a poll")
	public ResponseEntity<PollResponse> updatePoll(@PathVariable String id,
			@RequestBody SavePollRequest savePollRequest) {
		return ResponseEntity.ok(pollService.updatePoll(id, savePollRequest));
	}

	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(description = "This endpoint retrieves all polls")
	public ResponseEntity<CustomPageResponse<PollResponse>> retrievePolls(@RequestParam(name = "pageNo") int pageNo,
			@RequestParam(name = "pageSize") int pageSize) {
		String accountId = securityMapper.retrieveAccountId(request.getHeader(GeneralConstants.TOKEN_KEY),
				propertyConstants.getEventsLoginEncryptionSecretKey());
		return ResponseEntity.ok(pollService.retrievePolls(accountId, pageNo, pageSize));
	}

	@PostMapping(path = "/bulk-ids", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(description = "This endpoint retrieves bulk polls by ids")
	public ResponseEntity<List<PollResponse>> retrievePoll(@RequestBody List<String> ids) {
		return ResponseEntity.ok(pollService.retrievePoll(ids));
	}

	@GetMapping(path = "/details", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(description = "This endpoint retrieves poll by the public id")
	public ResponseEntity<PollResponse> retrievePublishedPollByPublicId(@RequestParam(name = "pid") String pid) {
		return ResponseEntity.ok(pollService.retrievePublishedPollByPublicId(pid));
	}

	@GetMapping(path = RESOURCE_ID + "/publish", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(description = "This endpoint publishes a poll")
	public ResponseEntity<OperationalResponse> publishPoll(@PathVariable String id) {
		return ResponseEntity.ok(pollService.publishPoll(id));
	}

	@GetMapping(path = RESOURCE_ID + "/unpublish", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(description = "This endpoint unpublishes a poll")
	public ResponseEntity<OperationalResponse> unpublishPoll(@PathVariable String id) {
		return ResponseEntity.ok(pollService.unpublishPoll(id));
	}
}
