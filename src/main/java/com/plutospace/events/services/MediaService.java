/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.services;

import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.plutospace.events.domain.data.request.UploadMediaRequest;
import com.plutospace.events.domain.data.response.MediaResponse;
import com.plutospace.events.domain.data.response.OperationalResponse;

public interface MediaService {

	MediaResponse saveMedia(UploadMediaRequest uploadMediaRequest);

	MediaResponse saveLargeMediaFiles(UploadMediaRequest uploadMediaRequest);

	S3ObjectInputStream downloadMedia(String fileName);

	MediaResponse retrieveMediaUsingPublicId(String publicId);

	OperationalResponse deleteMedia(String id, String accountId);
}
