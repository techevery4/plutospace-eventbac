/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.domain.data.request;

public record CreateAdminUserRequest(String firstName, String lastName, String email, String password,
		String confirmPassword, String role) {
}
