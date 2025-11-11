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
}
