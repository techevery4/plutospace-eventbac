/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.commons.exception.handler;

import java.util.Date;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import com.plutospace.events.commons.exception.*;

import io.swagger.v3.oas.annotations.Hidden;

@Hidden
@ControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(UnauthorizedAccessException.class)
	public ResponseEntity<ExceptionResponse> unauthorizedAccessException(UnauthorizedAccessException ex,
			WebRequest request) {
		ExceptionResponse exceptionResponse = new ExceptionResponse(new Date(), ex.getMessage(),
				request.getDescription(false));
		return new ResponseEntity<>(exceptionResponse, HttpStatus.UNAUTHORIZED);
	}

	@ExceptionHandler(GeneralPlatformDomainRuleException.class)
	public ResponseEntity<ExceptionResponse> generalPlatformDomainRuleException(GeneralPlatformDomainRuleException ex,
			WebRequest request) {
		ExceptionResponse exceptionResponse = new ExceptionResponse(new Date(), ex.getMessage(),
				request.getDescription(false));
		return new ResponseEntity<>(exceptionResponse, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(GeneralPlatformServiceException.class)
	public ResponseEntity<ExceptionResponse> generalPlatformServiceException(GeneralPlatformServiceException ex,
			WebRequest request) {
		ExceptionResponse exceptionResponse = new ExceptionResponse(new Date(), ex.getMessage(),
				request.getDescription(false));
		return new ResponseEntity<>(exceptionResponse, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<ExceptionResponse> resourceNotFoundException(ResourceNotFoundException ex,
			WebRequest request) {
		ExceptionResponse exceptionResponse = new ExceptionResponse(new Date(), ex.getMessage(),
				request.getDescription(false));
		return new ResponseEntity<>(exceptionResponse, HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(ResourceAlreadyExistsException.class)
	public ResponseEntity<ExceptionResponse> resourceAlreadyExistsException(ResourceAlreadyExistsException ex,
			WebRequest request) {
		ExceptionResponse exceptionResponse = new ExceptionResponse(new Date(), ex.getMessage(),
				request.getDescription(false));
		return new ResponseEntity<>(exceptionResponse, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(InvalidInputException.class)
	public ResponseEntity<ExceptionResponse> invalidInputException(InvalidInputException ex, WebRequest request) {
		ExceptionResponse exceptionResponse = new ExceptionResponse(new Date(), ex.getMessage(),
				request.getDescription(false));
		return new ResponseEntity<>(exceptionResponse, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(LoginDisputeException.class)
	public ResponseEntity<ExceptionResponse> incorrectAccessException(LoginDisputeException ex, WebRequest request) {
		ExceptionResponse exceptionResponse = new ExceptionResponse(new Date(), ex.getMessage(),
				request.getDescription(false));
		return new ResponseEntity<>(exceptionResponse, HttpStatus.PRECONDITION_FAILED);
	}

	@ExceptionHandler(ExternalServiceException.class)
	public ResponseEntity<ExceptionResponse> ExternalServiceException(ExternalServiceException ex, WebRequest request) {
		ExceptionResponse exceptionResponse = new ExceptionResponse(new Date(), ex.getMessage(),
				request.getDescription(false));
		return new ResponseEntity<>(exceptionResponse, HttpStatus.BAD_REQUEST);
	}
}
