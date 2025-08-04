package com.restaurantcrm.restaurant_crm_backend.exception;

import lombok.Getter;

@Getter
public enum CustomErrorMessage {
  CUSTOMER_NOT_FOUND(404, "Customer not found"),
  DUPLICATE_EMAIL(409, "Email already exists"),
  DUPLICATE_PHONE(409, "Phone number already exists"),
  INTERNAL_SERVER_ERROR(500, "An internal server error occurred"),;

  private final int status;
  private final String message;

  CustomErrorMessage(int status, String message) {
    this.status = status;
    this.message = message;
  }
}
