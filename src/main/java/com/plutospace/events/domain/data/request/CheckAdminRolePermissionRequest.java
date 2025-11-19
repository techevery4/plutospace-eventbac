/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.domain.data.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CheckAdminRolePermissionRequest {

	private String role;
	private String endpoint;
	private String method;
}
