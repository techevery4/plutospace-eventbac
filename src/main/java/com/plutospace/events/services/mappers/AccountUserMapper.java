/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.services.mappers;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import com.plutospace.events.commons.data.CustomPageResponse;
import com.plutospace.events.domain.data.request.InviteAccountUserRequest;
import com.plutospace.events.domain.data.request.RegisterBusinessAccountRequest;
import com.plutospace.events.domain.data.request.RegisterPersonalAccountRequest;
import com.plutospace.events.domain.data.response.AccountResponse;
import com.plutospace.events.domain.data.response.AccountUserResponse;
import com.plutospace.events.domain.data.response.PlanResponse;
import com.plutospace.events.domain.entities.Account;
import com.plutospace.events.domain.entities.AccountUser;

@Component
public class AccountUserMapper {

	public AccountUserResponse toResponse(AccountUser accountUser) {
		return AccountUserResponse.instance(accountUser.getId(), accountUser.getAccountId(), accountUser.getFirstName(),
				accountUser.getLastName(), accountUser.getName(), accountUser.getEmail(), accountUser.getImageId(),
				accountUser.getImageUrl(), accountUser.getCreatedOn(), accountUser.getLastLogin(),
				accountUser.getIsActive());
	}

	public AccountResponse toResponse(Account account, AccountUserResponse accountUserResponse,
			PlanResponse planResponse) {
		return AccountResponse.instance(account.getId(), account.getPlanId(), planResponse, account.getAccountOwner(),
				accountUserResponse, account.getNumberOfMembers(), account.getIsDefaulted(), account.getPlanDueDate(),
				account.getCreatedOn());
	}

	public AccountUser toEntity(RegisterPersonalAccountRequest registerPersonalAccountRequest) {
		return AccountUser.instance(null, registerPersonalAccountRequest.firstName(),
				registerPersonalAccountRequest.lastName(), null, registerPersonalAccountRequest.email(), null, null,
				null, null, true);
	}

	public AccountUser toEntity(RegisterBusinessAccountRequest registerBusinessAccountRequest) {
		return AccountUser.instance(null, null, null, registerBusinessAccountRequest.name(),
				registerBusinessAccountRequest.email(), null, null, null, null, true);
	}

	public AccountUser toEntity(InviteAccountUserRequest inviteAccountUserRequest, String accountId) {
		return AccountUser.instance(accountId, inviteAccountUserRequest.firstName(),
				inviteAccountUserRequest.lastName(), null, inviteAccountUserRequest.email(), null, null, null, null,
				false);
	}

	public CustomPageResponse<AccountUserResponse> toPagedResponse(Page<AccountUser> accountUsers) {
		List<AccountUserResponse> accountUserResponses = accountUsers.getContent().stream().map(this::toResponse)
				.toList();
		long totalElements = accountUsers.getTotalElements();
		Pageable pageable = accountUsers.getPageable();
		return CustomPageResponse.resolvePageResponse(accountUserResponses, totalElements, pageable);
	}

	public CustomPageResponse<AccountResponse> toPagedResponse(Page<Account> accounts,
			Map<String, AccountUserResponse> accountUserResponseMap, Map<String, PlanResponse> planResponseMap) {
		List<AccountResponse> accountResponses = accounts.getContent().stream().map(account -> {
			PlanResponse planResponse = planResponseMap.get(account.getPlanId());
			AccountUserResponse accountUserResponse = accountUserResponseMap.get(account.getAccountOwner());
			return toResponse(account, accountUserResponse, planResponse);
		}).toList();
		long totalElements = accounts.getTotalElements();
		Pageable pageable = accounts.getPageable();
		return CustomPageResponse.resolvePageResponse(accountResponses, totalElements, pageable);
	}
}
