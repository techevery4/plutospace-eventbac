/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.domain.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.plutospace.events.commons.repositories.BaseRepository;
import com.plutospace.events.domain.entities.PlanPaymentHistory;

@Repository
public interface PlanPaymentHistoryRepository extends BaseRepository<PlanPaymentHistory, String> {
	boolean existsByPaystackReference(String reference);

	Page<PlanPaymentHistory> findAllByOrderByCreatedOnDesc(Pageable pageable);

	Page<PlanPaymentHistory> findByAccountIdOrderByCreatedOnDesc(String accountId, Pageable pageable);
}
