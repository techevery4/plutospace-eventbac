/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.commons.exception.handler;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ExceptionResponse {

	private Date timestamp;
	private String message;
	private String details;
}
