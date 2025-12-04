/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.services;

import java.util.List;

import com.plutospace.events.commons.data.CustomPageResponse;
import com.plutospace.events.domain.data.request.CreatePollResultRequest;
import com.plutospace.events.domain.data.request.SavePollRequest;
import com.plutospace.events.domain.data.response.OperationalResponse;
import com.plutospace.events.domain.data.response.PollResponse;
import com.plutospace.events.domain.data.response.PollResultResponse;

public interface PollService {

	PollResponse createPoll(SavePollRequest savePollRequest, String accountId);

	PollResponse updatePoll(String id, SavePollRequest savePollRequest);

	CustomPageResponse<PollResponse> retrievePolls(String accountId, int pageNo, int pageSize);

	List<PollResponse> retrievePoll(List<String> ids);

	PollResponse retrievePublishedPollByPublicId(String publicId);

	OperationalResponse publishPoll(String id);

	OperationalResponse unpublishPoll(String id);

	OperationalResponse createPollResult(String publicId, CreatePollResultRequest createPollResultRequest);

	OperationalResponse checkIfUserAlreadySubmittedResponse(String publicId, String email);

	OperationalResponse deleteResponse(String publicId, String email);

	CustomPageResponse<PollResultResponse> retrievePollResults(String pollId, int pageNo, int pageSize);
}
