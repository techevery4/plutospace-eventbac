/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.services.implementations;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.plutospace.events.commons.data.CustomPageResponse;
import com.plutospace.events.commons.definitions.GeneralConstants;
import com.plutospace.events.commons.exception.ResourceAlreadyExistsException;
import com.plutospace.events.commons.exception.ResourceNotFoundException;
import com.plutospace.events.domain.data.request.SavePermissionRequest;
import com.plutospace.events.domain.data.response.OperationalResponse;
import com.plutospace.events.domain.data.response.PermissionResponse;
import com.plutospace.events.domain.entities.Permission;
import com.plutospace.events.domain.repositories.PermissionRepository;
import com.plutospace.events.services.PermissionService;
import com.plutospace.events.services.mappers.PermissionMapper;
import com.plutospace.events.validation.PermissionValidator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class PermissionServiceImpl implements PermissionService {

	private final PermissionRepository permissionRepository;
	private final PermissionMapper permissionMapper;
	private final PermissionValidator permissionValidator;

	@Override
	public PermissionResponse createPermission(SavePermissionRequest savePermissionRequest) {
		permissionValidator.validate(savePermissionRequest);

		if (permissionRepository.existsByEndpointIgnoreCaseAndMethodIgnoreCase(savePermissionRequest.endpoint(),
				savePermissionRequest.method()))
			throw new ResourceAlreadyExistsException("Permission Already Exists");

		Permission permission = permissionMapper.toEntity(savePermissionRequest);

		try {
			Permission savedPermission = permissionRepository.save(permission);

			return permissionMapper.toResponse(savedPermission);
		} catch (DataIntegrityViolationException e) {
			throw new DataIntegrityViolationException(e.getLocalizedMessage());
		}
	}

	@Override
	public OperationalResponse createBulkPermissions(List<SavePermissionRequest> savePermissionRequests) {
		List<Permission> permissions = new ArrayList<>();

		for (SavePermissionRequest savePermissionRequest : savePermissionRequests) {
			permissionValidator.validate(savePermissionRequest);

			if (permissionRepository.existsByEndpointIgnoreCaseAndMethodIgnoreCase(savePermissionRequest.endpoint(),
					savePermissionRequest.method()))
				throw new ResourceAlreadyExistsException("Permission Already Exists");

			Permission permission = permissionMapper.toEntity(savePermissionRequest);
			permissions.add(permission);
		}

		try {
			permissionRepository.saveAll(permissions);

			return OperationalResponse.instance(GeneralConstants.SUCCESS_MESSAGE);
		} catch (DataIntegrityViolationException e) {
			throw new DataIntegrityViolationException(e.getLocalizedMessage());
		}
	}

	@Override
	public PermissionResponse updatePermission(String id, SavePermissionRequest savePermissionRequest) {
		Permission existingPermission = retrievePermissionById(id);

		if (StringUtils.isNotBlank(savePermissionRequest.endpoint())
				|| StringUtils.isNotBlank(savePermissionRequest.method())) {
			String endpoint = StringUtils.isNotBlank(savePermissionRequest.endpoint())
					? savePermissionRequest.endpoint()
					: existingPermission.getEndpoint();
			String method = StringUtils.isNotBlank(savePermissionRequest.method())
					? savePermissionRequest.method()
					: existingPermission.getMethod();

			Permission checkPermission = permissionRepository.findByEndpointIgnoreCaseAndMethodIgnoreCase(endpoint,
					method);
			if (checkPermission != null && !checkPermission.getId().equals(id))
				throw new ResourceAlreadyExistsException("Permission Already Exists");

			existingPermission.setEndpoint(endpoint);
			existingPermission.setMethod(method);
		}
		if (StringUtils.isNotBlank(savePermissionRequest.module()))
			existingPermission.setModule(savePermissionRequest.module());
		if (StringUtils.isNotBlank(savePermissionRequest.name()))
			existingPermission.setName(savePermissionRequest.name());
		if (StringUtils.isNotBlank(savePermissionRequest.description()))
			existingPermission.setDescription(savePermissionRequest.description());
		if (ObjectUtils.isNotEmpty(savePermissionRequest.isGeneral()))
			existingPermission.setIsGeneral(savePermissionRequest.isGeneral());
		if (ObjectUtils.isNotEmpty(savePermissionRequest.tiedToPlan()))
			existingPermission.setIsGeneral(savePermissionRequest.tiedToPlan());

		try {
			Permission savedPermission = permissionRepository.save(existingPermission);

			return permissionMapper.toResponse(savedPermission);
		} catch (DataIntegrityViolationException e) {
			throw new DataIntegrityViolationException(e.getLocalizedMessage());
		}
	}

	@Override
	public CustomPageResponse<PermissionResponse> retrievePermissions(int pageNo, int pageSize) {
		Pageable pageable = PageRequest.of(pageNo, pageSize);

		Page<Permission> permissions = permissionRepository.findAll(pageable);
		return permissionMapper.toPagedResponse(permissions);
	}

	@Override
	public List<PermissionResponse> retrievePermission(List<String> ids) {
		List<Permission> permissions = permissionRepository.findByIdIn(ids);

		return permissions.stream().map(permissionMapper::toResponse).toList();
	}

	private Permission retrievePermissionById(String id) {
		return permissionRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Permission Not Found"));
	}
}
