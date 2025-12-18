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
import com.plutospace.events.domain.data.request.BookFreeSlotRequest;
import com.plutospace.events.domain.data.request.SaveFreeSlotRequest;
import com.plutospace.events.domain.data.response.FreeSlotResponse;
import com.plutospace.events.domain.data.response.OperationalResponse;
import com.plutospace.events.services.FreeSlotService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

import static com.plutospace.events.commons.definitions.ApiResourceConstants.*;

@RestController
@RequestMapping(FREE_SLOTS)
@Tag(name = "Free Slots (Appointment Booking) Endpoints", description = "These endpoints manages free slots (appointment booking) on PlutoSpace Events")
@RequiredArgsConstructor
public class FreeSlotApiResource {

	private final FreeSlotService freeSlotService;
	private final SecurityMapper securityMapper;
	private final PropertyConstants propertyConstants;
	private final HttpServletRequest request;

	@PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	@Operation(description = "This endpoint creates a new free slot on PlutoSpace Events")
	public ResponseEntity<FreeSlotResponse> createFreeSlot(@RequestBody SaveFreeSlotRequest saveFreeSlotRequest,
			UriComponentsBuilder uriComponentsBuilder) {
		String accountId = securityMapper.retrieveAccountId(request.getHeader(GeneralConstants.TOKEN_KEY),
				propertyConstants.getEventsLoginEncryptionSecretKey());
		FreeSlotResponse freeSlotResponse = freeSlotService.createFreeSlot(saveFreeSlotRequest, accountId);

		String location = uriComponentsBuilder.path(FREE_SLOTS_RESOURCE_ID).buildAndExpand(freeSlotResponse.getId())
				.toUriString();
		URI uri = URI.create(location);
		return ResponseEntity.created(uri).body(freeSlotResponse);
	}

	@PutMapping(path = RESOURCE_ID, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	@Operation(description = "This endpoint updates a free slot on PlutoSpace Events")
	public ResponseEntity<FreeSlotResponse> updateFreeSlot(@PathVariable String id,
			@RequestBody SaveFreeSlotRequest saveFreeSlotRequest) {
		String accountId = securityMapper.retrieveAccountId(request.getHeader(GeneralConstants.TOKEN_KEY),
				propertyConstants.getEventsLoginEncryptionSecretKey());
		return ResponseEntity.ok(freeSlotService.updateFreeSlot(id, accountId, saveFreeSlotRequest));
	}

	@GetMapping(path = "/public-link", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(description = "This endpoint generates a public link for free slots booking")
	public ResponseEntity<String> generateAvailableSlotLink() {
		String accountId = securityMapper.retrieveAccountId(request.getHeader(GeneralConstants.TOKEN_KEY),
				propertyConstants.getEventsLoginEncryptionSecretKey());
		String accountUserId = securityMapper.retrieveAccountUserId(request.getHeader(GeneralConstants.TOKEN_KEY),
				propertyConstants.getEventsLoginEncryptionSecretKey());
		return ResponseEntity.ok(freeSlotService.generateAvailableSlotLink(accountId, accountUserId));
	}

	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(description = "This endpoint retrieves all my free slots")
	public ResponseEntity<CustomPageResponse<FreeSlotResponse>> retrieveMyFreeSlots(
			@RequestParam(name = "pageNo") int pageNo, @RequestParam(name = "pageSize") int pageSize) {
		String accountId = securityMapper.retrieveAccountId(request.getHeader(GeneralConstants.TOKEN_KEY),
				propertyConstants.getEventsLoginEncryptionSecretKey());
		String accountUserId = securityMapper.retrieveAccountUserId(request.getHeader(GeneralConstants.TOKEN_KEY),
				propertyConstants.getEventsLoginEncryptionSecretKey());
		return ResponseEntity.ok(freeSlotService.retrieveMyFreeSlots(accountId, accountUserId, pageNo, pageSize));
	}

	@GetMapping(path = "/available-slots", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(description = "This endpoint retrieves available slots")
	public ResponseEntity<List<FreeSlotResponse>> retrieveMyAvailableSlots(@RequestParam(name = "pid") String pid,
			@RequestParam(name = "startTime") Long startTime, @RequestParam(name = "endTime") Long endTime) {
		return ResponseEntity.ok(freeSlotService.retrieveMyAvailableSlots(pid, startTime, endTime));
	}

	@DeleteMapping(path = RESOURCE_ID, produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(description = "This endpoint deletes a free slot")
	public ResponseEntity<OperationalResponse> deleteFreeSlot(@PathVariable String id) {
		String accountId = securityMapper.retrieveAccountId(request.getHeader(GeneralConstants.TOKEN_KEY),
				propertyConstants.getEventsLoginEncryptionSecretKey());
		return ResponseEntity.ok(freeSlotService.deleteFreeSlot(id, accountId));
	}

	@PostMapping(path = "/book", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	@Operation(description = "This endpoint books a free slot on PlutoSpace Events")
	public ResponseEntity<OperationalResponse> updateFreeSlot(@RequestBody BookFreeSlotRequest bookFreeSlotRequest) {
		return ResponseEntity.ok(freeSlotService.bookFreeSlot(bookFreeSlotRequest));
	}

	@GetMapping(path = "/search", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(description = "This endpoint searches through free slots")
	public ResponseEntity<CustomPageResponse<FreeSlotResponse>> searchFreeSlot(@RequestParam(name = "text") String text,
			@RequestParam(name = "pageNo") int pageNo, @RequestParam(name = "pageSize") int pageSize) {
		String accountId = securityMapper.retrieveAccountId(request.getHeader(GeneralConstants.TOKEN_KEY),
				propertyConstants.getEventsLoginEncryptionSecretKey());
		String accountUserId = securityMapper.retrieveAccountUserId(request.getHeader(GeneralConstants.TOKEN_KEY),
				propertyConstants.getEventsLoginEncryptionSecretKey());
		return ResponseEntity.ok(freeSlotService.searchFreeSlot(accountId, accountUserId, text, pageNo, pageSize));
	}
}
