/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.commons.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Objects;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class FileUtil {

	public File convertMultiPartFileToFile(final MultipartFile multipartFile) {
		final File file = new File(Objects.requireNonNull(multipartFile.getOriginalFilename()));
		try (final FileOutputStream outputStream = new FileOutputStream(file)) {
			outputStream.write(multipartFile.getBytes());
		} catch (IOException e) {
			log.error("Error {} occurred while converting the multipart file", e.getLocalizedMessage());
		}
		return file;
	}

	public String generateFileName(MultipartFile multiPart) {
		return new Date().getTime() + "-" + multiPart.getOriginalFilename().replace(" ", "_");
	}
}
