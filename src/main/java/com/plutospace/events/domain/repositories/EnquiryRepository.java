/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.domain.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.plutospace.events.commons.repositories.BaseRepository;
import com.plutospace.events.domain.entities.Enquiry;

@Repository
public interface EnquiryRepository extends BaseRepository<Enquiry, String> {
	Page<Enquiry> findByIsTreatedNot(boolean isTreated, Pageable pageable);
}
