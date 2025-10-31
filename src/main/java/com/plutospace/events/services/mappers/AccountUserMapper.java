/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.services.mappers;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import com.plutospace.events.commons.data.CustomPageResponse;
import com.plutospace.events.domain.data.request.RegisterBusinessAccountRequest;
import com.plutospace.events.domain.data.request.RegisterPersonalAccountRequest;
import com.plutospace.events.domain.data.response.AccountUserResponse;
import com.plutospace.events.domain.entities.AccountUser;

@Component
public class AccountUserMapper {

	public AccountUserResponse toResponse(AccountUser accountUser) {
		return AccountUserResponse.instance(accountUser.getId(), accountUser.getAccountId(), accountUser.getFirstName(),
				accountUser.getLastName(), accountUser.getName(), accountUser.getEmail(), accountUser.getImageId(),
				accountUser.getImageUrl(), accountUser.getCreatedOn(), accountUser.getLastLogin());
	}

	public AccountUser toEntity(RegisterPersonalAccountRequest registerPersonalAccountRequest) {
		return AccountUser.instance(null, registerPersonalAccountRequest.firstName(),
				registerPersonalAccountRequest.lastName(), null, registerPersonalAccountRequest.email(), null, null,
				null, null);
	}

	public AccountUser toEntity(RegisterBusinessAccountRequest registerBusinessAccountRequest) {
		return AccountUser.instance(null, null, null, registerBusinessAccountRequest.name(),
				registerBusinessAccountRequest.email(), null, null, null, null);
	}

	public CustomPageResponse<AccountUserResponse> toPagedResponse(Page<AccountUser> accountUsers) {
		List<AccountUserResponse> accountUserResponses = accountUsers.getContent().stream().map(this::toResponse)
				.toList();
		long totalElements = accountUsers.getTotalElements();
		Pageable pageable = accountUsers.getPageable();
		return CustomPageResponse.resolvePageResponse(accountUserResponses, totalElements, pageable);
	}
}
