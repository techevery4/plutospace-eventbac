/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.domain.data.response;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor(staticName = "instance")
public class SuspiciousActivityResponse {

	private String id;
	private String accountId;
	private String createdBy;
	private AccountUserResponse accountUserResponse;
	private String userAgent;
	private String actionPerformed;
	private String endpoint;
	private String method;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime createdOn;
}
