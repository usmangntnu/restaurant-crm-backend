package com.restaurantcrm.restaurant_crm_backend.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.NonNull;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler for handling various exceptions across the restaurant CRM backend.
 * <p>
 * Provides centralized exception handling using {@link RestControllerAdvice}.
 * Ensures that all errors are returned with a consistent structure without stack traces.
 * Handles restaurant-specific operations, customer management, and validation errors.
 * </p>
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

  /**
   * Creates an error response entity for exceptions with a predefined {@link CustomErrorMessage}.
   *
   * @param errorMessage the predefined custom error message
   * @param e            the thrown exception
   * @param request      the current HTTP request
   * @return a structured {@link ResponseEntity} containing {@link ErrorDetails}
   */
  private ResponseEntity<ErrorDetails> buildErrorResponse(CustomErrorMessage errorMessage, Exception e, HttpServletRequest request) {
    ErrorDetails errorDetails = ErrorDetails.builder()
        .timestamp(LocalDateTime.now())
        .status(errorMessage.getStatus())
        .error(HttpStatus.valueOf(errorMessage.getStatus()).getReasonPhrase())
        .message(errorMessage.getMessage())
        .path(request.getRequestURI())
        .exceptionType(e.getClass().getSimpleName())
        .build();
    return ResponseEntity.status(errorMessage.getStatus()).body(errorDetails);
  }

  /**
   * Creates an error response entity for generic exceptions with dynamic {@link HttpStatus}.
   *
   * @param status  the HTTP status to be returned
   * @param e       the thrown exception
   * @param request the current HTTP request
   * @return a structured {@link ResponseEntity} containing {@link ErrorDetails}
   */
  private ResponseEntity<ErrorDetails> buildErrorResponse(HttpStatus status, Exception e, HttpServletRequest request) {
    ErrorDetails errorDetails = ErrorDetails.builder()
        .timestamp(LocalDateTime.now())
        .status(status.value())
        .error(status.getReasonPhrase())
        .message(e.getMessage())
        .path(request.getRequestURI())
        .exceptionType(e.getClass().getSimpleName())
        .build();
    return ResponseEntity.status(status).body(errorDetails);
  }

  /**
   * Handles {@link MethodArgumentNotValidException} for validation errors from @Valid annotations.
   * <p>
   * Processes field-level validation failures and returns detailed error information
   * for each failed validation constraint.
   * </p>
   *
   * @param ex      the thrown validation exception
   * @param request the current HTTP request
   * @return a structured {@link ResponseEntity} containing {@link ErrorDetails} with validation errors
   */
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorDetails> handleValidationErrors(@NonNull MethodArgumentNotValidException ex, HttpServletRequest request) {
    Map<String, String> validationErrors = new HashMap<>();
    ex.getBindingResult().getAllErrors().forEach(error -> {
      if (error instanceof FieldError fieldError) {
        validationErrors.put(fieldError.getField(), error.getDefaultMessage());
      } else {
        validationErrors.put(error.getObjectName(), error.getDefaultMessage());
      }
    });

    ErrorDetails errorDetails = ErrorDetails.builder()
        .timestamp(LocalDateTime.now())
        .status(HttpStatus.BAD_REQUEST.value())
        .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
        .message("Validation failed for one or more fields")
        .path(request.getRequestURI())
        .exceptionType(ex.getClass().getSimpleName())
        .validationErrors(validationErrors)
        .build();

    return ResponseEntity.badRequest().body(errorDetails);
  }

  /**
   * Handles {@link ResourceNotFoundException} when a requested resource is not found.
   * <p>
   * Typically occurs when attempting to retrieve customers or other entities
   * that do not exist in the system.
   * </p>
   *
   * @param ex      the thrown exception
   * @param request the current HTTP request
   * @return a structured {@link ResponseEntity} containing {@link ErrorDetails}
   */
  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<ErrorDetails> handleResourceNotFound(@NonNull ResourceNotFoundException ex, HttpServletRequest request) {
    return buildErrorResponse(HttpStatus.NOT_FOUND, ex, request);
  }

  /**
   * Handles {@link DataIntegrityViolationException} for database constraint violations.
   * <p>
   * Detects specific constraint violations such as duplicate email addresses
   * or phone numbers and maps them to appropriate error messages.
   * </p>
   *
   * @param ex      the thrown exception
   * @param request the current HTTP request
   * @return a structured {@link ResponseEntity} containing {@link ErrorDetails}
   */
  @ExceptionHandler(DataIntegrityViolationException.class)
  public ResponseEntity<ErrorDetails> handleConstraintViolations(@NonNull DataIntegrityViolationException ex, HttpServletRequest request) {
    String message = ex.getMessage().toLowerCase();

    if (message.contains("email")) {
      return buildErrorResponse(CustomErrorMessage.DUPLICATE_EMAIL, ex, request);
    } else if (message.contains("phone")) {
      return buildErrorResponse(CustomErrorMessage.DUPLICATE_PHONE, ex, request);
    }

    return buildErrorResponse(HttpStatus.CONFLICT, ex, request);
  }

  /**
   * Handles {@link IllegalArgumentException} for invalid method arguments.
   * <p>
   * Occurs when methods are called with invalid or inappropriate arguments
   * that don't meet the expected criteria.
   * </p>
   *
   * @param ex      the thrown exception
   * @param request the current HTTP request
   * @return a structured {@link ResponseEntity} containing {@link ErrorDetails}
   */
  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ErrorDetails> handleIllegalArgumentException(@NonNull IllegalArgumentException ex, HttpServletRequest request) {
    return buildErrorResponse(HttpStatus.BAD_REQUEST, ex, request);
  }

  /**
   * Handles {@link IllegalStateException} for invalid state operations.
   * <p>
   * Occurs when operations are attempted on objects that are in an
   * inappropriate state for the requested operation.
   * </p>
   *
   * @param ex      the thrown exception
   * @param request the current HTTP request
   * @return a structured {@link ResponseEntity} containing {@link ErrorDetails}
   */
  @ExceptionHandler(IllegalStateException.class)
  public ResponseEntity<ErrorDetails> handleIllegalStateException(@NonNull IllegalStateException ex, HttpServletRequest request) {
    return buildErrorResponse(HttpStatus.CONFLICT, ex, request);
  }

  /**
   * Handles any unhandled exceptions that are not caught by specific handlers.
   * <p>
   * Serves as a catch-all to ensure that all exceptions result in a
   * consistent error response format. Returns a generic internal server error.
   * </p>
   *
   * @param ex      the thrown exception
   * @param request the current HTTP request
   * @return a structured {@link ResponseEntity} containing {@link ErrorDetails}
   */
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorDetails> handleGenericException(@NonNull Exception ex, HttpServletRequest request) {
    return buildErrorResponse(CustomErrorMessage.INTERNAL_SERVER_ERROR, ex, request);
  }
}