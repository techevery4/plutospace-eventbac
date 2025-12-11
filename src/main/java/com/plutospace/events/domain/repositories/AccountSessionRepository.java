/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.domain.repositories;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.plutospace.events.commons.repositories.BaseRepository;
import com.plutospace.events.domain.entities.AccountSession;

@Repository
public interface AccountSessionRepository extends BaseRepository<AccountSession, String> {
	AccountSession findByAccountIdAndUserIdAndUserAgentIgnoreCase(String accountId, String userId, String userAgent);

	List<AccountSession> findByUserIdOrderByLastUseTimeDesc(String userId);

	Long countByAccountId(String accountId);
}
