package com.endava.example.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.endava.example.utils.GenericResponse;

/**
 * GlobalExceptionHandler is a centralized exception handler that intercepts and
 * manages various exceptions thrown in the application. It provides custom
 * responses for different types of exceptions to ensure consistent error
 * handling.
 * 
 * Annotated with @RestControllerAdvice, making it a global exception handler
 * for all controllers in the application.
 * 
 * It handles the following:
 * 
 * - ResourceNotFoundException: Returns a 404 - NOT_FOUND response with the
 * exception message.
 * 
 * - ResourceAlreadyExistsException: Returns a 409 - CONFLICT response with the
 * exception message.
 * 
 * - InvalidCredentialException: Returns a 401 - UNAUTHORIZED response with the
 * exception message.
 * 
 * - PaymentFailedException: Returns a 402 - PAYMENT_REQUIRED response with the
 * exception message.
 * 
 * - IllegalArgumentException: Returns a 500 - INTERNAL_SERVER_ERROR response
 * with the exception message.
 * 
 * - Exception: A catch-all handler for unexpected errors, returning a 500 -
 * INTERNAL_SERVER_ERROR response with a general error message.
 * 
 * Each handler wraps the exception message into a standardized ApiResponse
 * object to maintain consistency in the response structure.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<GenericResponse<String>> handleResourceNotFoundException(ResourceNotFoundException ex) {
		return new ResponseEntity<>(new GenericResponse<>(false, ex.getMessage(), null), HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(InvalidCredentialException.class)
	public ResponseEntity<GenericResponse<String>> handleInvalidCredentialException(InvalidCredentialException ex) {
		return new ResponseEntity<>(new GenericResponse<>(false, ex.getMessage(), null), HttpStatus.UNAUTHORIZED);
	}

	@ExceptionHandler(ResourceAlreadyExistsException.class)
	public ResponseEntity<GenericResponse<String>> handleResourceAlreadyExistsException(
			ResourceAlreadyExistsException ex) {
		return new ResponseEntity<>(new GenericResponse<>(false, ex.getMessage(), null), HttpStatus.CONFLICT);
	}

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<GenericResponse<String>> handleIllegalArgumentException(IllegalArgumentException ex) {
		return new ResponseEntity<>(new GenericResponse<>(false, ex.getMessage(), null),
				HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler
	public ResponseEntity<GenericResponse<String>> handlePaymentFailedException(PaymentFailedException ex) {
		return new ResponseEntity<>(new GenericResponse<>(false, "An unexpected error occured", null),
				HttpStatus.PAYMENT_REQUIRED);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<GenericResponse<String>> handleGeneralException(Exception ex) {
		return new ResponseEntity<>(new GenericResponse<>(false, "An unexpected error occured", null),
				HttpStatus.INTERNAL_SERVER_ERROR);
	}
}
