/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.domain.repositories;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.plutospace.events.commons.repositories.BaseRepository;
import com.plutospace.events.domain.entities.QuestionAndAnswer;

@Repository
public interface QuestionAndAnswerRepository extends BaseRepository<QuestionAndAnswer, String> {
	Page<QuestionAndAnswer> findByAccountIdOrderByCreatedOnDesc(String accountId, Pageable pageable);

	Long countByAccountIdAndCreatedOnBetween(String accountId, LocalDateTime startTime, LocalDateTime endTime);
}
