/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.services.mappers;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import com.plutospace.events.commons.data.CustomPageResponse;
import com.plutospace.events.domain.data.request.SaveFreeSlotRequest;
import com.plutospace.events.domain.data.response.FreeSlotResponse;
import com.plutospace.events.domain.entities.FreeSlot;

@Component
public class FreeSlotMapper {

	public FreeSlotResponse toResponse(FreeSlot freeSlot) {
		return FreeSlotResponse.instance(freeSlot.getId(), freeSlot.getAccountId(), freeSlot.getTitle(),
				freeSlot.getDate(), freeSlot.getStartTime(), freeSlot.getEndTime(), freeSlot.getIsAvailable(),
				freeSlot.getTimezone(), freeSlot.getCreatedOn());
	}

	public FreeSlot toEntity(SaveFreeSlotRequest saveFreeSlotRequest, String accountId, LocalDateTime startTime,
			LocalDateTime endTime) {
		FreeSlot.Timezone timezone = new FreeSlot.Timezone();
		timezone.setRepresentation(saveFreeSlotRequest.timezoneString());
		timezone.setValue(saveFreeSlotRequest.timezoneValue());

		return FreeSlot.instance(accountId, saveFreeSlotRequest.title(), saveFreeSlotRequest.date(), startTime, endTime,
				true, timezone);
	}

	public CustomPageResponse<FreeSlotResponse> toPagedResponse(Page<FreeSlot> freeSlots) {
		List<FreeSlotResponse> freeSlotResponses = freeSlots.getContent().stream().map(this::toResponse).toList();
		long totalElements = freeSlots.getTotalElements();
		Pageable pageable = freeSlots.getPageable();
		return CustomPageResponse.resolvePageResponse(freeSlotResponses, totalElements, pageable);
	}
}
