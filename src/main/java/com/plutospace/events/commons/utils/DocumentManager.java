/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.commons.utils;

import java.io.InputStream;
import java.net.URL;

import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.sax.BodyContentHandler;
import org.springframework.stereotype.Component;

import lombok.Data;

@Component
public class DocumentManager {

	public TikaResult extractContentAndMetadata(String documentUrl) throws Exception {
		URL url = new URL(documentUrl);
		try (InputStream input = url.openStream()) {
			AutoDetectParser parser = new AutoDetectParser();
			BodyContentHandler handler = new BodyContentHandler();
			Metadata metadata = new Metadata();
			ParseContext context = new ParseContext();

			parser.parse(input, handler, metadata, context);

			return new TikaResult(handler.toString(), metadata);
		}
	}

	@Data
	public static class TikaResult {
		private final String content;
		private final Metadata metadata;
	}
}
