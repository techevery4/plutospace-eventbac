/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.services;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.List;

import com.plutospace.events.commons.data.CustomPageResponse;
import com.plutospace.events.domain.data.request.*;
import com.plutospace.events.domain.data.response.AccountResponse;
import com.plutospace.events.domain.data.response.AccountUserResponse;
import com.plutospace.events.domain.data.response.OperationalResponse;

public interface AccountUserService {

	AccountUserResponse registerPersonalAccount(RegisterPersonalAccountRequest request)
			throws NoSuchAlgorithmException, InvalidKeySpecException;

	AccountUserResponse registerBusinessAccount(RegisterBusinessAccountRequest request)
			throws NoSuchAlgorithmException, InvalidKeySpecException;

	CustomPageResponse<AccountUserResponse> retrieveAllAccounts(int pageNo, int pageSize);

	AccountUserResponse login(LoginAccountUserRequest loginAccountUserRequest)
			throws NoSuchAlgorithmException, InvalidKeySpecException;

	OperationalResponse changeAccountUserPassword(ChangeAccountUserPasswordRequest changeAccountUserPasswordRequest,
			String accountUserId) throws NoSuchAlgorithmException, InvalidKeySpecException;

	AccountUserResponse retrieveAccountUser(String id);

	OperationalResponse checkIfUserExists(String email);

	List<AccountUserResponse> retrieveAccountUserByEmail(List<String> emails);

	List<AccountUserResponse> retrieveAccountUser(List<String> ids);

	AccountResponse retrieveMyAccount(String id);

	CustomPageResponse<AccountResponse> retrieveAccounts(int pageNo, int pageSize);

	CustomPageResponse<AccountUserResponse> retrieveAllUsersTiedToAnAccount(String id, int pageNo, int pageSize);

	CustomPageResponse<AccountUserResponse> searchAccountUser(String text, int pageNo, int pageSize);

	AccountUserResponse inviteAccountUser(InviteAccountUserRequest inviteAccountUserRequest, String accountId);

	OperationalResponse reInviteAccountUser(List<String> ids, String accountId);

	OperationalResponse activateAccountUser(String id, String accountId);

	OperationalResponse deactivateAccountUser(String id, String accountId);
}
