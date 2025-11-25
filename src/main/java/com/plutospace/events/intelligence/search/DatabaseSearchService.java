/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.intelligence.search;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.plutospace.events.domain.entities.AccountUser;
import com.plutospace.events.domain.entities.AdminUser;
import com.plutospace.events.domain.entities.Enquiry;
import com.plutospace.events.domain.entities.EventCategory;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class DatabaseSearchService {

	private final MongoTemplate mongoTemplate;

	public Page<AccountUser> findByDynamicFilter(String searchText, List<String> fields, Pageable pageable) {
		Query query = new Query();

		if (searchText != null && !searchText.trim().isEmpty()) {
			// Create a regex for "like %text%" equivalent (case-insensitive)
			Pattern pattern = Pattern.compile(".*" + Pattern.quote(searchText) + ".*", Pattern.CASE_INSENSITIVE);

			List<Criteria> criteriaList = new ArrayList<>();
			for (String field : fields) {
				criteriaList.add(Criteria.where(field).regex(pattern));
			}
			Criteria fullSearchCriteria = new Criteria().orOperator(criteriaList);
			query.addCriteria(fullSearchCriteria);
		}

		// Count total documents matching the criteria for pagination
		long total = mongoTemplate.count(query, AccountUser.class);

		// Apply pagination to the query
		query.with(pageable);

		// Execute the query to get the paginated results
		List<AccountUser> accountUsers = mongoTemplate.find(query, AccountUser.class);

		// Return a Page object
		return new PageImpl<>(accountUsers, pageable, total);
	}

	public Page<AdminUser> findAdminUserByDynamicFilter(String searchText, List<String> fields, Pageable pageable) {
		Query query = new Query();

		if (StringUtils.isNotBlank(searchText)) {
			Pattern pattern = Pattern.compile(".*" + Pattern.quote(searchText) + ".*", Pattern.CASE_INSENSITIVE);

			List<Criteria> criteriaList = new ArrayList<>();
			for (String field : fields) {
				criteriaList.add(Criteria.where(field).regex(pattern));
			}
			Criteria fullSearchCriteria = new Criteria().orOperator(criteriaList);
			query.addCriteria(fullSearchCriteria);
		}

		long total = mongoTemplate.count(query, AdminUser.class);
		query.with(pageable);

		List<AdminUser> adminUsers = mongoTemplate.find(query, AdminUser.class);

		// Return a Page object
		return new PageImpl<>(adminUsers, pageable, total);
	}

	public Page<Enquiry> findEnquiryByDynamicFilter(String searchText, List<String> fields, Pageable pageable) {
		Query query = new Query();

		if (StringUtils.isNotBlank(searchText)) {
			Pattern pattern = Pattern.compile(".*" + Pattern.quote(searchText) + ".*", Pattern.CASE_INSENSITIVE);

			List<Criteria> criteriaList = new ArrayList<>();
			for (String field : fields) {
				criteriaList.add(Criteria.where(field).regex(pattern));
			}
			Criteria fullSearchCriteria = new Criteria().orOperator(criteriaList);
			query.addCriteria(fullSearchCriteria);
		}

		long total = mongoTemplate.count(query, Enquiry.class);
		query.with(pageable);

		List<Enquiry> enquiries = mongoTemplate.find(query, Enquiry.class);

		// Return a Page object
		return new PageImpl<>(enquiries, pageable, total);
	}

	public Page<EventCategory> findEventCategoryByDynamicFilter(String searchText, List<String> fields,
			Pageable pageable) {
		Query query = new Query();

		if (StringUtils.isNotBlank(searchText)) {
			Pattern pattern = Pattern.compile(".*" + Pattern.quote(searchText) + ".*", Pattern.CASE_INSENSITIVE);

			List<Criteria> criteriaList = new ArrayList<>();
			for (String field : fields) {
				criteriaList.add(Criteria.where(field).regex(pattern));
			}
			Criteria fullSearchCriteria = new Criteria().orOperator(criteriaList);
			query.addCriteria(fullSearchCriteria);
		}

		long total = mongoTemplate.count(query, EventCategory.class);
		query.with(pageable);

		List<EventCategory> eventCategories = mongoTemplate.find(query, EventCategory.class);

		// Return a Page object
		return new PageImpl<>(eventCategories, pageable, total);
	}
}
