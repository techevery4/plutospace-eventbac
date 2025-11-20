/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.services;

import java.util.List;

import com.plutospace.events.commons.data.CustomPageResponse;
import com.plutospace.events.domain.data.request.BookFreeSlotRequest;
import com.plutospace.events.domain.data.request.SaveFreeSlotRequest;
import com.plutospace.events.domain.data.response.FreeSlotResponse;
import com.plutospace.events.domain.data.response.OperationalResponse;

public interface FreeSlotService {

	FreeSlotResponse createFreeSlot(SaveFreeSlotRequest saveFreeSlotRequest, String accountId);

	FreeSlotResponse updateFreeSlot(String id, SaveFreeSlotRequest saveFreeSlotRequest);

	String generateAvailableSlotLink(String accountId, String accountUserId);

	CustomPageResponse<FreeSlotResponse> retrieveMyFreeSlots(String accountId, String accountUserId, int pageNo,
			int pageSize);

	List<FreeSlotResponse> retrieveMyAvailableSlots(String slotLink, Long startTime, Long endTime);

	OperationalResponse deleteFreeSlot(String id);

	OperationalResponse bookFreeSlot(BookFreeSlotRequest bookFreeSlotRequest);
}
