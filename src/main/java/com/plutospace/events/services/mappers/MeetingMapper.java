/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.services.mappers;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import com.plutospace.events.commons.data.CustomPageResponse;
import com.plutospace.events.domain.data.request.CreateMeetingRequest;
import com.plutospace.events.domain.data.response.MeetingResponse;
import com.plutospace.events.domain.entities.Meeting;

@Component
public class MeetingMapper {

	public MeetingResponse toResponse(Meeting meeting) {
		return MeetingResponse.instance(meeting.getId(), meeting.getTitle(), meeting.getAccountId(),
				meeting.getDescription(), meeting.getStartDate(), meeting.getEndDate(), meeting.getStartTime(),
				meeting.getEndTime(), meeting.getTimezone(), meeting.getIsRecurring(),
				meeting.getRecurringDaysOfTheWeek(), meeting.getMaximumParticipants(), meeting.getPublicId(),
				meeting.getCreatedOn());
	}

	public Meeting toEntity(CreateMeetingRequest createMeetingRequest) {
		Meeting.Timezone timezone = new Meeting.Timezone();
		timezone.setRepresentation(createMeetingRequest.getTimezoneString());
		timezone.setValue(createMeetingRequest.getTimezoneValue());

		return Meeting.instance(createMeetingRequest.getTitle(), createMeetingRequest.getAccountId(),
				createMeetingRequest.getDescription(), createMeetingRequest.getStartDate(),
				createMeetingRequest.getEndDate(), createMeetingRequest.getStartTime(),
				createMeetingRequest.getEndTime(), timezone, createMeetingRequest.getIsRecurring(),
				createMeetingRequest.getRecurringDaysOfTheWeek(), createMeetingRequest.getMaximumParticipants(), null);
	}

	public CustomPageResponse<MeetingResponse> toPagedResponse(Page<Meeting> meetings) {
		List<MeetingResponse> meetingResponses = meetings.getContent().stream().map(this::toResponse).toList();
		long totalElements = meetings.getTotalElements();
		Pageable pageable = meetings.getPageable();
		return CustomPageResponse.resolvePageResponse(meetingResponses, totalElements, pageable);
	}
}
