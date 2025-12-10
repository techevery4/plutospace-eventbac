/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.services.mappers;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import com.plutospace.events.commons.data.CustomPageResponse;
import com.plutospace.events.domain.data.MeetingType;
import com.plutospace.events.domain.data.request.CreateMeetingRequest;
import com.plutospace.events.domain.data.response.MeetingResponse;
import com.plutospace.events.domain.entities.Meeting;

@Component
public class MeetingMapper {

	public MeetingResponse toResponse(Meeting meeting) {
		return MeetingResponse.instance(meeting.getId(), meeting.getTitle(), meeting.getAccountId(), meeting.getType(),
				meeting.getDescription(), meeting.getStartTime(), meeting.getEndTime(), meeting.getTimezone(),
				meeting.getMaximumParticipants(), meeting.getPublicId(), meeting.getMuteParticipantsOnEntry(),
				meeting.getEnableWaitingRoom(), meeting.getCreatedOn());
	}

	public Meeting toEntity(CreateMeetingRequest createMeetingRequest, LocalDateTime startTime, LocalDateTime endTime) {
		MeetingType type = MeetingType.fromValue(createMeetingRequest.getType());
		Meeting.Timezone timezone = new Meeting.Timezone();
		timezone.setRepresentation(createMeetingRequest.getTimezoneString());
		timezone.setValue(createMeetingRequest.getTimezoneValue());

		return Meeting.instance(createMeetingRequest.getTitle(), null, type, createMeetingRequest.getDescription(),
				startTime, endTime, timezone, createMeetingRequest.getMaximumParticipants(), null,
				createMeetingRequest.getMuteParticipantsOnEntry(), createMeetingRequest.getEnableWaitingRoom());
	}

	public CustomPageResponse<MeetingResponse> toPagedResponse(Page<Meeting> meetings) {
		List<MeetingResponse> meetingResponses = meetings.getContent().stream().map(this::toResponse).toList();
		long totalElements = meetings.getTotalElements();
		Pageable pageable = meetings.getPageable();
		return CustomPageResponse.resolvePageResponse(meetingResponses, totalElements, pageable);
	}
}
