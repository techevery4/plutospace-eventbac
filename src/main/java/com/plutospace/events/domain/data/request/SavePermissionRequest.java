/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.domain.data.request;

public record SavePermissionRequest(String name, String description, String module, String endpoint, String method,
		String planFeature, Boolean isGeneral) {
}
