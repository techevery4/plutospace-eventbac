/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.domain.data.request;

public record RegisterBusinessAccountRequest(String name, String email, String password, String confirmPassword,
		String planId) {
}
