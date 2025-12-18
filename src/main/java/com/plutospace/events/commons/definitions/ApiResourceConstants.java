/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.commons.definitions;

public class ApiResourceConstants {

	private ApiResourceConstants() {
	}

	public static final String API_VERSION = "/api/v1";
	public static final String RESOURCE_ID = "/{id}";
	public static final String PLANS = API_VERSION + "/plans";
	public static final String PLANS_RESOURCE_ID = PLANS + RESOURCE_ID;
	public static final String ACCOUNT_USERS = API_VERSION + "/account-users";
	public static final String ACCOUNT_USERS_RESOURCE_ID = ACCOUNT_USERS + RESOURCE_ID;
	public static final String EVENTS = API_VERSION + "/events";
	public static final String EVENTS_RESOURCE_ID = EVENTS + RESOURCE_ID;
	public static final String EVENT_CATEGORIES = API_VERSION + "/event-categories";
	public static final String EVENT_CATEGORIES_RESOURCE_ID = EVENT_CATEGORIES + RESOURCE_ID;
	public static final String MEETINGS = API_VERSION + "/meetings";
	public static final String MEETINGS_RESOURCE_ID = MEETINGS + RESOURCE_ID;
	public static final String ADMIN_USERS = API_VERSION + "/admin-users";
	public static final String ADMIN_USERS_RESOURCE_ID = ADMIN_USERS + RESOURCE_ID;
	public static final String CALENDARS = API_VERSION + "/calendars";
	public static final String ACCOUNT_SESSIONS = API_VERSION + "/account-sessions";
	public static final String ENQUIRIES = API_VERSION + "/enquiries";
	public static final String ENQUIRIES_RESOURCE_ID = ENQUIRIES + RESOURCE_ID;
	public static final String MEETING_INVITEES = API_VERSION + "/meeting-invitees";
	public static final String EVENT_REGISTRATIONS = API_VERSION + "/event-registrations";
	public static final String PERMISSIONS = API_VERSION + "/permissions";
	public static final String PERMISSIONS_RESOURCE_ID = PERMISSIONS + RESOURCE_ID;
	public static final String ADMIN_PERMISSIONS = API_VERSION + "/admin-permissions";
	public static final String FREE_SLOTS = API_VERSION + "/free-slots";
	public static final String FREE_SLOTS_RESOURCE_ID = FREE_SLOTS + RESOURCE_ID;
	public static final String PROMO_CODES = API_VERSION + "/promo-codes";
	public static final String PROMO_CODES_RESOURCE_ID = PROMO_CODES + RESOURCE_ID;
	public static final String POLLS = API_VERSION + "/polls";
	public static final String POLLS_RESOURCE_ID = POLLS + RESOURCE_ID;
	public static final String PROPOSALS = API_VERSION + "/proposals";
	public static final String PROPOSALS_RESOURCE_ID = PROPOSALS + RESOURCE_ID;
	public static final String MEDIA = API_VERSION + "/media";
	public static final String MEDIA_RESOURCE_ID = MEDIA + RESOURCE_ID;
	public static final String PLAN_PAYMENT_HISTORIES = API_VERSION + "/plan-payment-histories";
}
