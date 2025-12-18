/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.services;

import java.util.List;

import com.plutospace.events.commons.data.CustomPageResponse;
import com.plutospace.events.domain.data.request.*;
import com.plutospace.events.domain.data.response.MeetingResponse;
import com.plutospace.events.domain.data.response.OperationalResponse;

public interface MeetingService {

	MeetingResponse createMeeting(CreateMeetingRequest createMeetingRequest, String accountId);

	List<MeetingResponse> retrieveMeeting(List<String> ids);

	MeetingResponse retrieveMeetingByPublicId(String publicId);

	CustomPageResponse<MeetingResponse> retrieveMeetingsBetween(String accountId, Long startTime, Long endTime,
			int pageNo, int pageSize);

	List<MeetingResponse> retrieveUpcomingMeetingsBetween(String accountId, String accountUserId, Long startTime,
			Long endTime);

	MeetingResponse retrieveMeeting(String id);

	CustomPageResponse<MeetingResponse> searchMeeting(String accountId, String accountUserId, String text, int pageNo,
			int pageSize);

	OperationalResponse startRecordingMeeting(String accountUserId, String publicId);

	MeetingResponse updateMeeting(String id, String accountId, UpdateMeetingRequest updateMeetingRequest);

	MeetingResponse updateMeetingBasicSetting(String id, String accountId,
			UpdateMeetingBasicSettingsRequest updateMeetingBasicSettingsRequest);

	MeetingResponse updateMeetingTime(String id, String accountId, UpdateMeetingTimeRequest updateMeetingTimeRequest);

	MeetingResponse updateRecurringMeetingTime(String id, String accountId,
			UpdateRecurringMeetingTimeRequest updateRecurringMeetingTimeRequest);
}
