/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.services.implementations;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.plutospace.events.commons.data.CustomPageResponse;
import com.plutospace.events.commons.definitions.GeneralConstants;
import com.plutospace.events.commons.definitions.PropertyConstants;
import com.plutospace.events.commons.exception.*;
import com.plutospace.events.commons.utils.HashPassword;
import com.plutospace.events.commons.utils.SecurityMapper;
import com.plutospace.events.domain.data.PlanType;
import com.plutospace.events.domain.data.request.*;
import com.plutospace.events.domain.data.response.AccountResponse;
import com.plutospace.events.domain.data.response.AccountUserResponse;
import com.plutospace.events.domain.data.response.OperationalResponse;
import com.plutospace.events.domain.data.response.PlanResponse;
import com.plutospace.events.domain.entities.Account;
import com.plutospace.events.domain.entities.AccountUser;
import com.plutospace.events.domain.entities.Plan;
import com.plutospace.events.domain.repositories.AccountRepository;
import com.plutospace.events.domain.repositories.AccountUserRepository;
import com.plutospace.events.domain.repositories.PlanRepository;
import com.plutospace.events.integrations.MiddlewareService;
import com.plutospace.events.intelligence.search.DatabaseSearchService;
import com.plutospace.events.services.AccountSessionService;
import com.plutospace.events.services.AccountUserService;
import com.plutospace.events.services.PlanService;
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
	private final PlanService planService;
	private final DatabaseSearchService databaseSearchService;
	private final MiddlewareService middlewareService;
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
		account.setIsDefaulted(true);
		account.setPlanDueDate(LocalDateTime.now());

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
		account.setIsDefaulted(true);
		account.setPlanDueDate(LocalDateTime.now());

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
	public OperationalResponse changeAccountUserPassword(
			ChangeAccountUserPasswordRequest changeAccountUserPasswordRequest, String accountUserId)
			throws NoSuchAlgorithmException, InvalidKeySpecException {
		accountUserValidator.validate(changeAccountUserPasswordRequest);
		AccountUser accountUser = retrieveAccountUserById(accountUserId);

		if (!hashPassword.validatePass(changeAccountUserPasswordRequest.oldPassword(), accountUser.getPassword()))
			throw new GeneralPlatformDomainRuleException("Old Password is incorrect");

		accountUser.setPassword(hashPassword.hashPass(changeAccountUserPasswordRequest.newPassword()));

		try {
			accountUserRepository.save(accountUser);

			return OperationalResponse.instance(GeneralConstants.SUCCESS_MESSAGE);
		} catch (DataIntegrityViolationException e) {
			throw new DataIntegrityViolationException(e.getLocalizedMessage());
		}
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

	@Override
	public List<AccountUserResponse> retrieveAccountUser(List<String> ids) {
		List<AccountUser> accountUsers = accountUserRepository.findByIdIn(ids);

		return accountUsers.stream().map(accountUserMapper::toResponse).toList();
	}

	@Override
	public AccountResponse retrieveMyAccount(String id) {
		Account account = retrieveAccountById(id);
		List<AccountUserResponse> accountUserResponses = retrieveAccountUser(List.of(account.getAccountOwner()));
		List<PlanResponse> planResponses = planService.retrievePlan(List.of(account.getPlanId()));

		return accountUserMapper.toResponse(account, accountUserResponses.get(0), planResponses.get(0));
	}

	@Override
	public CustomPageResponse<AccountResponse> retrieveAccounts(int pageNo, int pageSize) {
		Pageable pageable = PageRequest.of(pageNo, pageSize);

		Page<Account> accounts = accountRepository.findAllByOrderByCreatedOnDesc(pageable);
		if (accounts.getTotalElements() == 0)
			return new CustomPageResponse<>();

		List<String> accountUserIds = accounts.getContent().stream().map(Account::getAccountOwner).toList();
		List<String> planIds = accounts.getContent().stream().map(Account::getPlanId).toList();
		List<AccountUserResponse> accountUserResponses = retrieveAccountUser(accountUserIds);
		List<PlanResponse> planResponses = planService.retrievePlan(planIds);

		Map<String, AccountUserResponse> accountUserResponseMap = new HashMap<>();
		for (AccountUserResponse accountUserResponse : accountUserResponses) {
			accountUserResponseMap.putIfAbsent(accountUserResponse.getId(), accountUserResponse);
		}
		Map<String, PlanResponse> planResponseMap = new HashMap<>();
		for (PlanResponse planResponse : planResponses) {
			planResponseMap.putIfAbsent(planResponse.getId(), planResponse);
		}

		return accountUserMapper.toPagedResponse(accounts, accountUserResponseMap, planResponseMap);
	}

	@Override
	public CustomPageResponse<AccountUserResponse> retrieveAllUsersTiedToAnAccount(String id, int pageNo,
			int pageSize) {
		Pageable pageable = PageRequest.of(pageNo, pageSize);

		Page<AccountUser> accountUsers = accountUserRepository.findByAccountIdOrderByLastNameAsc(id, pageable);

		return accountUserMapper.toPagedResponse(accountUsers);
	}

	@Override
	public CustomPageResponse<AccountUserResponse> searchAccountUser(String text, int pageNo, int pageSize) {
		Pageable pageable = PageRequest.of(pageNo, pageSize);

		List<String> fields = List.of("firstName", "lastName", "name", "email");
		Page<AccountUser> accountUsers = databaseSearchService.findByDynamicFilter(text, fields, pageable);
		log.info("users {}", accountUsers);

		return accountUserMapper.toPagedResponse(accountUsers);
	}

	@Override
	public AccountUserResponse inviteAccountUser(InviteAccountUserRequest inviteAccountUserRequest, String accountId) {
		accountUserValidator.validate(inviteAccountUserRequest);
		Account account = retrieveAccountById(accountId);
		AccountUser accountOwner = retrieveAccountUserById(account.getAccountOwner());

		if (accountUserRepository.existsByEmailIgnoreCase(inviteAccountUserRequest.email()))
			throw new ResourceAlreadyExistsException("User Already Exists");
		AccountUser accountUser = accountUserMapper.toEntity(inviteAccountUserRequest, accountId);

		try {
			AccountUser savedAccountUser = accountUserRepository.save(accountUser);

			// Sending email
			GoMailerRequest goMailerRequest = new GoMailerRequest();
			GoMailerRequest.MailData mailData = new GoMailerRequest.MailData();
			mailData.setCompany(accountOwner.getName());
			mailData.setYear(LocalDateTime.now().getYear());
			String url = propertyConstants.getFrontendBaseUrl() + propertyConstants.getInviteUserUrl() + "?ixxd="
					+ savedAccountUser.getId();
			mailData.setInvitationLink(url);
			mailData.setFirstName(inviteAccountUserRequest.firstName());
			mailData.setSupportEmail(propertyConstants.getTechEveryWhereSupportEmail());
			goMailerRequest.setRecipient_email(inviteAccountUserRequest.email());
			goMailerRequest.setData(mailData);
			middlewareService.sendInvite(goMailerRequest);

			return accountUserMapper.toResponse(accountUser);
		} catch (DataIntegrityViolationException e) {
			throw new DataIntegrityViolationException(e.getLocalizedMessage());
		}
	}

	@Override
	public OperationalResponse reInviteAccountUser(List<String> ids, String accountId) {
		List<AccountUser> accountUsers = accountUserRepository.findByIdIn(ids);
		Account account = retrieveAccountById(accountId);
		AccountUser accountOwner = retrieveAccountUserById(account.getAccountOwner());

		for (AccountUser accountUser : accountUsers) {
			if (!accountId.equals(accountUser.getAccountId()))
				throw new GeneralPlatformServiceException(GeneralConstants.MODIFY_NOT_ALLOWED_MESSAGE);

			if (StringUtils.isNotBlank(accountUser.getPassword()))
				throw new GeneralPlatformDomainRuleException(
						"One of the users already accepted the invite sent before");
		}

		try {
			for (AccountUser accountUser : accountUsers) {
				GoMailerRequest goMailerRequest = new GoMailerRequest();
				GoMailerRequest.MailData mailData = new GoMailerRequest.MailData();
				mailData.setCompany(accountOwner.getName());
				mailData.setYear(LocalDateTime.now().getYear());
				String url = propertyConstants.getFrontendBaseUrl() + propertyConstants.getInviteUserUrl() + "?ixxd="
						+ accountUser.getId();
				mailData.setInvitationLink(url);
				mailData.setFirstName(accountUser.getFirstName());
				mailData.setSupportEmail(propertyConstants.getTechEveryWhereSupportEmail());
				goMailerRequest.setRecipient_email(accountUser.getEmail());
				goMailerRequest.setData(mailData);
				middlewareService.sendInvite(goMailerRequest);
			}

			return OperationalResponse.instance(GeneralConstants.SUCCESS_MESSAGE);
		} catch (DataIntegrityViolationException e) {
			throw new DataIntegrityViolationException(e.getLocalizedMessage());
		}
	}

	@Override
	public OperationalResponse activateAccountUser(String id, String accountId) {
		AccountUser existingAccountUser = retrieveAccountUserById(id);
		if (!accountId.equals(existingAccountUser.getAccountId()))
			throw new GeneralPlatformServiceException(GeneralConstants.MODIFY_NOT_ALLOWED_MESSAGE);

		if (existingAccountUser.getIsActive())
			throw new GeneralPlatformDomainRuleException("This user is already active");

		existingAccountUser.setIsActive(true);

		try {
			accountUserRepository.save(existingAccountUser);

			return OperationalResponse.instance(GeneralConstants.SUCCESS_MESSAGE);
		} catch (DataIntegrityViolationException e) {
			throw new DataIntegrityViolationException(e.getLocalizedMessage());
		}
	}

	@Override
	public OperationalResponse deactivateAccountUser(String id, String accountId) {
		AccountUser existingAccountUser = retrieveAccountUserById(id);
		if (!accountId.equals(existingAccountUser.getAccountId()))
			throw new GeneralPlatformServiceException(GeneralConstants.MODIFY_NOT_ALLOWED_MESSAGE);

		if (!existingAccountUser.getIsActive())
			throw new GeneralPlatformDomainRuleException("This user is already inactive");

		existingAccountUser.setIsActive(false);

		try {
			accountUserRepository.save(existingAccountUser);

			return OperationalResponse.instance(GeneralConstants.SUCCESS_MESSAGE);
		} catch (DataIntegrityViolationException e) {
			throw new DataIntegrityViolationException(e.getLocalizedMessage());
		}
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

	private Account retrieveAccountById(String id) {
		return accountRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Account Not Found"));
	}
}
