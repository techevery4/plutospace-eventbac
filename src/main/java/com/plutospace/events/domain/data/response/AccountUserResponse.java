/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.domain.data.response;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor(staticName = "instance")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AccountUserResponse {

	private String id;
	private String accountId;

	private String firstName;
	private String lastName;
	private String name; // this is stored for business type

	private String email;

	private String imageId;
	private String imageUrl;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime createdOn;
}
