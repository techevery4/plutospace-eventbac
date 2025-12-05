/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.services.implementations;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.services.s3.transfer.MultipleFileUpload;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;
import com.plutospace.events.commons.definitions.GeneralConstants;
import com.plutospace.events.commons.definitions.PropertyConstants;
import com.plutospace.events.commons.exception.GeneralPlatformDomainRuleException;
import com.plutospace.events.commons.exception.GeneralPlatformServiceException;
import com.plutospace.events.commons.exception.ResourceNotFoundException;
import com.plutospace.events.commons.utils.FileUtil;
import com.plutospace.events.commons.utils.LinkGenerator;
import com.plutospace.events.domain.data.request.UploadMediaRequest;
import com.plutospace.events.domain.data.response.MediaResponse;
import com.plutospace.events.domain.data.response.OperationalResponse;
import com.plutospace.events.domain.entities.Media;
import com.plutospace.events.domain.repositories.MediaRepository;
import com.plutospace.events.services.MediaService;
import com.plutospace.events.services.mappers.MediaMapper;
import com.plutospace.events.validation.MediaValidator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class MediaServiceImpl implements MediaService {

	private final MediaRepository mediaRepository;
	private final MediaMapper mediaMapper;
	private final AmazonS3 amazonS3;
	private final MediaValidator mediaValidator;
	private final LinkGenerator linkGenerator;
	private final FileUtil fileUtil;
	private final PropertyConstants propertyConstants;

	@Override
	public MediaResponse saveMedia(UploadMediaRequest uploadMediaRequest) {
		mediaValidator.validate(uploadMediaRequest);

		File file;
		if (uploadMediaRequest.getMultipartFile() != null)
			file = fileUtil.convertMultiPartFileToFile(uploadMediaRequest.getMultipartFile());
		else
			throw new GeneralPlatformDomainRuleException("File is missing");

		String errorMessage;
		try {
			final String fileName = fileUtil.generateFileName(uploadMediaRequest.getMultipartFile());
			log.info("Uploading file with name {}", fileName);
			final PutObjectRequest putObjectRequest = new PutObjectRequest(propertyConstants.getS3BucketName(),
					fileName, file).withCannedAcl(CannedAccessControlList.PublicRead);
			amazonS3.putObject(putObjectRequest);
			file.delete(); // Remove the file locally created in the project folder

			// Saving to database
			String fileUrl = propertyConstants.getS3DisplayEndpoint() + "/" + propertyConstants.getS3BucketName() + "/"
					+ fileName;
			Media media = mediaMapper.toEntity(uploadMediaRequest, fileName, fileUrl);
			Media savedMedia = mediaRepository.save(media);
			String publicLink = linkGenerator.generatePublicLink(savedMedia.getId(), savedMedia.getAccountId(),
					GeneralConstants.MEDIA, propertyConstants.getEventsEncryptionSecretKey());
			savedMedia.setPublicId(publicLink);
			mediaRepository.save(savedMedia);

			return mediaMapper.toResponse(savedMedia);
		} catch (AmazonServiceException e) {
			log.error("Error {} occurred while uploading file", e.getLocalizedMessage());
			errorMessage = "Error while uploading file";
		} catch (Exception e) {
			log.error("Error While Saving File {} ", e.getLocalizedMessage());
			errorMessage = "General error while saving media";
		}

		throw new GeneralPlatformServiceException(errorMessage);
	}

	@Override
	public MediaResponse saveLargeMediaFiles(UploadMediaRequest uploadMediaRequest) {
		mediaValidator.validate(uploadMediaRequest);

		File file;
		if (uploadMediaRequest.getMultipartFile() != null)
			file = fileUtil.convertMultiPartFileToFile(uploadMediaRequest.getMultipartFile());
		else
			throw new GeneralPlatformDomainRuleException("File is missing");

		String errorMessage;
		try {
			final String fileName = fileUtil.generateFileName(uploadMediaRequest.getMultipartFile());
			log.info("Uploading (large) file with name {}", propertyConstants.getS3BucketName());

			TransferManager tm = TransferManagerBuilder.standard().withS3Client(amazonS3)
					.withMultipartUploadThreshold((long) (50 * 1024 * 1025)).build();

			long start = System.currentTimeMillis();
			List<File> files = new ArrayList<>();
			files.add(file);
			final MultipleFileUpload fileListResult = tm.uploadFileList(propertyConstants.getS3BucketName(), null,
					new File("."), files, null, null, myFile -> CannedAccessControlList.PublicRead);
			// Upload result = tm.upload(apis.getS3BucketName(), "m" + suffixTime + "_" +
			// file.getName(),
			// file);
			fileListResult.waitForCompletion();
			long end = System.currentTimeMillis();
			log.info("Complete Multipart Uploading {}s", (end - start) / 1000);

			// Saving to database
			String fileUrl = propertyConstants.getS3DisplayEndpoint() + "/" + propertyConstants.getS3BucketName() + "/"
					+ fileName;
			Media media = mediaMapper.toEntity(uploadMediaRequest, fileName, fileUrl);
			Media savedMedia = mediaRepository.save(media);
			String publicLink = linkGenerator.generatePublicLink(savedMedia.getId(), savedMedia.getAccountId(),
					GeneralConstants.MEDIA, propertyConstants.getEventsEncryptionSecretKey());
			savedMedia.setPublicId(publicLink);
			mediaRepository.save(savedMedia);

			return mediaMapper.toResponse(savedMedia);
		} catch (AmazonServiceException e) {
			log.error("Error {} occurred while uploading (large) file", e.getLocalizedMessage());
			errorMessage = "Error while uploading file";
		} catch (Exception e) {
			log.error("Error While Saving (large) File {} ", e.getLocalizedMessage());
			errorMessage = "General error while saving media";
		}

		throw new GeneralPlatformServiceException(errorMessage);
	}

	@Override
	public S3ObjectInputStream downloadMedia(String fileName) {
		return amazonS3.getObject(propertyConstants.getS3BucketName(), fileName).getObjectContent();
	}

	@Override
	public MediaResponse retrieveMediaUsingPublicId(String publicId) {
		String decryptedLink = linkGenerator.extractDetailsFromPublicLink(publicId,
				propertyConstants.getEventsEncryptionSecretKey());
		String[] words = decryptedLink.split(":");
		if (words.length < 1)
			throw new GeneralPlatformDomainRuleException("Media public id has been corrupted");

		Media media = retrieveMediaById(words[0]);

		return mediaMapper.toResponse(media);
	}

	@Override
	public OperationalResponse deleteMedia(String id) {
		Media existingMedia = retrieveMediaById(id);

		try {
			mediaRepository.delete(existingMedia);

			return OperationalResponse.instance(GeneralConstants.SUCCESS_MESSAGE);
		} catch (DataIntegrityViolationException e) {
			throw new DataIntegrityViolationException(e.getLocalizedMessage());
		}
	}

	private Media retrieveMediaById(String id) {
		return mediaRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Media Not Found"));
	}
}
