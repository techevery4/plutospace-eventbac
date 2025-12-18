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
import com.plutospace.events.domain.data.response.MeetingResponse;
import com.plutospace.events.domain.data.response.OperationalResponse;
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

	@GetMapping(path = "/between", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(description = "This endpoint retrieves meetings between specified dates")
	public ResponseEntity<CustomPageResponse<MeetingResponse>> retrieveMeetingsBetween(
			@RequestParam(name = "startTime") Long startTime, @RequestParam(name = "endTime") Long endTime,
			@RequestParam(name = "pageNo") int pageNo, @RequestParam(name = "pageSize") int pageSize) {
		String accountId = securityMapper.retrieveAccountId(request.getHeader(GeneralConstants.TOKEN_KEY),
				propertyConstants.getEventsLoginEncryptionSecretKey());
		return ResponseEntity
				.ok(meetingService.retrieveMeetingsBetween(accountId, startTime, endTime, pageNo, pageSize));
	}

	@GetMapping(path = "/upcoming", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(description = "This endpoint retrieves upcoming meetings between specified dates")
	public ResponseEntity<List<MeetingResponse>> retrieveUpcomingMeetingsBetween(
			@RequestParam(name = "startTime") Long startTime, @RequestParam(name = "endTime") Long endTime) {
		String accountId = securityMapper.retrieveAccountId(request.getHeader(GeneralConstants.TOKEN_KEY),
				propertyConstants.getEventsLoginEncryptionSecretKey());
		String accountUserId = securityMapper.retrieveAccountUserId(request.getHeader(GeneralConstants.TOKEN_KEY),
				propertyConstants.getEventsLoginEncryptionSecretKey());
		return ResponseEntity
				.ok(meetingService.retrieveUpcomingMeetingsBetween(accountId, accountUserId, startTime, endTime));
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
		String accountUserId = securityMapper.retrieveAccountUserId(request.getHeader(GeneralConstants.TOKEN_KEY),
				propertyConstants.getEventsLoginEncryptionSecretKey());
		return ResponseEntity.ok(meetingService.searchMeeting(accountId, accountUserId, text, pageNo, pageSize));
	}

	@GetMapping(path = "/record", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(description = "This endpoint records a meeting")
	public ResponseEntity<OperationalResponse> startRecordingMeeting(@RequestParam(name = "pid") String pid) {
		String accountUserId = securityMapper.retrieveAccountUserId(request.getHeader(GeneralConstants.TOKEN_KEY),
				propertyConstants.getEventsLoginEncryptionSecretKey());
		return ResponseEntity.ok(meetingService.startRecordingMeeting(accountUserId, pid));
	}

	@PutMapping(path = RESOURCE_ID, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	@Operation(description = "This endpoint updates a meeting on PlutoSpace Events")
	public ResponseEntity<MeetingResponse> updateMeeting(@PathVariable String id,
			@RequestBody UpdateMeetingRequest updateMeetingRequest) {
		String accountId = securityMapper.retrieveAccountId(request.getHeader(GeneralConstants.TOKEN_KEY),
				propertyConstants.getEventsLoginEncryptionSecretKey());
		return ResponseEntity.ok(meetingService.updateMeeting(id, accountId, updateMeetingRequest));
	}

	@PutMapping(path = RESOURCE_ID
			+ "/basic-setting", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	@Operation(description = "This endpoint updates a meeting basic setting on PlutoSpace Events")
	public ResponseEntity<MeetingResponse> updateMeetingBasicSetting(@PathVariable String id,
			@RequestBody UpdateMeetingBasicSettingsRequest updateMeetingBasicSettingsRequest) {
		String accountId = securityMapper.retrieveAccountId(request.getHeader(GeneralConstants.TOKEN_KEY),
				propertyConstants.getEventsLoginEncryptionSecretKey());
		return ResponseEntity
				.ok(meetingService.updateMeetingBasicSetting(id, accountId, updateMeetingBasicSettingsRequest));
	}

	@PutMapping(path = RESOURCE_ID
			+ "/time", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	@Operation(description = "This endpoint updates a meeting time on PlutoSpace Events")
	public ResponseEntity<MeetingResponse> updateMeetingTime(@PathVariable String id,
			@RequestBody UpdateMeetingTimeRequest updateMeetingTimeRequest) {
		String accountId = securityMapper.retrieveAccountId(request.getHeader(GeneralConstants.TOKEN_KEY),
				propertyConstants.getEventsLoginEncryptionSecretKey());
		return ResponseEntity.ok(meetingService.updateMeetingTime(id, accountId, updateMeetingTimeRequest));
	}

	@PutMapping(path = RESOURCE_ID
			+ "/recurring-time", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	@Operation(description = "This endpoint updates a recurring meeting time on PlutoSpace Events")
	public ResponseEntity<MeetingResponse> updateRecurringMeetingTime(@PathVariable String id,
			@RequestBody UpdateRecurringMeetingTimeRequest updateRecurringMeetingTimeRequest) {
		String accountId = securityMapper.retrieveAccountId(request.getHeader(GeneralConstants.TOKEN_KEY),
				propertyConstants.getEventsLoginEncryptionSecretKey());
		return ResponseEntity
				.ok(meetingService.updateRecurringMeetingTime(id, accountId, updateRecurringMeetingTimeRequest));
	}
}
