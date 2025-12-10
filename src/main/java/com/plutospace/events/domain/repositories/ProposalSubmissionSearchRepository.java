/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.domain.repositories;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;

import com.plutospace.events.domain.entities.ProposalSubmissionData;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ProposalSubmissionSearchRepository {

	private final MongoTemplate mongoTemplate;

	public List<ProposalSubmissionData> searchMultipleSubstringsIgnoreCaseByProposalId(List<String> substrings,
			String proposalId, int page, int size) {

		// Lowercase the substrings (case-insensitive match)
		List<String> lowered = substrings.stream().map(String::toLowerCase).toList();

		// Build dynamic substring occurrence expressions
		List<String> occurrenceExpressions = new ArrayList<>();

		for (String s : lowered) {
			occurrenceExpressions.add(String
					.format("(strLenCP(lowerMsg) - strLenCP(replaceAll(lowerMsg, '%s', ''))) / strLenCP('%s')", s, s));
		}

		// totalOccurrences = sum of all substring matches
		String totalOccurrencesExpr = occurrenceExpressions.stream()
				.collect(java.util.stream.Collectors.joining(" + "));

		//
		// ----- Aggregation Pipeline -----
		//

		// Step 1: Filter by proposalId (important performance step)
		MatchOperation filterByProposalId = Aggregation.match(Criteria.where("proposalId").is(proposalId));

		// Step 2: Project lowercase field and compute occurrences
		ProjectionOperation project = Aggregation.project()
				// lowercase message for case-insensitive search
				.andExpression("toLower(content)").as("lowerMsg")
				// include original message
				.andInclude("content", "proposalId")
				// compute total occurrence count
				.andExpression(totalOccurrencesExpr).as("totalOccurrences");

		// Step 3: Remove documents that have no matches
		MatchOperation matchOccurrences = Aggregation.match(Criteria.where("totalOccurrences").gt(0));

		// Step 4: Sort by highest frequency
		SortOperation sort = Aggregation.sort(Sort.Direction.DESC, "totalOccurrences");

		// Step 5: Pagination
		SkipOperation skip = Aggregation.skip((long) page * size);
		LimitOperation limit = Aggregation.limit(size);

		Aggregation aggregation = Aggregation.newAggregation(filterByProposalId, // filter FIRST for performance
				project, matchOccurrences, sort, skip, limit);

		return mongoTemplate.aggregate(aggregation, "proposalSubmissionData", ProposalSubmissionData.class)
				.getMappedResults();
	}
}
