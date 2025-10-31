/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.controllers;

import java.net.URI;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import com.plutospace.events.commons.data.CustomPageResponse;
import com.plutospace.events.domain.data.request.LoginAccountUserRequest;
import com.plutospace.events.domain.data.request.RegisterBusinessAccountRequest;
import com.plutospace.events.domain.data.request.RegisterPersonalAccountRequest;
import com.plutospace.events.domain.data.response.AccountUserResponse;
import com.plutospace.events.services.AccountUserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import static com.plutospace.events.commons.definitions.ApiResourceConstants.*;

@RestController
@RequestMapping(ACCOUNT_USERS)
@Tag(name = "Account User Endpoints", description = "These endpoints manages account users on PlutoSpace Events")
@RequiredArgsConstructor
public class AccountUserApiResource {

	private final AccountUserService accountUserService;

	@PostMapping(path = "/personal", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	@Operation(description = "This endpoint registers a new personal account user on PlutoSpace Events")
	public ResponseEntity<AccountUserResponse> registerPersonalAccount(
			@RequestBody RegisterPersonalAccountRequest registerPersonalAccountRequest,
			UriComponentsBuilder uriComponentsBuilder) throws NoSuchAlgorithmException, InvalidKeySpecException {
		AccountUserResponse accountUserResponse = accountUserService
				.registerPersonalAccount(registerPersonalAccountRequest);

		String location = uriComponentsBuilder.path(ACCOUNT_USERS_RESOURCE_ID)
				.buildAndExpand(accountUserResponse.getId()).toUriString();
		URI uri = URI.create(location);
		return ResponseEntity.created(uri).body(accountUserResponse);
	}

	@PostMapping(path = "/business", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	@Operation(description = "This endpoint registers a new business account user on PlutoSpace Events")
	public ResponseEntity<AccountUserResponse> registerBusinessAccount(
			@RequestBody RegisterBusinessAccountRequest registerBusinessAccountRequest,
			UriComponentsBuilder uriComponentsBuilder) throws NoSuchAlgorithmException, InvalidKeySpecException {
		AccountUserResponse accountUserResponse = accountUserService
				.registerBusinessAccount(registerBusinessAccountRequest);

		String location = uriComponentsBuilder.path(ACCOUNT_USERS_RESOURCE_ID)
				.buildAndExpand(accountUserResponse.getId()).toUriString();
		URI uri = URI.create(location);
		return ResponseEntity.created(uri).body(accountUserResponse);
	}

	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(description = "This endpoint retrieves all account users")
	public ResponseEntity<CustomPageResponse<AccountUserResponse>> retrieveAllAccounts(
			@RequestParam(value = "pageNo") int pageNo, @RequestParam(value = "pageSize") int pageSize) {
		return ResponseEntity.ok(accountUserService.retrieveAllAccounts(pageNo, pageSize));
	}

	@GetMapping(path = RESOURCE_ID, produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(description = "This endpoint retrieves a single account user")
	public ResponseEntity<AccountUserResponse> retrieveAccountUser(@PathVariable String id) {
		return ResponseEntity.ok(accountUserService.retrieveAccountUser(id));
	}

	@PostMapping(path = "/login", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	@Operation(description = "This endpoint performs an account user login")
	public ResponseEntity<AccountUserResponse> login(@RequestBody LoginAccountUserRequest loginAccountUserRequest)
			throws NoSuchAlgorithmException, InvalidKeySpecException {
		return ResponseEntity.ok(accountUserService.login(loginAccountUserRequest));
	}
}
