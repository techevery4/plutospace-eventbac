/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.domain.repositories;

import org.springframework.stereotype.Repository;

import com.plutospace.events.commons.repositories.BaseRepository;
import com.plutospace.events.domain.entities.AccountUser;

@Repository
public interface AccountUserRepository extends BaseRepository<AccountUser, String> {
	boolean existsByEmailIgnoreCase(String email);
}
