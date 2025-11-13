/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.services;

import com.plutospace.events.commons.data.CustomPageResponse;
import com.plutospace.events.domain.data.request.CreateMeetingInviteRequest;
import com.plutospace.events.domain.data.response.MeetingInviteeResponse;
import com.plutospace.events.domain.data.response.OperationalResponse;

public interface MeetingInviteeService {

	OperationalResponse createMeetingInvite(CreateMeetingInviteRequest createMeetingInviteRequest);

	CustomPageResponse<MeetingInviteeResponse> retrieveMeetingInvitees(String meetingId, int pageNo, int pageSize);

	OperationalResponse checkIfAlreadyInvited(String meetingId, String email);

	OperationalResponse changeInviteeStatus(String meetingId, String email, String status);
}
