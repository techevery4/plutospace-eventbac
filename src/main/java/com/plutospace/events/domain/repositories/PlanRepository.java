/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.domain.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.plutospace.events.commons.repositories.BaseRepository;
import com.plutospace.events.domain.data.PlanType;
import com.plutospace.events.domain.entities.Plan;

@Repository
public interface PlanRepository extends BaseRepository<Plan, String> {
	boolean existsByNameIgnoreCase(String name);

	Plan findByNameIgnoreCase(String name);

	List<Plan> findByIdIn(List<String> ids);

	Page<Plan> findByTypeAndIsActiveOrderByPriceNairaDesc(PlanType planType, boolean b, Pageable pageable);
}
