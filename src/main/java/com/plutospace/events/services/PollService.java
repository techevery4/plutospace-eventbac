/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.services;

import java.util.List;

import com.plutospace.events.commons.data.CustomPageResponse;
import com.plutospace.events.domain.data.request.SavePollRequest;
import com.plutospace.events.domain.data.response.OperationalResponse;
import com.plutospace.events.domain.data.response.PollResponse;

public interface PollService {

	PollResponse createPoll(SavePollRequest savePollRequest, String accountId);

	PollResponse updatePoll(String id, SavePollRequest savePollRequest);

	CustomPageResponse<PollResponse> retrievePolls(String accountId, int pageNo, int pageSize);

	List<PollResponse> retrievePoll(List<String> ids);

	PollResponse retrievePublishedPollByPublicId(String publicId);

	OperationalResponse publishPoll(String id);

	OperationalResponse unpublishPoll(String id);
}
