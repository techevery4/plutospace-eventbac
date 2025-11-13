/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.services.mappers;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import com.plutospace.events.commons.data.CustomPageResponse;
import com.plutospace.events.domain.data.MeetingAcceptanceStatus;
import com.plutospace.events.domain.data.response.AccountUserResponse;
import com.plutospace.events.domain.data.response.MeetingInviteeResponse;
import com.plutospace.events.domain.entities.MeetingInvitee;

@Component
public class MeetingInviteeMapper {

	public MeetingInviteeResponse toResponse(MeetingInvitee meetingInvitee, AccountUserResponse accountUserResponse) {
		return MeetingInviteeResponse.instance(meetingInvitee.getId(), meetingInvitee.getMeetingId(),
				meetingInvitee.getEmail(), accountUserResponse, meetingInvitee.getMeetingAcceptanceStatus(),
				meetingInvitee.getLastStatusTime(), meetingInvitee.getCreatedOn());
	}

	public MeetingInvitee toEntity(String email, String meetingId) {
		return MeetingInvitee.instance(meetingId, email, MeetingAcceptanceStatus.PENDING, null);
	}

	public CustomPageResponse<MeetingInviteeResponse> toPagedResponse(Page<MeetingInvitee> meetingInvitees,
			Map<String, AccountUserResponse> accountUserResponseMap) {
		List<MeetingInviteeResponse> meetingInviteeResponses = meetingInvitees.getContent().stream()
				.map(meetingInvitee -> {
					AccountUserResponse accountUserResponse = accountUserResponseMap.get(meetingInvitee.getEmail());
					return toResponse(meetingInvitee, accountUserResponse);
				}).toList();
		long totalElements = meetingInvitees.getTotalElements();
		Pageable pageable = meetingInvitees.getPageable();
		return CustomPageResponse.resolvePageResponse(meetingInviteeResponses, totalElements, pageable);
	}
}
