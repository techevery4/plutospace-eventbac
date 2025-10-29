/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events;

import java.util.TimeZone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import jakarta.annotation.PostConstruct;

@EnableAspectJAutoProxy
@SpringBootApplication
public class EventsApplication {

	public static void main(String[] args) {
		TimeZone.setDefault(TimeZone.getTimeZone("Africa/Lagos"));
		SpringApplication.run(EventsApplication.class, args);
	}

	@PostConstruct
	public void init() {
		TimeZone.setDefault(TimeZone.getTimeZone("GMT+1"));
	}
}
