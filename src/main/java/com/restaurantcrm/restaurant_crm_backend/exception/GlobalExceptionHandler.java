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
 * Global exception handler for REST API errors in the restaurant CRM backend.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

  /**
   * Creates an error response entity using predefined error messages.
   *
   * @param errorMessage the predefined custom error message
   * @param e            the thrown exception
   * @param request      the current HTTP request
   * @return a structured ResponseEntity containing ErrorDetails
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
   * Creates an error response entity using HTTP status.
   *
   * @param status  the HTTP status to be returned
   * @param e       the thrown exception
   * @param request the current HTTP request
   * @return a structured ResponseEntity containing ErrorDetails
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
   * Handles validation errors from @Valid annotations.
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
        .message("Validering feilet")
        .path(request.getRequestURI())
        .exceptionType(ex.getClass().getSimpleName())
        .validationErrors(validationErrors)
        .build();

    return ResponseEntity.badRequest().body(errorDetails);
  }

  /**
   * Handles ResourceNotFoundException when a requested resource is not found.
   */
  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<ErrorDetails> handleResourceNotFound(@NonNull ResourceNotFoundException ex, HttpServletRequest request) {
    return buildErrorResponse(CustomErrorMessage.CUSTOMER_NOT_FOUND, ex, request);
  }

  /**
   * Handles database constraint violations like duplicate email/phone.
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
   * Handles IllegalArgumentException for invalid method arguments.
   */
  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ErrorDetails> handleIllegalArgumentException(@NonNull IllegalArgumentException ex, HttpServletRequest request) {
    return buildErrorResponse(HttpStatus.BAD_REQUEST, ex, request);
  }

  /**
   * Handles IllegalStateException for invalid state operations.
   */
  @ExceptionHandler(IllegalStateException.class)
  public ResponseEntity<ErrorDetails> handleIllegalStateException(@NonNull IllegalStateException ex, HttpServletRequest request) {
    return buildErrorResponse(HttpStatus.CONFLICT, ex, request);
  }

  /**
   * Catch-all handler for any unhandled exceptions.
   */
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorDetails> handleGenericException(@NonNull Exception ex, HttpServletRequest request) {
    return buildErrorResponse(CustomErrorMessage.INTERNAL_SERVER_ERROR, ex, request);
  }
}