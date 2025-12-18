/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.domain.data.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SaveSuspiciousActivityRequest {

	private String accountId;
	private String userAgent;

	private String actionPerformed;
	private String endpoint;
	private String method;
}
