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
import com.plutospace.events.domain.data.request.CreateMeetingRequest;
import com.plutospace.events.domain.data.response.MeetingResponse;
import com.plutospace.events.services.MeetingService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

import static com.plutospace.events.commons.definitions.ApiResourceConstants.*;

@RestController
@RequestMapping(MEETINGS)
@Tag(name = "Meeting Endpoints", description = "These endpoints manages meetings on PlutoSpace Events")
@RequiredArgsConstructor
public class MeetingApiResource {

	private final MeetingService meetingService;
	private final SecurityMapper securityMapper;
	private final PropertyConstants propertyConstants;
	private final HttpServletRequest request;

	@PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	@Operation(description = "This endpoint creates a new meeting on PlutoSpace Events")
	public ResponseEntity<MeetingResponse> createMeeting(@RequestBody CreateMeetingRequest createMeetingRequest,
			UriComponentsBuilder uriComponentsBuilder) {
		String accountId = securityMapper.retrieveAccountId(request.getHeader(GeneralConstants.TOKEN_KEY),
				propertyConstants.getEventsLoginEncryptionSecretKey());
		MeetingResponse meetingResponse = meetingService.createMeeting(createMeetingRequest, accountId);

		String location = uriComponentsBuilder.path(MEETINGS_RESOURCE_ID).buildAndExpand(meetingResponse.getId())
				.toUriString();
		URI uri = URI.create(location);
		return ResponseEntity.created(uri).body(meetingResponse);
	}

	@PostMapping(path = "/bulk-ids", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(description = "This endpoint retrieves bulk meetings by ids")
	public ResponseEntity<List<MeetingResponse>> retrieveMeeting(@RequestBody List<String> ids) {
		return ResponseEntity.ok(meetingService.retrieveMeeting(ids));
	}

	@GetMapping(path = "/details", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(description = "This endpoint retrieves meeting by the public id")
	public ResponseEntity<MeetingResponse> retrieveMeetingByPublicId(@RequestParam(name = "pid") String pid) {
		return ResponseEntity.ok(meetingService.retrieveMeetingByPublicId(pid));
	}

	@GetMapping(path = "/search", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(description = "This endpoint searches through meetings")
	public ResponseEntity<CustomPageResponse<MeetingResponse>> searchMeeting(@RequestParam(name = "text") String text,
			@RequestParam(name = "pageNo") int pageNo, @RequestParam(name = "pageSize") int pageSize) {
		String accountId = securityMapper.retrieveAccountId(request.getHeader(GeneralConstants.TOKEN_KEY),
				propertyConstants.getEventsLoginEncryptionSecretKey());
		return ResponseEntity.ok(meetingService.searchMeeting(accountId, text, pageNo, pageSize));
	}
}
