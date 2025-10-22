/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.commons.definitions;

public class ApiResourceConstants {

	private ApiResourceConstants() {
	}

	public static final String API_VERSION = "/api/v1";
	public static final String RESOURCE_ID = "/{id}";
	public static final String PLANS = API_VERSION + "/plans";
	public static final String PLANS_RESOURCE_ID = PLANS + RESOURCE_ID;
}
