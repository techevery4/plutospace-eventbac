/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.services;

import com.plutospace.events.commons.data.CustomPageResponse;
import com.plutospace.events.domain.data.request.CreateEnquiryRequest;
import com.plutospace.events.domain.data.response.EnquiryResponse;
import com.plutospace.events.domain.data.response.OperationalResponse;

public interface EnquiryService {

	EnquiryResponse createNewEnquiry(CreateEnquiryRequest createEnquiryRequest);

	CustomPageResponse<EnquiryResponse> retrievePendingEnquiries(int pageNo, int pageSize);

	CustomPageResponse<EnquiryResponse> retrieveEnquiries(int pageNo, int pageSize);

	OperationalResponse markAsTreated(String id, String userId);

	OperationalResponse markAsNotTreated(String id, String userId);
}
