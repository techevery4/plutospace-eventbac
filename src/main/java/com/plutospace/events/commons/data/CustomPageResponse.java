/* Developed by TechEveryWhere Engineering (C)2024 */
package com.plutospace.events.commons.data;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class CustomPageResponse<T> {
	private int totalPages;

	private int size;

	private long totalElements;

	private boolean hasNext;

	private boolean hasPrevious;

	private List<T> content = new ArrayList<>();

	public static <T> CustomPageResponse<T> resolvePageResponse(List<T> objectList, long totalElements,
			Pageable pageable) {
		CustomPageResponse<T> customPageResponse = new CustomPageResponse<>();
		boolean hasNext = pageable.getOffset() + pageable.getPageSize() < totalElements;
		boolean hasPrevious = pageable.getOffset() > 0;
		int totalPages = (int) Math.ceil((double) totalElements / pageable.getPageSize());
		customPageResponse.setContent(objectList);
		customPageResponse.setSize(objectList.size());
		customPageResponse.setHasNext(hasNext);
		customPageResponse.setHasPrevious(hasPrevious);
		customPageResponse.setTotalElements(totalElements);
		customPageResponse.setTotalPages(totalPages);
		return customPageResponse;
	}
}
