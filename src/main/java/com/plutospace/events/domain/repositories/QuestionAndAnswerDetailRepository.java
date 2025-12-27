/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.domain.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.plutospace.events.commons.repositories.BaseRepository;
import com.plutospace.events.domain.entities.QuestionAndAnswerDetail;

@Repository
public interface QuestionAndAnswerDetailRepository extends BaseRepository<QuestionAndAnswerDetail, String> {
	List<QuestionAndAnswerDetail> findByIdIn(List<String> ids);

	Page<QuestionAndAnswerDetail> findByQuestionAnswerIdAndIsAnsweredOrderByCreatedOnDesc(String questionAndAnswerId,
			boolean isAnswered, Pageable pageable);

	Page<QuestionAndAnswerDetail> findByQuestionAnswerIdOrderByCreatedOnDesc(String questionAndAnswerId,
			Pageable pageable);

	boolean existsByQuestionAnswerId(String questionAndAnswerId);

	Long countByQuestionAnswerId(String questionAndAnswerId);
}
