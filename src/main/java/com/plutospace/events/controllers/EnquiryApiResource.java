/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.controllers;

import java.net.URI;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import com.plutospace.events.commons.data.CustomPageResponse;
import com.plutospace.events.commons.definitions.GeneralConstants;
import com.plutospace.events.commons.definitions.PropertyConstants;
import com.plutospace.events.commons.utils.SecurityMapper;
import com.plutospace.events.domain.data.request.CreateEnquiryRequest;
import com.plutospace.events.domain.data.response.EnquiryResponse;
import com.plutospace.events.domain.data.response.OperationalResponse;
import com.plutospace.events.services.EnquiryService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

import static com.plutospace.events.commons.definitions.ApiResourceConstants.*;

@RestController
@RequestMapping(ENQUIRIES)
@Tag(name = "Enquiry (Contact Us) Endpoints", description = "These endpoints manages enquiries (contact us) on PlutoSpace Events")
@RequiredArgsConstructor
public class EnquiryApiResource {

	private final EnquiryService enquiryService;
	private final SecurityMapper securityMapper;
	private final PropertyConstants propertyConstants;
	private final HttpServletRequest request;

	@PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	@Operation(description = "This endpoint creates a new enquiry on PlutoSpace Events")
	public ResponseEntity<EnquiryResponse> createNewEnquiry(@RequestBody CreateEnquiryRequest createEnquiryRequest,
			UriComponentsBuilder uriComponentsBuilder) {
		EnquiryResponse enquiryResponse = enquiryService.createNewEnquiry(createEnquiryRequest);

		String location = uriComponentsBuilder.path(ENQUIRIES_RESOURCE_ID).buildAndExpand(enquiryResponse.getId())
				.toUriString();
		URI uri = URI.create(location);
		return ResponseEntity.created(uri).body(enquiryResponse);
	}

	@GetMapping(path = "/pending", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(description = "This endpoint retrieves pending enquiries")
	public ResponseEntity<CustomPageResponse<EnquiryResponse>> retrievePendingEnquiries(
			@RequestParam(name = "pageNo") int pageNo, @RequestParam(name = "pageSize") int pageSize) {
		return ResponseEntity.ok(enquiryService.retrievePendingEnquiries(pageNo, pageSize));
	}

	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(description = "This endpoint retrieves all enquiries")
	public ResponseEntity<CustomPageResponse<EnquiryResponse>> retrieveEnquiries(
			@RequestParam(name = "pageNo") int pageNo, @RequestParam(name = "pageSize") int pageSize) {
		return ResponseEntity.ok(enquiryService.retrieveEnquiries(pageNo, pageSize));
	}

	@GetMapping(path = RESOURCE_ID + "/treated", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(description = "This endpoint marks an enquiry as treated")
	public ResponseEntity<OperationalResponse> markAsTreated(@PathVariable String id) {
		String userId = securityMapper.retrieveAdminUserId(request.getHeader(GeneralConstants.TOKEN_KEY),
				propertyConstants.getEventsLoginEncryptionSecretKey());
		return ResponseEntity.ok(enquiryService.markAsTreated(id, userId));
	}

	@GetMapping(path = RESOURCE_ID + "/not-treated", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(description = "This endpoint marks an enquiry as not treated")
	public ResponseEntity<OperationalResponse> markAsNotTreated(@PathVariable String id) {
		String userId = securityMapper.retrieveAdminUserId(request.getHeader(GeneralConstants.TOKEN_KEY),
				propertyConstants.getEventsLoginEncryptionSecretKey());
		return ResponseEntity.ok(enquiryService.markAsNotTreated(id, userId));
	}

	@GetMapping(path = "/search", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(description = "This endpoint searches through enquiries")
	public ResponseEntity<CustomPageResponse<EnquiryResponse>> searchEnquiry(@RequestParam(name = "text") String text,
			@RequestParam(name = "pageNo") int pageNo, @RequestParam(name = "pageSize") int pageSize) {
		return ResponseEntity.ok(enquiryService.searchEnquiry(text, pageNo, pageSize));
	}
}
