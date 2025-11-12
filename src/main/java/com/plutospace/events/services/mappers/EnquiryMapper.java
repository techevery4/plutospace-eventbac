/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.services.mappers;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import com.plutospace.events.commons.data.CustomPageResponse;
import com.plutospace.events.domain.data.request.CreateEnquiryRequest;
import com.plutospace.events.domain.data.response.AdminUserResponse;
import com.plutospace.events.domain.data.response.EnquiryResponse;
import com.plutospace.events.domain.entities.Enquiry;

@Component
public class EnquiryMapper {

	public EnquiryResponse toResponse(Enquiry enquiry, AdminUserResponse treatedByUser) {
		return EnquiryResponse.instance(enquiry.getId(), enquiry.getName(), enquiry.getEmail(), enquiry.getSubject(),
				enquiry.getMessage(), enquiry.getIsTreated(), enquiry.getTreatedBy(), treatedByUser,
				enquiry.getCreatedOn());
	}

	public Enquiry toEntity(CreateEnquiryRequest createEnquiryRequest) {
		return Enquiry.instance(createEnquiryRequest.name(), createEnquiryRequest.email(),
				createEnquiryRequest.subject(), createEnquiryRequest.message(), false, null);
	}

	public CustomPageResponse<EnquiryResponse> toPagedResponse(Page<Enquiry> enquiries,
			Map<String, AdminUserResponse> adminUserResponseMap) {
		List<EnquiryResponse> enquiryResponses = enquiries.getContent().stream().map(enquiry -> {
			AdminUserResponse adminUserResponse = adminUserResponseMap.get(enquiry.getTreatedBy());
			return toResponse(enquiry, adminUserResponse);
		}).toList();
		long totalElements = enquiries.getTotalElements();
		Pageable pageable = enquiries.getPageable();
		return CustomPageResponse.resolvePageResponse(enquiryResponses, totalElements, pageable);
	}
}
