/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.services;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import com.plutospace.events.commons.data.CustomPageResponse;
import com.plutospace.events.domain.data.request.LoginAccountUserRequest;
import com.plutospace.events.domain.data.request.RegisterBusinessAccountRequest;
import com.plutospace.events.domain.data.request.RegisterPersonalAccountRequest;
import com.plutospace.events.domain.data.response.AccountUserResponse;

public interface AccountUserService {

	AccountUserResponse registerPersonalAccount(RegisterPersonalAccountRequest request)
			throws NoSuchAlgorithmException, InvalidKeySpecException;

	AccountUserResponse registerBusinessAccount(RegisterBusinessAccountRequest request)
			throws NoSuchAlgorithmException, InvalidKeySpecException;

	CustomPageResponse<AccountUserResponse> retrieveAllAccounts(int pageNo, int pageSize);

	AccountUserResponse login(LoginAccountUserRequest loginAccountUserRequest)
			throws NoSuchAlgorithmException, InvalidKeySpecException;

	AccountUserResponse retrieveAccountUser(String id);
}
