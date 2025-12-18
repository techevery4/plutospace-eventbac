/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.controllers;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.plutospace.events.commons.data.CustomPageResponse;
import com.plutospace.events.commons.definitions.GeneralConstants;
import com.plutospace.events.commons.definitions.PropertyConstants;
import com.plutospace.events.commons.utils.SecurityMapper;
import com.plutospace.events.domain.data.request.PayForPlanRequest;
import com.plutospace.events.domain.data.response.OperationalResponse;
import com.plutospace.events.domain.data.response.PlanPaymentHistoryResponse;
import com.plutospace.events.services.PlanPaymentHistoryService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

import static com.plutospace.events.commons.definitions.ApiResourceConstants.*;

@RestController
@RequestMapping(PLAN_PAYMENT_HISTORIES)
@Tag(name = "Plan Payment History Endpoints", description = "These endpoints manages plan payment histories on PlutoSpace Events")
@RequiredArgsConstructor
public class PlanPaymentHistoryApiResource {

	private final PlanPaymentHistoryService planPaymentHistoryService;
	private final SecurityMapper securityMapper;
	private final PropertyConstants propertyConstants;
	private final HttpServletRequest request;

	@PostMapping(path = "/verify-paystack", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	@Operation(description = "This endpoint verifies a plan payment with Paystack")
	public ResponseEntity<OperationalResponse> payWithPaystack(@RequestBody PayForPlanRequest payForPlanRequest) {
		String accountId = securityMapper.retrieveAccountId(request.getHeader(GeneralConstants.TOKEN_KEY),
				propertyConstants.getEventsLoginEncryptionSecretKey());
		return ResponseEntity.ok(planPaymentHistoryService.buyPlanWithPaystack(payForPlanRequest, accountId));
	}

	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(description = "This endpoint retrieves all plan payment histories")
	public ResponseEntity<CustomPageResponse<PlanPaymentHistoryResponse>> retrievePlanPaymentHistories(
			@RequestParam(name = "pageNo") int pageNo, @RequestParam(name = "pageSize") int pageSize) {
		return ResponseEntity.ok(planPaymentHistoryService.retrievePlanPaymentHistories(pageNo, pageSize));
	}

	@GetMapping(path = "/my", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(description = "This endpoint retrieves my plan payment histories")
	public ResponseEntity<CustomPageResponse<PlanPaymentHistoryResponse>> retrieveMyPlanPaymentHistories(
			@RequestParam(name = "pageNo") int pageNo, @RequestParam(name = "pageSize") int pageSize) {
		String accountId = securityMapper.retrieveAccountId(request.getHeader(GeneralConstants.TOKEN_KEY),
				propertyConstants.getEventsLoginEncryptionSecretKey());
		return ResponseEntity.ok(planPaymentHistoryService.retrieveMyPlanPaymentHistories(accountId, pageNo, pageSize));
	}

	@GetMapping(path = "/search", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(description = "This endpoint searches through plan payment histories")
	public ResponseEntity<CustomPageResponse<PlanPaymentHistoryResponse>> searchPlanPaymentHistories(
			@RequestParam(name = "text") String text, @RequestParam(name = "pageNo") int pageNo,
			@RequestParam(name = "pageSize") int pageSize) {
		return ResponseEntity.ok(planPaymentHistoryService.searchPlanPaymentHistories(text, pageNo, pageSize));
	}

	@GetMapping(path = "/search/my", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(description = "This endpoint searches through my plan payment histories")
	public ResponseEntity<CustomPageResponse<PlanPaymentHistoryResponse>> searchMyPlanPaymentHistories(
			@RequestParam(name = "text") String text, @RequestParam(name = "pageNo") int pageNo,
			@RequestParam(name = "pageSize") int pageSize) {
		String accountId = securityMapper.retrieveAccountId(request.getHeader(GeneralConstants.TOKEN_KEY),
				propertyConstants.getEventsLoginEncryptionSecretKey());
		return ResponseEntity
				.ok(planPaymentHistoryService.searchMyPlanPaymentHistories(accountId, text, pageNo, pageSize));
	}
}
