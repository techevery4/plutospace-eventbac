/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.controllers;

import java.net.URI;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import com.plutospace.events.commons.definitions.GeneralConstants;
import com.plutospace.events.commons.definitions.PropertyConstants;
import com.plutospace.events.commons.utils.SecurityMapper;
import com.plutospace.events.domain.data.request.ChangeAdminUserPasswordRequest;
import com.plutospace.events.domain.data.request.CreateAdminUserRequest;
import com.plutospace.events.domain.data.request.LoginAdminUserRequest;
import com.plutospace.events.domain.data.response.AdminUserResponse;
import com.plutospace.events.domain.data.response.OperationalResponse;
import com.plutospace.events.services.AdminUserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

import static com.plutospace.events.commons.definitions.ApiResourceConstants.ADMIN_USERS;
import static com.plutospace.events.commons.definitions.ApiResourceConstants.ADMIN_USERS_RESOURCE_ID;

@RestController
@RequestMapping(ADMIN_USERS)
@Tag(name = "Admin User Endpoints", description = "These endpoints manages admin users on PlutoSpace Events")
@RequiredArgsConstructor
public class AdminUserApiResource {

	private final AdminUserService adminUserService;
	private final SecurityMapper securityMapper;
	private final PropertyConstants propertyConstants;
	private final HttpServletRequest request;

	@PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	@Operation(description = "This endpoint creates a new admin user on PlutoSpace Events")
	public ResponseEntity<AdminUserResponse> createAdminUser(@RequestBody CreateAdminUserRequest createAdminUserRequest,
			UriComponentsBuilder uriComponentsBuilder) throws NoSuchAlgorithmException, InvalidKeySpecException {
		AdminUserResponse adminUserResponse = adminUserService.createAdminUser(createAdminUserRequest);

		String location = uriComponentsBuilder.path(ADMIN_USERS_RESOURCE_ID).buildAndExpand(adminUserResponse.getId())
				.toUriString();
		URI uri = URI.create(location);
		return ResponseEntity.created(uri).body(adminUserResponse);
	}

	@PostMapping(path = "/login", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	@Operation(description = "This endpoint performs an admin user login")
	public ResponseEntity<AdminUserResponse> login(@RequestBody LoginAdminUserRequest loginAdminUserRequest)
			throws NoSuchAlgorithmException, InvalidKeySpecException {
		return ResponseEntity.ok(adminUserService.login(loginAdminUserRequest));
	}

	@PostMapping(path = "/change-password", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	@Operation(description = "This endpoint changes the password of an admin user")
	public ResponseEntity<OperationalResponse> changeAdminUserPassword(
			@RequestBody ChangeAdminUserPasswordRequest changeAdminUserPasswordRequest)
			throws NoSuchAlgorithmException, InvalidKeySpecException {
		String id = securityMapper.retrieveAccountId(request.getHeader(GeneralConstants.TOKEN_KEY),
				propertyConstants.getEventsLoginEncryptionSecretKey());
		return ResponseEntity.ok(adminUserService.changeAdminUserPassword(changeAdminUserPasswordRequest, id));
	}
}
