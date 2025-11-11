/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.domain.data.request;

public record CompleteAdminUserInviteRequest(String firstName, String lastName, String password,
		String confirmPassword) {
}
