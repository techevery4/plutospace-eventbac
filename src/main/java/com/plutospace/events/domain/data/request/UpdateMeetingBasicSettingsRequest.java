/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.domain.data.request;

public record UpdateMeetingBasicSettingsRequest(Integer maximumParticipants, Boolean muteParticipantsOnEntry,
		Boolean enableWaitingRoom) {
}
