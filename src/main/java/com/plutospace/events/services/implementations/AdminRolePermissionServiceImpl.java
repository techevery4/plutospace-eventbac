/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.services.implementations;

import java.util.ArrayList;
import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.plutospace.events.commons.definitions.GeneralConstants;
import com.plutospace.events.commons.exception.ResourceNotFoundException;
import com.plutospace.events.commons.exception.UnauthorizedAccessException;
import com.plutospace.events.domain.data.AdminRoleType;
import com.plutospace.events.domain.data.request.CheckAdminRolePermissionRequest;
import com.plutospace.events.domain.data.request.SaveAdminRolePermissionRequest;
import com.plutospace.events.domain.data.response.OperationalResponse;
import com.plutospace.events.domain.data.response.PermissionResponse;
import com.plutospace.events.domain.entities.AdminRolePermission;
import com.plutospace.events.domain.entities.Permission;
import com.plutospace.events.domain.repositories.AdminRolePermissionRepository;
import com.plutospace.events.domain.repositories.PermissionRepository;
import com.plutospace.events.services.AdminRolePermissionService;
import com.plutospace.events.services.mappers.PermissionMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminRolePermissionServiceImpl implements AdminRolePermissionService {

	private final AdminRolePermissionRepository adminRolePermissionRepository;
	private final PermissionRepository permissionRepository;
	private final PermissionMapper permissionMapper;

	@Override
	public OperationalResponse checkIfHasPermission(CheckAdminRolePermissionRequest checkAdminRolePermissionRequest) {
		AdminRoleType roleType = AdminRoleType.fromValue(checkAdminRolePermissionRequest.getRole());

		Permission existingPermission = permissionRepository.findByEndpointIgnoreCaseAndMethodIgnoreCase(
				checkAdminRolePermissionRequest.getEndpoint(), checkAdminRolePermissionRequest.getMethod());
		if (existingPermission == null || existingPermission.getIsGeneral())
			return OperationalResponse.instance(GeneralConstants.PERMISSION_ALLOW_MESSAGE);

		if (!adminRolePermissionRepository.existsByRoleAndPermissionId(roleType, existingPermission.getId()))
			throw new UnauthorizedAccessException("You are not authorized to access this resource");

		return OperationalResponse.instance(GeneralConstants.PERMISSION_ALLOW_MESSAGE);
	}

	@Override
	public List<PermissionResponse> retrievePermissionsForAdminRole(String role) {
		AdminRoleType roleType = AdminRoleType.fromValue(role);

		List<AdminRolePermission> rolePermissionList = adminRolePermissionRepository.findByRole(roleType);
		List<String> permissionIds = rolePermissionList.stream().map(AdminRolePermission::getPermissionId).toList();
		List<Permission> permissions = permissionRepository.findByIdIn(permissionIds);
		if (permissions.isEmpty())
			return new ArrayList<>();

		return permissions.stream().map(permissionMapper::toResponse).toList();
	}

	@Override
	public OperationalResponse assignPermissionsToRole(SaveAdminRolePermissionRequest saveAdminRolePermissionRequest) {
		AdminRoleType roleType = AdminRoleType.fromValue(saveAdminRolePermissionRequest.role());

		List<Permission> permissions = permissionRepository.findByIdIn(saveAdminRolePermissionRequest.permissionIds());
		if (permissions.isEmpty())
			throw new ResourceNotFoundException("Permissions Not Found");

		List<String> permissionIds = permissions.stream().map(Permission::getId).toList();

		List<AdminRolePermission> rolePermissionsList = new ArrayList<>();
		for (String permissionId : permissionIds) {
			rolePermissionsList.add(AdminRolePermission.instance(roleType, permissionId));
		}

		try {
			adminRolePermissionRepository.saveAll(rolePermissionsList);

			return OperationalResponse.instance(GeneralConstants.SUCCESS_MESSAGE);
		} catch (DataIntegrityViolationException e) {
			throw new DataIntegrityViolationException(e.getLocalizedMessage());
		}
	}

	@Override
	public OperationalResponse removePermissionFromRole(SaveAdminRolePermissionRequest saveAdminRolePermissionRequest) {
		AdminRoleType roleType = AdminRoleType.fromValue(saveAdminRolePermissionRequest.role());

		List<Permission> permissions = permissionRepository.findByIdIn(saveAdminRolePermissionRequest.permissionIds());
		if (permissions.isEmpty())
			throw new ResourceNotFoundException("Permissions Not Found");

		List<String> permissionIds = permissions.stream().map(Permission::getId).toList();

		List<AdminRolePermission> rolePermissionsList = adminRolePermissionRepository
				.findByRoleAndPermissionIdIn(roleType, permissionIds);

		try {
			adminRolePermissionRepository.deleteAll(rolePermissionsList);

			return OperationalResponse.instance(GeneralConstants.SUCCESS_MESSAGE);
		} catch (DataIntegrityViolationException e) {
			throw new DataIntegrityViolationException(e.getLocalizedMessage());
		}
	}
}
