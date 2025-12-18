/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.services;

import com.plutospace.events.commons.data.CustomPageResponse;
import com.plutospace.events.domain.data.request.SaveSuspiciousActivityRequest;
import com.plutospace.events.domain.data.response.OperationalResponse;
import com.plutospace.events.domain.data.response.SuspiciousActivityResponse;
import com.plutospace.events.domain.data.response.SuspiciousUserResponse;

public interface SuspiciousUserService {

	OperationalResponse saveSuspiciousThread(SaveSuspiciousActivityRequest saveSuspiciousActivityRequest,
			String accountUserId);

	OperationalResponse checkIfUserIsBlocked(String userAgent);

	CustomPageResponse<SuspiciousActivityResponse> retrieveSuspiciousActivities(int pageNo, int pageSize);

	CustomPageResponse<SuspiciousUserResponse> retrieveSuspiciousUsers(int pageNo, int pageSize);
}
