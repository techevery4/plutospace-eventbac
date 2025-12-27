/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.controllers;

import java.net.URI;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import com.plutospace.events.commons.definitions.GeneralConstants;
import com.plutospace.events.commons.definitions.PropertyConstants;
import com.plutospace.events.commons.utils.SecurityMapper;
import com.plutospace.events.domain.data.request.UploadMediaRequest;
import com.plutospace.events.domain.data.response.MediaResponse;
import com.plutospace.events.domain.data.response.OperationalResponse;
import com.plutospace.events.services.MediaService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

import static com.plutospace.events.commons.definitions.ApiResourceConstants.*;

@RestController
@RequestMapping(MEDIA)
@Tag(name = "Media Endpoints", description = "These endpoints manages media on PlutoSpace Events")
@RequiredArgsConstructor
public class MediaApiResource {

	private final MediaService mediaService;
	private final SecurityMapper securityMapper;
	private final PropertyConstants propertyConstants;
	private final HttpServletRequest request;

	@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(description = "This endpoint uploads a new media on PlutoSpace Events")
	public ResponseEntity<MediaResponse> save(
			@RequestParam(name = "file", required = false) MultipartFile multipartFile,
			@RequestParam(name = "type", required = false) String type,
			@RequestParam(name = "accountId", required = false) String accountId,
			@RequestParam(name = "size", required = false) Long size, UriComponentsBuilder uriComponentsBuilder) {

		UploadMediaRequest uploadMediaRequest = new UploadMediaRequest();
		uploadMediaRequest.setMultipartFile(multipartFile);
		if (StringUtils.isNotBlank(type))
			uploadMediaRequest.setType(type);
		if (ObjectUtils.isNotEmpty(size))
			uploadMediaRequest.setSize(size);
		if (StringUtils.isNotBlank(accountId))
			uploadMediaRequest.setAccountId(accountId);

		MediaResponse mediaResponse = mediaService.saveMedia(uploadMediaRequest);

		String location = uriComponentsBuilder.path(MEDIA_RESOURCE_ID).buildAndExpand(mediaResponse.getId())
				.toUriString();
		URI uri = URI.create(location);
		return ResponseEntity.created(uri).body(mediaResponse);
	}

	@PostMapping(path = "/large", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@Operation(description = "This endpoint uploads a new large media on PlutoSpace Events")
	public ResponseEntity<MediaResponse> saveLargeMediaFiles(
			@RequestParam(name = "file", required = false) MultipartFile multipartFile,
			@RequestParam(name = "type", required = false) String type,
			@RequestParam(name = "accountId", required = false) String accountId,
			@RequestParam(name = "size", required = false) Long size, UriComponentsBuilder uriComponentsBuilder) {

		UploadMediaRequest uploadMediaRequest = new UploadMediaRequest();
		uploadMediaRequest.setMultipartFile(multipartFile);
		if (StringUtils.isNotBlank(type))
			uploadMediaRequest.setType(type);
		if (ObjectUtils.isNotEmpty(size))
			uploadMediaRequest.setSize(size);
		if (StringUtils.isNotBlank(accountId))
			uploadMediaRequest.setAccountId(accountId);

		MediaResponse mediaResponse = mediaService.saveLargeMediaFiles(uploadMediaRequest);

		String location = uriComponentsBuilder.path(MEDIA_RESOURCE_ID).buildAndExpand(mediaResponse.getId())
				.toUriString();
		URI uri = URI.create(location);
		return ResponseEntity.created(uri).body(mediaResponse);
	}

	@GetMapping(path = "/download", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(description = "This endpoint downloads a media")
	public ResponseEntity<InputStreamResource> downloadMedia(@RequestParam(name = "fileName") String fileName) {
		return ResponseEntity.ok().cacheControl(CacheControl.noCache())
				.header("Content-type", "application/octet-stream")
				.header("Content-disposition", "attachment; filename=\"" + fileName + "\"")
				.body(new InputStreamResource(mediaService.downloadMedia(fileName)));
	}

	@GetMapping(path = "/details", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(description = "This endpoint retrieves media by the public id")
	public ResponseEntity<MediaResponse> retrieveMediaUsingPublicId(@RequestParam(name = "pid") String pid) {
		return ResponseEntity.ok(mediaService.retrieveMediaUsingPublicId(pid));
	}

	@DeleteMapping(path = RESOURCE_ID, produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(description = "This endpoint deletes a media")
	public ResponseEntity<OperationalResponse> deleteMedia(@PathVariable String id) {
		String accountId = securityMapper.retrieveAccountId(request.getHeader(GeneralConstants.TOKEN_KEY),
				propertyConstants.getEventsLoginEncryptionSecretKey());
		return ResponseEntity.ok(mediaService.deleteMedia(id, accountId));
	}
}
