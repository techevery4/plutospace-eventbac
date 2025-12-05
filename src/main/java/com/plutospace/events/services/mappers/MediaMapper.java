/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.services.mappers;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import com.plutospace.events.commons.data.CustomPageResponse;
import com.plutospace.events.domain.data.request.UploadMediaRequest;
import com.plutospace.events.domain.data.response.MediaResponse;
import com.plutospace.events.domain.entities.Media;

@Component
public class MediaMapper {

	public MediaResponse toResponse(Media media) {
		return MediaResponse.instance(media.getId(), media.getAccountId(), media.getName(), media.getDisplayName(),
				media.getType(), media.getPublicId(), media.getSize(), media.getCreatedOn());
	}

	public Media toEntity(UploadMediaRequest uploadMediaRequest, String name, String displayName) {
		return Media.instance(uploadMediaRequest.getAccountId(), name, displayName, uploadMediaRequest.getType(), null,
				uploadMediaRequest.getSize());
	}

	public CustomPageResponse<MediaResponse> toPagedResponse(Page<Media> media) {
		List<MediaResponse> mediaResponses = media.getContent().stream().map(this::toResponse).toList();
		long totalElements = media.getTotalElements();
		Pageable pageable = media.getPageable();
		return CustomPageResponse.resolvePageResponse(mediaResponses, totalElements, pageable);
	}
}
