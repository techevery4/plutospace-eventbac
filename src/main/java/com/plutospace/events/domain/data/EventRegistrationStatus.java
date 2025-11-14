/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.domain.data;

import com.plutospace.events.commons.exception.GeneralPlatformDomainRuleException;

public enum EventRegistrationStatus {
	// PENDING - User just registered for the event
	// APPROVED - Owner of event approved the registration, in the case the event
	// requires approval
	// DECLINED - Owner of event declined the registration, in the case the event
	// requires approval
	// DENIED_ENTRY - User has come for the event but upon verification, was denied
	// entry
	// SIGNED_IN - User was granted access on the day of the event and signed in
	// SIGNED_OUT - Event has ended and user signed out
	PENDING, APPROVED, DECLINED, DENIED_ENTRY, SIGNED_IN, SIGNED_OUT;

	public static EventRegistrationStatus fromValue(String value) {
		for (EventRegistrationStatus eventRegistrationStatus : EventRegistrationStatus.values()) {
			if (eventRegistrationStatus.toString().equalsIgnoreCase(value)) {
				return eventRegistrationStatus;
			}
		}
		throw new GeneralPlatformDomainRuleException("Invalid event registration status: " + value);
	}
}
