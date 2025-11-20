/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.services.implementations;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.plutospace.events.commons.data.CustomPageResponse;
import com.plutospace.events.commons.definitions.GeneralConstants;
import com.plutospace.events.commons.exception.ResourceNotFoundException;
import com.plutospace.events.domain.data.request.CreateEnquiryRequest;
import com.plutospace.events.domain.data.response.AdminUserResponse;
import com.plutospace.events.domain.data.response.EnquiryResponse;
import com.plutospace.events.domain.data.response.OperationalResponse;
import com.plutospace.events.domain.entities.Enquiry;
import com.plutospace.events.domain.repositories.EnquiryRepository;
import com.plutospace.events.intelligence.search.DatabaseSearchService;
import com.plutospace.events.services.AdminUserService;
import com.plutospace.events.services.EnquiryService;
import com.plutospace.events.services.mappers.EnquiryMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class EnquiryServiceImpl implements EnquiryService {

	private final EnquiryRepository enquiryRepository;
	private final AdminUserService adminUserService;
	private final DatabaseSearchService databaseSearchService;
	private final EnquiryMapper enquiryMapper;

	@Override
	public EnquiryResponse createNewEnquiry(CreateEnquiryRequest createEnquiryRequest) {
		Enquiry enquiry = enquiryMapper.toEntity(createEnquiryRequest);

		try {
			Enquiry savedEnquiry = enquiryRepository.save(enquiry);

			return enquiryMapper.toResponse(savedEnquiry, null);
		} catch (DataIntegrityViolationException e) {
			throw new DataIntegrityViolationException(e.getLocalizedMessage());
		}
	}

	@Override
	public CustomPageResponse<EnquiryResponse> retrievePendingEnquiries(int pageNo, int pageSize) {
		Pageable pageable = PageRequest.of(pageNo, pageSize);

		Page<Enquiry> enquiries = enquiryRepository.findByIsTreatedNot(true, pageable);
		return enquiryMapper.toPagedResponse(enquiries, new HashMap<>());
	}

	@Override
	public CustomPageResponse<EnquiryResponse> retrieveEnquiries(int pageNo, int pageSize) {
		Pageable pageable = PageRequest.of(pageNo, pageSize);

		Page<Enquiry> enquiries = enquiryRepository.findAll(pageable);
		if (enquiries.getTotalElements() == 0)
			return new CustomPageResponse<>();

		List<String> adminUserIds = enquiries.getContent().stream().map(Enquiry::getTreatedBy).toList();
		List<AdminUserResponse> adminUserResponses = adminUserService.retrieveAdminUser(adminUserIds);
		Map<String, AdminUserResponse> adminUserResponseMap = new HashMap<>();
		for (AdminUserResponse adminUserResponse : adminUserResponses) {
			adminUserResponseMap.putIfAbsent(adminUserResponse.getId(), adminUserResponse);
		}

		return enquiryMapper.toPagedResponse(enquiries, adminUserResponseMap);
	}

	@Override
	public OperationalResponse markAsTreated(String id, String userId) {
		Enquiry existingEnquiry = retrieveEnquiryById(id);

		existingEnquiry.setTreatedBy(userId);
		existingEnquiry.setIsTreated(true);

		try {
			enquiryRepository.save(existingEnquiry);

			return OperationalResponse.instance(GeneralConstants.SUCCESS_MESSAGE);
		} catch (DataIntegrityViolationException e) {
			throw new DataIntegrityViolationException(e.getLocalizedMessage());
		}
	}

	@Override
	public OperationalResponse markAsNotTreated(String id, String userId) {
		Enquiry existingEnquiry = retrieveEnquiryById(id);

		existingEnquiry.setTreatedBy(userId);
		existingEnquiry.setIsTreated(false);

		try {
			enquiryRepository.save(existingEnquiry);

			return OperationalResponse.instance(GeneralConstants.SUCCESS_MESSAGE);
		} catch (DataIntegrityViolationException e) {
			throw new DataIntegrityViolationException(e.getLocalizedMessage());
		}
	}

	@Override
	public CustomPageResponse<EnquiryResponse> searchEnquiry(String text, int pageNo, int pageSize) {
		Pageable pageable = PageRequest.of(pageNo, pageSize);

		List<String> fields = List.of("subject", "message", "name", "email");
		Page<Enquiry> enquiries = databaseSearchService.findEnquiryByDynamicFilter(text, fields, pageable);
		if (enquiries.getTotalElements() == 0)
			return new CustomPageResponse<>();

		List<String> adminUserIds = enquiries.getContent().stream().map(Enquiry::getTreatedBy).toList();
		List<AdminUserResponse> adminUserResponses = adminUserService.retrieveAdminUser(adminUserIds);
		Map<String, AdminUserResponse> adminUserResponseMap = new HashMap<>();
		for (AdminUserResponse adminUserResponse : adminUserResponses) {
			adminUserResponseMap.putIfAbsent(adminUserResponse.getId(), adminUserResponse);
		}

		return enquiryMapper.toPagedResponse(enquiries, adminUserResponseMap);
	}

	private Enquiry retrieveEnquiryById(String id) {
		return enquiryRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Enquiry Not Found"));
	}
}
