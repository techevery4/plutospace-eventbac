/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.domain.data.request;

public record ChangeAdminUserPasswordRequest(String oldPassword, String newPassword, String confirmPassword) {
}
