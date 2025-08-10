package com.restaurantcrm.restaurant_crm_backend.exception;

import lombok.Getter;

/**
 * Enum representing custom error messages and their associated HTTP status codes
 * for the restaurant CRM backend.
 * <p>
 * Used throughout the application to provide consistent error handling and
 * responses for restaurant-specific operations and customer management.
 * Each constant defines both a status code and a descriptive message.
 * </p>
 */
@Getter
public enum CustomErrorMessage {

  /**
   * Error when a customer is not found in the system.
   */
  CUSTOMER_NOT_FOUND(404, "Customer not found"),

  /**
   * Error when attempting to register a customer with an email that already exists.
   */
  DUPLICATE_EMAIL(409, "Email already exists"),

  /**
   * Error when attempting to register a customer with a phone number that already exists.
   */
  DUPLICATE_PHONE(409, "Phone number already exists"),

  /**
   * Error when an internal server error occurs during request processing.
   */
  INTERNAL_SERVER_ERROR(500, "An internal server error occurred");

  /**
   * The HTTP status code associated with the error.
   */
  private final int status;

  /**
   * The descriptive error message.
   */
  private final String message;

  /**
   * Constructor for CustomErrorMessage.
   *
   * @param status  the HTTP status code
   * @param message the descriptive error message
   */
  CustomErrorMessage(int status, String message) {
    this.status = status;
    this.message = message;
  }
}