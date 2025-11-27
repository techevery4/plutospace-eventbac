/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.domain.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.plutospace.events.commons.repositories.BaseRepository;
import com.plutospace.events.domain.entities.PromoCode;

@Repository
public interface PromoCodeRepository extends BaseRepository<PromoCode, String> {
	boolean existsByCodeIgnoreCase(String code);

	Page<PromoCode> findAllByOrderByCreatedOnDesc(Pageable pageable);
}
