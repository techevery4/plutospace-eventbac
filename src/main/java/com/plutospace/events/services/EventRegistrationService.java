/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.services;

import java.util.List;

import com.plutospace.events.commons.data.CustomPageResponse;
import com.plutospace.events.domain.data.request.CreateEventRegistrationRequest;
import com.plutospace.events.domain.data.response.EventRegistrationLogResponse;
import com.plutospace.events.domain.data.response.EventRegistrationResponse;
import com.plutospace.events.domain.data.response.OperationalResponse;

public interface EventRegistrationService {

	OperationalResponse registerForAnEvent(CreateEventRegistrationRequest createEventRegistrationRequest);

	CustomPageResponse<EventRegistrationResponse> retrieveEventRegistrations(String eventId, int pageNo, int pageSize);

	OperationalResponse approveRegistration(String id);

	OperationalResponse declineRegistration(String id, String reason);

	OperationalResponse signInAttendee(String id);

	OperationalResponse denyAttendeeEntry(String id, String reason);

	OperationalResponse signOutAttendee(String id);

	List<EventRegistrationLogResponse> viewLogsAroundRegistration(String id);

	CustomPageResponse<EventRegistrationResponse> searchEventRegistration(String eventId, String text);
}
