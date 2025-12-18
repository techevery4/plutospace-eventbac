/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.controllers;

import java.net.URI;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import com.plutospace.events.commons.data.CustomPageResponse;
import com.plutospace.events.domain.data.request.CreatePromoCodeRequest;
import com.plutospace.events.domain.data.request.RegisterWithPromoCodeRequest;
import com.plutospace.events.domain.data.request.RenewPromoCodeRequest;
import com.plutospace.events.domain.data.response.*;
import com.plutospace.events.services.PromoCodeService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import static com.plutospace.events.commons.definitions.ApiResourceConstants.*;

@RestController
@RequestMapping(PROMO_CODES)
@Tag(name = "Promo Code Endpoints", description = "These endpoints manages promo codes on PlutoSpace Events")
@RequiredArgsConstructor
public class PromoCodeApiResource {

	private final PromoCodeService promoCodeService;

	@PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	@Operation(description = "This endpoint creates a new promo code on PlutoSpace Events")
	public ResponseEntity<PromoCodeResponse> createPromoCode(@RequestBody CreatePromoCodeRequest createPromoCodeRequest,
			UriComponentsBuilder uriComponentsBuilder) {
		PromoCodeResponse promoCodeResponse = promoCodeService.createPromoCode(createPromoCodeRequest);

		String location = uriComponentsBuilder.path(PROMO_CODES_RESOURCE_ID).buildAndExpand(promoCodeResponse.getId())
				.toUriString();
		URI uri = URI.create(location);
		return ResponseEntity.created(uri).body(promoCodeResponse);
	}

	@PutMapping(path = RESOURCE_ID, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	@Operation(description = "This endpoint renews a promo code")
	public ResponseEntity<PromoCodeResponse> renewPromoCode(@PathVariable String id,
			@RequestBody RenewPromoCodeRequest renewPromoCodeRequest) {
		return ResponseEntity.ok(promoCodeService.renewPromoCode(id, renewPromoCodeRequest));
	}

	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(description = "This endpoint retrieves all promo codes")
	public ResponseEntity<CustomPageResponse<PromoCodeResponse>> retrievePromoCodes(
			@RequestParam(name = "pageNo") int pageNo, @RequestParam(name = "pageSize") int pageSize) {
		return ResponseEntity.ok(promoCodeService.retrievePromoCodes(pageNo, pageSize));
	}

	@PostMapping(path = "/register", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(description = "This endpoint registers promo code usage during user registration")
	public ResponseEntity<OperationalResponse> registerWithPromoCode(
			@RequestBody RegisterWithPromoCodeRequest registerWithPromoCodeRequest) {
		return ResponseEntity.ok(promoCodeService.registerWithPromoCode(registerWithPromoCodeRequest));
	}

	@GetMapping(path = "/logs", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(description = "This endpoint retrieves registration logs for a promo code")
	public ResponseEntity<CustomPageResponse<PromoCodeRegistrationLogResponse>> retrieveRegistrationLogsOfPromoCode(
			@RequestParam(name = "code") String code, @RequestParam(name = "pageNo") int pageNo,
			@RequestParam(name = "pageSize") int pageSize) {
		return ResponseEntity.ok(promoCodeService.retrieveRegistrationLogsOfPromoCode(code, pageNo, pageSize));
	}

	@GetMapping(path = "/{registrationLogId}/mark-as-settled", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(description = "This endpoint marks a promo code registration as settled")
	public ResponseEntity<OperationalResponse> markAsSettled(@PathVariable String registrationLogId) {
		return ResponseEntity.ok(promoCodeService.markAsSettled(registrationLogId));
	}

	@GetMapping(path = "/{registrationLogId}/mark-as-not-settled", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(description = "This endpoint marks a promo code registration as not settled")
	public ResponseEntity<OperationalResponse> markAsNotSettled(@PathVariable String registrationLogId) {
		return ResponseEntity.ok(promoCodeService.markAsNotSettled(registrationLogId));
	}

	@GetMapping(path = "/search", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(description = "This endpoint searches through promo codes")
	public ResponseEntity<CustomPageResponse<PromoCodeResponse>> searchPromoCodes(
			@RequestParam(name = "text") String text, @RequestParam(name = "pageNo") int pageNo,
			@RequestParam(name = "pageSize") int pageSize) {
		return ResponseEntity.ok(promoCodeService.searchPromoCodes(text, pageNo, pageSize));
	}

	@GetMapping(path = "/logs/search", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(description = "This endpoint searches through promo code registration logs")
	public ResponseEntity<CustomPageResponse<PromoCodeRegistrationLogResponse>> searchPromoCodeRegistrationLogs(
			@RequestParam(name = "text") String text, @RequestParam(name = "code") String code,
			@RequestParam(name = "pageNo") int pageNo, @RequestParam(name = "pageSize") int pageSize) {
		return ResponseEntity.ok(promoCodeService.searchPromoCodeRegistrationLogs(text, code, pageNo, pageSize));
	}
}
