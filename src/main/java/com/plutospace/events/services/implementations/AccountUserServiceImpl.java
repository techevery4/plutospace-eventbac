/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.services.implementations;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.plutospace.events.commons.data.CustomPageResponse;
import com.plutospace.events.commons.definitions.GeneralConstants;
import com.plutospace.events.commons.definitions.PropertyConstants;
import com.plutospace.events.commons.exception.GeneralPlatformDomainRuleException;
import com.plutospace.events.commons.exception.LoginDisputeException;
import com.plutospace.events.commons.exception.ResourceAlreadyExistsException;
import com.plutospace.events.commons.exception.ResourceNotFoundException;
import com.plutospace.events.commons.utils.HashPassword;
import com.plutospace.events.commons.utils.SecurityMapper;
import com.plutospace.events.domain.data.PlanType;
import com.plutospace.events.domain.data.request.CreateAccountSessionRequest;
import com.plutospace.events.domain.data.request.LoginAccountUserRequest;
import com.plutospace.events.domain.data.request.RegisterBusinessAccountRequest;
import com.plutospace.events.domain.data.request.RegisterPersonalAccountRequest;
import com.plutospace.events.domain.data.response.AccountUserResponse;
import com.plutospace.events.domain.data.response.OperationalResponse;
import com.plutospace.events.domain.entities.Account;
import com.plutospace.events.domain.entities.AccountUser;
import com.plutospace.events.domain.entities.Plan;
import com.plutospace.events.domain.repositories.AccountRepository;
import com.plutospace.events.domain.repositories.AccountUserRepository;
import com.plutospace.events.domain.repositories.PlanRepository;
import com.plutospace.events.services.AccountSessionService;
import com.plutospace.events.services.AccountUserService;
import com.plutospace.events.services.mappers.AccountUserMapper;
import com.plutospace.events.validation.AccountUserValidator;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountUserServiceImpl implements AccountUserService {

	private final AccountRepository accountRepository;
	private final AccountUserRepository accountUserRepository;
	private final PlanRepository planRepository;
	private final AccountSessionService accountSessionService;
	private final PropertyConstants propertyConstants;
	private final AccountUserMapper accountUserMapper;
	private final AccountUserValidator accountUserValidator;
	private final HashPassword hashPassword;
	private final HttpServletRequest request;
	private final HttpServletResponse headers;
	private final SecurityMapper securityMapper;

	@Override
	public AccountUserResponse registerPersonalAccount(RegisterPersonalAccountRequest request)
			throws NoSuchAlgorithmException, InvalidKeySpecException {
		accountUserValidator.validate(request);

		Plan plan = retrievePlanById(request.planId());
		if (!plan.getType().equals(PlanType.PERSONAL))
			throw new GeneralPlatformDomainRuleException("User can only register for a personal plan");

		AccountUser accountUser = accountUserMapper.toEntity(request);
		if (accountUserRepository.existsByEmailIgnoreCase(request.email()))
			throw new ResourceAlreadyExistsException("User Already Exists");

		// Hashing the password
		accountUser.setPassword(hashPassword.hashPass(request.password()));

		Account account = new Account();
		account.setPlanId(request.planId());
		account.setNumberOfMembers(1L);

		try {
			AccountUser savedAccountUser = accountUserRepository.save(accountUser);

			account.setAccountOwner(savedAccountUser.getId());
			Account savedAccount = accountRepository.save(account);
			savedAccountUser.setAccountId(savedAccount.getId());
			accountUserRepository.save(savedAccountUser);

			return accountUserMapper.toResponse(savedAccountUser);
		} catch (DataIntegrityViolationException e) {
			throw new DataIntegrityViolationException(e.getLocalizedMessage());
		}
	}

	@Override
	public AccountUserResponse registerBusinessAccount(RegisterBusinessAccountRequest request)
			throws NoSuchAlgorithmException, InvalidKeySpecException {
		accountUserValidator.validate(request);

		Plan plan = retrievePlanById(request.planId());
		if (!plan.getType().equals(PlanType.BUSINESS))
			throw new GeneralPlatformDomainRuleException("User can only register for a business plan");

		AccountUser accountUser = accountUserMapper.toEntity(request);
		if (accountUserRepository.existsByEmailIgnoreCase(request.email()))
			throw new ResourceAlreadyExistsException("User Already Exists");

		// Hashing the password
		accountUser.setPassword(hashPassword.hashPass(request.password()));

		Account account = new Account();
		account.setPlanId(request.planId());
		account.setNumberOfMembers(1L);

		try {
			AccountUser savedAccountUser = accountUserRepository.save(accountUser);

			account.setAccountOwner(savedAccountUser.getId());
			Account savedAccount = accountRepository.save(account);
			savedAccountUser.setAccountId(savedAccount.getId());
			accountUserRepository.save(savedAccountUser);

			return accountUserMapper.toResponse(savedAccountUser);
		} catch (DataIntegrityViolationException e) {
			throw new DataIntegrityViolationException(e.getLocalizedMessage());
		}
	}

	@Override
	public CustomPageResponse<AccountUserResponse> retrieveAllAccounts(int pageNo, int pageSize) {
		Pageable pageable = PageRequest.of(pageNo, pageSize);

		Page<AccountUser> accountUsers = accountUserRepository.findAll(pageable);

		return accountUserMapper.toPagedResponse(accountUsers);
	}

	@Override
	public AccountUserResponse login(LoginAccountUserRequest loginAccountUserRequest)
			throws NoSuchAlgorithmException, InvalidKeySpecException {
		AccountUser accountUser = retrieveAccountUserByEmail(loginAccountUserRequest.email());

		if (!hashPassword.validatePass(loginAccountUserRequest.password(), accountUser.getPassword()))
			throw new LoginDisputeException("Username or Password Incorrect");

		// Saving login time
		accountUser.setLastLogin(LocalDateTime.now());
		accountUserRepository.save(accountUser);

		String token = securityMapper.generateEncryptedLoginTokenForUser(accountUser.getId(),
				accountUser.getAccountId(), propertyConstants.getEventsLoginEncryptionSecretKey());
		headers.setHeader(GeneralConstants.TOKEN_KEY, token);

		String userAgent = request.getHeader("User-Agent");
		CreateAccountSessionRequest createAccountSessionRequest = new CreateAccountSessionRequest(accountUser.getId(),
				accountUser.getAccountId(), userAgent, token);
		accountSessionService.createSession(createAccountSessionRequest);

		return accountUserMapper.toResponse(accountUser);
	}

	@Override
	public AccountUserResponse retrieveAccountUser(String id) {
		AccountUser accountUser = retrieveAccountUserById(id);

		return accountUserMapper.toResponse(accountUser);
	}

	@Override
	public OperationalResponse checkIfUserExists(String email) {
		retrieveAccountUserByEmail(email);

		return OperationalResponse.instance(GeneralConstants.SUCCESS_MESSAGE);
	}

	@Override
	public List<AccountUserResponse> retrieveAccountUserByEmail(List<String> emails) {
		List<AccountUser> accountUsers = accountUserRepository.findByEmailIgnoreCaseIn(emails);

		return accountUsers.stream().map(accountUserMapper::toResponse).toList();
	}

	private Plan retrievePlanById(String id) {
		return planRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Plan Not Found"));
	}

	private AccountUser retrieveAccountUserByEmail(String email) {
		return accountUserRepository.findByEmailIgnoreCase(email)
				.orElseThrow(() -> new ResourceNotFoundException("User Not Found"));
	}

	private AccountUser retrieveAccountUserById(String id) {
		return accountUserRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User Not Found"));
	}
}
