/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.services;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.List;

import com.plutospace.events.domain.data.request.CreateAccountSessionRequest;
import com.plutospace.events.domain.data.response.AccountSessionResponse;
import com.plutospace.events.domain.data.response.OperationalResponse;

public interface AccountSessionService {

	void createSession(CreateAccountSessionRequest createAccountSessionRequest)
			throws NoSuchAlgorithmException, InvalidKeySpecException;

	OperationalResponse validateToken(String token, String userAgent)
			throws NoSuchAlgorithmException, InvalidKeySpecException;

	List<AccountSessionResponse> retrieveAccountSessions(String userId);

	OperationalResponse terminateSession(String token, String userAgent)
			throws NoSuchAlgorithmException, InvalidKeySpecException;
}
