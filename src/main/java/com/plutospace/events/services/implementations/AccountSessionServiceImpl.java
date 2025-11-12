/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.services.implementations;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.plutospace.events.commons.definitions.GeneralConstants;
import com.plutospace.events.commons.definitions.PropertyConstants;
import com.plutospace.events.commons.exception.UnauthorizedAccessException;
import com.plutospace.events.commons.utils.HashPassword;
import com.plutospace.events.commons.utils.SecurityMapper;
import com.plutospace.events.domain.data.request.CreateAccountSessionRequest;
import com.plutospace.events.domain.data.response.AccountSessionResponse;
import com.plutospace.events.domain.data.response.OperationalResponse;
import com.plutospace.events.domain.entities.AccountSession;
import com.plutospace.events.domain.repositories.AccountSessionRepository;
import com.plutospace.events.services.AccountSessionService;
import com.plutospace.events.services.mappers.AccountSessionMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountSessionServiceImpl implements AccountSessionService {

	private final AccountSessionRepository accountSessionRepository;
	private final AccountSessionMapper accountSessionMapper;
	private final SecurityMapper securityMapper;
	private final PropertyConstants propertyConstants;
	private final HashPassword hashPassword;

	@Override
	public void createSession(CreateAccountSessionRequest createAccountSessionRequest)
			throws NoSuchAlgorithmException, InvalidKeySpecException {
		AccountSession checkAccountSession = accountSessionRepository.findByAccountIdAndUserIdAndUserAgentIgnoreCase(
				createAccountSessionRequest.getAccountId(), createAccountSessionRequest.getUserId(),
				createAccountSessionRequest.getUserAgent());
		createAccountSessionRequest.setToken(hashPassword.hashPass(createAccountSessionRequest.getToken()));

		try {
			if (checkAccountSession != null) {
				checkAccountSession.setToken(createAccountSessionRequest.getToken());
				checkAccountSession.setRenewed(true);
				accountSessionRepository.save(checkAccountSession);
			} else {
				AccountSession accountSession = accountSessionMapper.toEntity(createAccountSessionRequest);
				accountSession.setRenewed(false);
				accountSessionRepository.save(accountSession);
			}
		} catch (DataIntegrityViolationException e) {
			throw new DataIntegrityViolationException(e.getLocalizedMessage());
		}
	}

	@Override
	public OperationalResponse validateToken(String token, String userAgent)
			throws NoSuchAlgorithmException, InvalidKeySpecException {
		if (token == null)
			throw new UnauthorizedAccessException(
					"You cannot complete this request as necessary credentials are missing. Kindly login again");
		String decryptedToken = securityMapper.extractDetailsFromLoginToken(token,
				propertyConstants.getEventsLoginEncryptionSecretKey());
		String[] words = decryptedToken.split(":");
		if (words.length < 3)
			throw new UnauthorizedAccessException("Your session has expired. Kindly login again to restart session");

		AccountSession accountSession = accountSessionRepository
				.findByAccountIdAndUserIdAndUserAgentIgnoreCase(words[1], words[2], userAgent);
		if (accountSession == null)
			throw new UnauthorizedAccessException("Kindly login to start a new session");
		if (!hashPassword.validatePass(token, accountSession.getToken()))
			throw new UnauthorizedAccessException("This session is invalid. Kindly login to start a new session");

		return OperationalResponse.instance(GeneralConstants.SUCCESS_MESSAGE);
	}

	@Override
	public List<AccountSessionResponse> retrieveAccountSessions(String userId) {
		List<AccountSession> accountSessions = accountSessionRepository.findByUserIdOrderByLastUseTimeDesc(userId);
		if (accountSessions.isEmpty())
			return new ArrayList<>();

		return accountSessions.stream().map(accountSessionMapper::toResponse).toList();
	}
}
