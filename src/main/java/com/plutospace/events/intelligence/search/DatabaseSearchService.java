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

import com.plutospace.events.domain.entities.*;

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

	public Page<Event> findEventByDynamicFilter(String accountId, String searchText, List<String> fields,
			Pageable pageable) {
		Query query = new Query();

		if (StringUtils.isNotBlank(searchText)) {
			Pattern pattern = Pattern.compile(".*" + Pattern.quote(searchText) + ".*", Pattern.CASE_INSENSITIVE);

			List<Criteria> criteriaList = new ArrayList<>();
			for (String field : fields) {
				criteriaList.add(Criteria.where(field).regex(pattern));
			}

			Criteria fullSearchCriteria = new Criteria().orOperator(criteriaList);
			Criteria accountCriteria = Criteria.where("accountId").is(accountId).andOperator(fullSearchCriteria);
			query.addCriteria(accountCriteria);
		}

		long total = mongoTemplate.count(query, Event.class);
		query.with(pageable);

		List<Event> events = mongoTemplate.find(query, Event.class);

		// Return a Page object
		return new PageImpl<>(events, pageable, total);
	}

	public Page<FreeSlot> findFreeSlotByDynamicFilter(String accountId, String accountUserId, String searchText,
			List<String> fields, Pageable pageable) {
		Query query = new Query();

		if (StringUtils.isNotBlank(searchText)) {
			Pattern pattern = Pattern.compile(".*" + Pattern.quote(searchText) + ".*", Pattern.CASE_INSENSITIVE);

			List<Criteria> criteriaList = new ArrayList<>();
			for (String field : fields) {
				criteriaList.add(Criteria.where(field).regex(pattern));
			}

			Criteria fullSearchCriteria = new Criteria().orOperator(criteriaList);
			Criteria accountCriteria = Criteria.where("accountId").is(accountId).and("createdBy").is(accountUserId)
					.andOperator(fullSearchCriteria);
			query.addCriteria(accountCriteria);
		}

		long total = mongoTemplate.count(query, FreeSlot.class);
		query.with(pageable);

		List<FreeSlot> freeSlots = mongoTemplate.find(query, FreeSlot.class);

		// Return a Page object
		return new PageImpl<>(freeSlots, pageable, total);
	}

	public Page<Meeting> findMeetingByDynamicFilter(String accountId, String accountUserId, String searchText,
			List<String> fields, Pageable pageable) {
		Query query = new Query();

		if (StringUtils.isNotBlank(searchText)) {
			Pattern pattern = Pattern.compile(".*" + Pattern.quote(searchText) + ".*", Pattern.CASE_INSENSITIVE);

			List<Criteria> criteriaList = new ArrayList<>();
			for (String field : fields) {
				criteriaList.add(Criteria.where(field).regex(pattern));
			}

			Criteria fullSearchCriteria = new Criteria().orOperator(criteriaList);
			Criteria accountCriteria = Criteria.where("accountId").is(accountId).and("createdBy").is(accountUserId)
					.andOperator(fullSearchCriteria);
			query.addCriteria(accountCriteria);
		}

		long total = mongoTemplate.count(query, Meeting.class);
		query.with(pageable);

		List<Meeting> meetings = mongoTemplate.find(query, Meeting.class);

		// Return a Page object
		return new PageImpl<>(meetings, pageable, total);
	}

	public Page<MeetingInvitee> findMeetingInviteeByDynamicFilter(String meetingId, String searchText,
			List<String> fields, Pageable pageable) {
		Query query = new Query();

		if (StringUtils.isNotBlank(searchText)) {
			Pattern pattern = Pattern.compile(".*" + Pattern.quote(searchText) + ".*", Pattern.CASE_INSENSITIVE);

			List<Criteria> criteriaList = new ArrayList<>();
			for (String field : fields) {
				criteriaList.add(Criteria.where(field).regex(pattern));
			}

			Criteria fullSearchCriteria = new Criteria().orOperator(criteriaList);
			Criteria accountCriteria = Criteria.where("meetingId").is(meetingId).andOperator(fullSearchCriteria);
			query.addCriteria(accountCriteria);
		}

		long total = mongoTemplate.count(query, MeetingInvitee.class);
		query.with(pageable);

		List<MeetingInvitee> meetingInvitees = mongoTemplate.find(query, MeetingInvitee.class);

		// Return a Page object
		return new PageImpl<>(meetingInvitees, pageable, total);
	}

	public Page<Permission> findPermissionByDynamicFilter(String searchText, List<String> fields, Pageable pageable) {
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

		long total = mongoTemplate.count(query, Permission.class);
		query.with(pageable);

		List<Permission> permissions = mongoTemplate.find(query, Permission.class);

		// Return a Page object
		return new PageImpl<>(permissions, pageable, total);
	}

	public Page<Plan> findPlanByDynamicFilter(String searchText, List<String> fields, Pageable pageable) {
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

		long total = mongoTemplate.count(query, Plan.class);
		query.with(pageable);

		List<Plan> plans = mongoTemplate.find(query, Plan.class);

		// Return a Page object
		return new PageImpl<>(plans, pageable, total);
	}

	public Page<PromoCode> findPromoCodeByDynamicFilter(String searchText, List<String> fields, Pageable pageable) {
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

		long total = mongoTemplate.count(query, PromoCode.class);
		query.with(pageable);

		List<PromoCode> promoCodes = mongoTemplate.find(query, PromoCode.class);

		// Return a Page object
		return new PageImpl<>(promoCodes, pageable, total);
	}

	public Page<PromoCodeRegistrationLog> findPromoCodeRegistrationLogByDynamicFilter(String promoCode,
			String searchText, List<String> fields, Pageable pageable) {
		Query query = new Query();

		if (StringUtils.isNotBlank(searchText)) {
			Pattern pattern = Pattern.compile(".*" + Pattern.quote(searchText) + ".*", Pattern.CASE_INSENSITIVE);

			List<Criteria> criteriaList = new ArrayList<>();
			for (String field : fields) {
				criteriaList.add(Criteria.where(field).regex(pattern));
			}

			Criteria fullSearchCriteria = new Criteria().orOperator(criteriaList);
			Criteria accountCriteria = Criteria.where("promoCode").is(promoCode).andOperator(fullSearchCriteria);
			query.addCriteria(accountCriteria);
		}

		long total = mongoTemplate.count(query, PromoCodeRegistrationLog.class);
		query.with(pageable);

		List<PromoCodeRegistrationLog> promoCodeRegistrationLogs = mongoTemplate.find(query,
				PromoCodeRegistrationLog.class);

		// Return a Page object
		return new PageImpl<>(promoCodeRegistrationLogs, pageable, total);
	}

	public Page<PlanPaymentHistory> findPlanPaymentHistoryByDynamicFilter(String searchText, List<String> fields,
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

		long total = mongoTemplate.count(query, PlanPaymentHistory.class);
		query.with(pageable);

		List<PlanPaymentHistory> planPaymentHistories = mongoTemplate.find(query, PlanPaymentHistory.class);

		// Return a Page object
		return new PageImpl<>(planPaymentHistories, pageable, total);
	}

	public Page<PlanPaymentHistory> findUserPlanPaymentHistoryByDynamicFilter(String accountId, String searchText,
			List<String> fields, Pageable pageable) {
		Query query = new Query();

		if (StringUtils.isNotBlank(searchText)) {
			Pattern pattern = Pattern.compile(".*" + Pattern.quote(searchText) + ".*", Pattern.CASE_INSENSITIVE);

			List<Criteria> criteriaList = new ArrayList<>();
			for (String field : fields) {
				criteriaList.add(Criteria.where(field).regex(pattern));
			}

			Criteria fullSearchCriteria = new Criteria().orOperator(criteriaList);
			Criteria accountCriteria = Criteria.where("accountId").is(accountId).andOperator(fullSearchCriteria);
			query.addCriteria(accountCriteria);
		}

		long total = mongoTemplate.count(query, PlanPaymentHistory.class);
		query.with(pageable);

		List<PlanPaymentHistory> planPaymentHistories = mongoTemplate.find(query, PlanPaymentHistory.class);

		// Return a Page object
		return new PageImpl<>(planPaymentHistories, pageable, total);
	}
}
