/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.services.implementations;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.plutospace.events.commons.definitions.GeneralConstants;
import com.plutospace.events.commons.definitions.PropertyConstants;
import com.plutospace.events.commons.exception.GeneralPlatformDomainRuleException;
import com.plutospace.events.commons.exception.LoginDisputeException;
import com.plutospace.events.commons.exception.ResourceAlreadyExistsException;
import com.plutospace.events.commons.exception.ResourceNotFoundException;
import com.plutospace.events.commons.utils.HashPassword;
import com.plutospace.events.commons.utils.SecurityMapper;
import com.plutospace.events.domain.data.AdminUserStatus;
import com.plutospace.events.domain.data.request.ChangeAdminUserPasswordRequest;
import com.plutospace.events.domain.data.request.CreateAdminUserRequest;
import com.plutospace.events.domain.data.request.LoginAdminUserRequest;
import com.plutospace.events.domain.data.response.AdminUserResponse;
import com.plutospace.events.domain.data.response.OperationalResponse;
import com.plutospace.events.domain.entities.AdminUser;
import com.plutospace.events.domain.repositories.AdminUserRepository;
import com.plutospace.events.services.AdminUserService;
import com.plutospace.events.services.mappers.AdminUserMapper;
import com.plutospace.events.validation.AdminUserValidator;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminUserServiceImpl implements AdminUserService {

	private final AdminUserRepository adminUserRepository;
	private final AdminUserValidator adminUserValidator;
	private final AdminUserMapper adminUserMapper;
	private final HashPassword hashPassword;
	private final HttpServletResponse headers;
	private final PropertyConstants propertyConstants;
	private final SecurityMapper securityMapper;

	@Override
	public AdminUserResponse createAdminUser(CreateAdminUserRequest createAdminUserRequest)
			throws NoSuchAlgorithmException, InvalidKeySpecException {
		adminUserValidator.validate(createAdminUserRequest);

		if (adminUserRepository.existsByEmailIgnoreCase(createAdminUserRequest.email()))
			throw new ResourceAlreadyExistsException("User already exists");

		AdminUser adminUser = adminUserMapper.toEntity(createAdminUserRequest);
		adminUser.setPassword(hashPassword.hashPass(adminUser.getPassword()));

		try {
			AdminUser savedAdminUser = adminUserRepository.save(adminUser);

			return adminUserMapper.toResponse(savedAdminUser);
		} catch (DataIntegrityViolationException e) {
			throw new DataIntegrityViolationException(e.getLocalizedMessage());
		}
	}

	@Override
	public OperationalResponse changeAdminUserPassword(ChangeAdminUserPasswordRequest changeAdminUserPasswordRequest,
			String accountId) throws NoSuchAlgorithmException, InvalidKeySpecException {
		adminUserValidator.validate(changeAdminUserPasswordRequest);
		AdminUser adminUser = retrieveAdminUserById(accountId);

		if (!hashPassword.validatePass(changeAdminUserPasswordRequest.oldPassword(), adminUser.getPassword()))
			throw new GeneralPlatformDomainRuleException("Old Password is incorrect");

		adminUser.setPassword(hashPassword.hashPass(changeAdminUserPasswordRequest.newPassword()));
		adminUserRepository.save(adminUser);

		return OperationalResponse.instance(GeneralConstants.SUCCESS_MESSAGE);
	}

	@Override
	public AdminUserResponse login(LoginAdminUserRequest loginAdminUserRequest)
			throws NoSuchAlgorithmException, InvalidKeySpecException {
		AdminUser adminUser = retrieveAdminUserByEmail(loginAdminUserRequest.email());

		if (adminUser.getIsPendingUser())
			throw new LoginDisputeException("Invalid User Type. Kindly accept the invite before proceeding to login");
		if (adminUser.getStatus().equals(AdminUserStatus.INACTIVE))
			throw new LoginDisputeException("User has been deactivated. Please meet your administrator");
		if (!hashPassword.validatePass(loginAdminUserRequest.password(), adminUser.getPassword()))
			throw new LoginDisputeException("Username or Password Incorrect");

		// Saving login time
		adminUser.setLastLogin(LocalDateTime.now());
		adminUserRepository.save(adminUser);

		headers.setHeader(GeneralConstants.TOKEN_KEY, securityMapper.generateEncryptedLoginTokenForAdmin(
				adminUser.getId(), propertyConstants.getEventsLoginEncryptionSecretKey()));

		return adminUserMapper.toResponse(adminUser);
	}

	@Override
	public List<AdminUserResponse> retrieveAdminUser(List<String> ids) {
		List<AdminUser> adminUsers = adminUserRepository.findByIdIn(ids);
		if (adminUsers.isEmpty())
			return new ArrayList<>();

		return adminUsers.stream().map(adminUserMapper::toResponse).toList();
	}

	private AdminUser retrieveAdminUserByEmail(String email) {
		return adminUserRepository.findByEmailIgnoreCase(email)
				.orElseThrow(() -> new ResourceNotFoundException("Admin User Not Found"));
	}

	private AdminUser retrieveAdminUserById(String id) {
		return adminUserRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Admin User Not Found"));
	}
}
