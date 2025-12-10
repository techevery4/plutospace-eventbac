/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.commons.utils;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.springframework.stereotype.Component;

@Component
public class DateConverter {

	public LocalDateTime convertTimestamp(long timeStamp) {
		return LocalDateTime.ofInstant(Instant.ofEpochMilli(timeStamp), TimeZone.getDefault().toZoneId());
	}

	public List<LocalDate> getDaysBetween(LocalDate startDate, LocalDate endDate, DayOfWeek dayOfWeek) {
		List<LocalDate> days = new ArrayList<>();

		// Ensure startDate <= endDate
		LocalDate date = startDate;
		while (!date.isAfter(endDate)) {
			if (date.getDayOfWeek() == dayOfWeek) {
				days.add(date);
			}
			date = date.plusDays(1);
		}

		return days;
	}

	public List<LocalDateTime> getDateTimeBetween(LocalDate startDate, LocalDate endDate, DayOfWeek dayOfWeek,
			String timeString) {
		LocalTime time = LocalTime.parse(timeString, DateTimeFormatter.ofPattern("HH:mm:ss"));
		List<LocalDateTime> days = new ArrayList<>();

		// Ensure startDate <= endDate
		LocalDate date = startDate;
		while (!date.isAfter(endDate)) {
			if (date.getDayOfWeek() == dayOfWeek) {
				LocalDateTime dateTime = LocalDateTime.of(date, time);
				days.add(dateTime);
			}
			date = date.plusDays(1);
		}

		return days;
	}

	public LocalDateTime mergeLocalDateAndTimeString(LocalDate date, String timeString) {
		// Parse string time
		LocalTime time = LocalTime.parse(timeString, DateTimeFormatter.ofPattern("HH:mm:ss"));

		// Combine date + time
		return LocalDateTime.of(date, time);
	}

	public String convertLocalDateTimeToTimestring(LocalDateTime localDateTime) {
		return localDateTime.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
	}

	public Long getCurrentTimestamp() {
		return new Date().getTime();
	}
}
