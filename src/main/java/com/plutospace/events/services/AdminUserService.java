/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.services;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import com.plutospace.events.domain.data.request.ChangeAdminUserPasswordRequest;
import com.plutospace.events.domain.data.request.CreateAdminUserRequest;
import com.plutospace.events.domain.data.request.LoginAdminUserRequest;
import com.plutospace.events.domain.data.response.AdminUserResponse;
import com.plutospace.events.domain.data.response.OperationalResponse;

public interface AdminUserService {

	AdminUserResponse createAdminUser(CreateAdminUserRequest createAdminUserRequest)
			throws NoSuchAlgorithmException, InvalidKeySpecException;

	OperationalResponse changeAdminUserPassword(ChangeAdminUserPasswordRequest changeAdminUserPasswordRequest,
			String accountId) throws NoSuchAlgorithmException, InvalidKeySpecException;

	AdminUserResponse login(LoginAdminUserRequest loginAdminUserRequest)
			throws NoSuchAlgorithmException, InvalidKeySpecException;
}
