/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.domain.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.plutospace.events.commons.repositories.BaseRepository;
import com.plutospace.events.domain.entities.AccountUser;

@Repository
public interface AccountUserRepository extends BaseRepository<AccountUser, String> {
	boolean existsByEmailIgnoreCase(String email);

	Optional<AccountUser> findByEmailIgnoreCase(String email);

	@Query("SELECT u FROM AccountUser u WHERE LOWER(u.email) IN :emails")
	List<AccountUser> findByEmailIgnoreCaseIn(@Param("emails") List<String> emails);

	List<AccountUser> findByIdIn(List<String> ids);

	Page<AccountUser> findByAccountIdOrderByLastNameAsc(String accountId, Pageable pageable);

	Long countByAccountId(String accountId);
}
