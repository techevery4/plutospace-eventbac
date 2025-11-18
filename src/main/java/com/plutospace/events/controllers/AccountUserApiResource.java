/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.controllers;

import java.net.URI;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import com.plutospace.events.commons.data.CustomPageResponse;
import com.plutospace.events.commons.definitions.GeneralConstants;
import com.plutospace.events.commons.definitions.PropertyConstants;
import com.plutospace.events.commons.utils.SecurityMapper;
import com.plutospace.events.domain.data.request.LoginAccountUserRequest;
import com.plutospace.events.domain.data.request.RegisterBusinessAccountRequest;
import com.plutospace.events.domain.data.request.RegisterPersonalAccountRequest;
import com.plutospace.events.domain.data.response.AccountResponse;
import com.plutospace.events.domain.data.response.AccountUserResponse;
import com.plutospace.events.domain.data.response.OperationalResponse;
import com.plutospace.events.services.AccountUserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

import static com.plutospace.events.commons.definitions.ApiResourceConstants.*;

@RestController
@RequestMapping(ACCOUNT_USERS)
@Tag(name = "Account User Endpoints", description = "These endpoints manages account users on PlutoSpace Events")
@RequiredArgsConstructor
public class AccountUserApiResource {

	private final AccountUserService accountUserService;
	private final SecurityMapper securityMapper;
	private final PropertyConstants propertyConstants;
	private final HttpServletRequest request;

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

	@GetMapping(path = "/single", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(description = "This endpoint retrieves a single account user")
	public ResponseEntity<AccountUserResponse> retrieveAccountUser() {
		String id = securityMapper.retrieveAccountUserId(request.getHeader(GeneralConstants.TOKEN_KEY),
				propertyConstants.getEventsLoginEncryptionSecretKey());
		return ResponseEntity.ok(accountUserService.retrieveAccountUser(id));
	}

	@PostMapping(path = "/login", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	@Operation(description = "This endpoint performs an account user login")
	public ResponseEntity<AccountUserResponse> login(@RequestBody LoginAccountUserRequest loginAccountUserRequest)
			throws NoSuchAlgorithmException, InvalidKeySpecException {
		return ResponseEntity.ok(accountUserService.login(loginAccountUserRequest));
	}

	@GetMapping(path = "/check-user", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(description = "This endpoint checks if user already exists")
	public ResponseEntity<OperationalResponse> checkIfUserExists(@RequestParam(value = "email") String email) {
		return ResponseEntity.ok(accountUserService.checkIfUserExists(email));
	}

	@PostMapping(path = "/bulk-ids", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(description = "This endpoint retrieves bulk account users by ids")
	public ResponseEntity<List<AccountUserResponse>> retrieveAccountUser(@RequestBody List<String> ids) {
		return ResponseEntity.ok(accountUserService.retrieveAccountUser(ids));
	}

	@GetMapping(path = "/my-account", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(description = "This endpoint retrieves my account information")
	public ResponseEntity<AccountResponse> retrieveMyAccount() {
		String id = securityMapper.retrieveAccountId(request.getHeader(GeneralConstants.TOKEN_KEY),
				propertyConstants.getEventsLoginEncryptionSecretKey());
		return ResponseEntity.ok(accountUserService.retrieveMyAccount(id));
	}

	@GetMapping(path = "/all-accounts", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(description = "This endpoint retrieves all accounts")
	public ResponseEntity<CustomPageResponse<AccountResponse>> retrieveAccounts(
			@RequestParam(value = "pageNo") int pageNo, @RequestParam(value = "pageSize") int pageSize) {
		return ResponseEntity.ok(accountUserService.retrieveAccounts(pageNo, pageSize));
	}

	@GetMapping(path = "/{accountId}/users", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(description = "This endpoint retrieves all users tied to an account")
	public ResponseEntity<CustomPageResponse<AccountUserResponse>> retrieveAllUsersTiedToAnAccount(
			@PathVariable String accountId, @RequestParam(value = "pageNo") int pageNo,
			@RequestParam(value = "pageSize") int pageSize) {
		return ResponseEntity.ok(accountUserService.retrieveAllUsersTiedToAnAccount(accountId, pageNo, pageSize));
	}
}
