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
public class AccountResponse {

	private String id;
	private String planId;
	private PlanResponse planResponse;
	private String accountOwner; // id of the customer that created the account
	private AccountUserResponse accountOwnerResponse;
	private Long numberOfMembers;
	private Boolean isDefaulted;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime createdOn;
}
