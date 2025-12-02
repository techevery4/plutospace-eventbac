/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.services.mappers;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import com.plutospace.events.commons.data.CustomPageResponse;
import com.plutospace.events.domain.data.PollType;
import com.plutospace.events.domain.data.request.SavePollRequest;
import com.plutospace.events.domain.data.response.PollResponse;
import com.plutospace.events.domain.entities.Poll;

@Component
public class PollMapper {

	public PollResponse toResponse(Poll poll) {
		return PollResponse.instance(poll.getId(), poll.getAccountId(), poll.getTitle(), poll.getType(),
				poll.getPublicId(), poll.getClosedEnded(), poll.getIsPublished(), poll.getBodies(),
				poll.getCreatedOn());
	}

	public Poll toEntity(SavePollRequest savePollRequest, String accountId) {
		PollType pollType = PollType.fromValue(savePollRequest.getType());
		return Poll.instance(accountId, savePollRequest.getTitle(), pollType, null,
				!StringUtils.isBlank(savePollRequest.getEventId()), false, savePollRequest.getBodies());
	}

	public CustomPageResponse<PollResponse> toPagedResponse(Page<Poll> polls) {
		List<PollResponse> pollResponses = polls.getContent().stream().map(this::toResponse).toList();
		long totalElements = polls.getTotalElements();
		Pageable pageable = polls.getPageable();
		return CustomPageResponse.resolvePageResponse(pollResponses, totalElements, pageable);
	}
}
