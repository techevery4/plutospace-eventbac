/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.services.mappers;

import org.springframework.stereotype.Component;

import com.plutospace.events.domain.data.request.CreateAccountSessionRequest;
import com.plutospace.events.domain.data.response.AccountSessionResponse;
import com.plutospace.events.domain.entities.AccountSession;

@Component
public class AccountSessionMapper {

	public AccountSessionResponse toResponse(AccountSession accountSession) {
		return AccountSessionResponse.instance(accountSession.getId(), accountSession.getUserId(),
				accountSession.getAccountId(), accountSession.getUserAgent(), accountSession.getRenewed(),
				accountSession.getLastUseTime(), accountSession.getCreatedOn());
	}

	public AccountSession toEntity(CreateAccountSessionRequest createAccountSessionRequest) {
		return AccountSession.instance(createAccountSessionRequest.getUserId(),
				createAccountSessionRequest.getAccountId(), createAccountSessionRequest.getUserAgent(), null,
				createAccountSessionRequest.getToken(), null);
	}
}
