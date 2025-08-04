package com.restaurantcrm.restaurant_crm_backend.exception;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Represents the details of an error response returned from the restaurant CRM backend.
 * <p>
 * Contains information such as timestamp, HTTP status code and message, exception type,
 * detailed error message, and the request path.
 * Used to provide clients with clear error information.
 * </p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorDetails {

  /**
   * The timestamp when the error occurred.
   */
  private LocalDateTime timestamp;

  /**
   * The HTTP status code associated with the error.
   */
  private int status;

  /**
   * The HTTP status message associated with the error.
   */
  private String error;

  /**
   * A detailed error message describing the issue.
   */
  private String message;

  /**
   * The path of the request that triggered the error.
   */
  private String path;

  /**
   * The type of exception that was thrown.
   */
  private String exceptionType;

  /**
   * Map of field-level validation errors (field name -> error message).
   * Only populated for validation errors.
   */
  private Map<String, String> validationErrors;
}