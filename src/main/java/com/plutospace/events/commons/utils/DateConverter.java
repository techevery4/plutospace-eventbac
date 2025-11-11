/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.commons.utils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.TimeZone;

import org.springframework.stereotype.Component;

@Component
public class DateConverter {

	public LocalDateTime convertTimestamp(long timeStamp) {
		return LocalDateTime.ofInstant(Instant.ofEpochMilli(timeStamp), TimeZone.getDefault().toZoneId());
	}
}
