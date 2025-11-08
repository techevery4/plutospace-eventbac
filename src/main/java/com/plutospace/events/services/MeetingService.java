/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.services;

import java.util.List;

import com.plutospace.events.domain.data.request.CreateMeetingRequest;
import com.plutospace.events.domain.data.response.MeetingResponse;

public interface MeetingService {

	MeetingResponse createMeeting(CreateMeetingRequest createMeetingRequest);

	List<MeetingResponse> retrieveMeeting(List<String> ids);

	MeetingResponse retrieveMeetingByPublicId(String publicId);
}
