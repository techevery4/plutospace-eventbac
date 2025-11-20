/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.services;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.List;

import com.plutospace.events.commons.data.CustomPageResponse;
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

	List<AdminUserResponse> retrieveAdminUser(List<String> ids);

	OperationalResponse activateAdminUser(String id);

	OperationalResponse deactivateAdminUser(String id);

	OperationalResponse deletePendingAdminUser(String id);

	CustomPageResponse<AdminUserResponse> searchAdminUser(String text, int pageNo, int pageSize);
}
