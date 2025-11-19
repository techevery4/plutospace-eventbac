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
public class PermissionResponse {

	private String id;
	private String name;
	private String module;
	private String description;
	private String endpoint;
	private String method;
	private Boolean tiedToPlan; // This is an endpoint that will trigger plan check
	private Boolean isGeneral; // This is a general endpoint and must be available to all roles

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime createdOn;
}
