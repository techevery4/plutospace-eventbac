/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.controllers;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.plutospace.events.commons.definitions.GeneralConstants;
import com.plutospace.events.domain.data.response.AccountSessionResponse;
import com.plutospace.events.domain.data.response.OperationalResponse;
import com.plutospace.events.services.AccountSessionService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

import static com.plutospace.events.commons.definitions.ApiResourceConstants.ACCOUNT_SESSIONS;

@RestController
@RequestMapping(ACCOUNT_SESSIONS)
@Tag(name = "Account Session Endpoints", description = "These endpoints manages account sessions on PlutoSpace Events")
@RequiredArgsConstructor
public class AccountSessionApiResource {

	private final AccountSessionService accountSessionService;
	private final HttpServletRequest request;

	@GetMapping(path = "/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(description = "This endpoint retrieves account session for a particular user")
	public ResponseEntity<List<AccountSessionResponse>> retrieveAccountSessions(@PathVariable String userId) {
		return ResponseEntity.ok(accountSessionService.retrieveAccountSessions(userId));
	}

	@GetMapping(path = "/validate", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(description = "This endpoint validates an account session")
	public ResponseEntity<OperationalResponse> validateToken()
			throws NoSuchAlgorithmException, InvalidKeySpecException {
		return ResponseEntity.ok(accountSessionService.validateToken(request.getHeader(GeneralConstants.TOKEN_KEY),
				request.getHeader("User-Agent")));
	}
}
