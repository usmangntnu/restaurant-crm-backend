package com.restaurantcrm.restaurant_crm_backend.entities;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Status indicating whether a customer is a Michelin inspector or not")
public enum MichelinStatus {

  @Schema(description = "Regular customer – not a Michelin inspector")
  REGULAR,

  @Schema(description = "Suspicious customer – may be a Michelin inspector")
  SUSPICIOUS,

  @Schema(description = "Confirmed Michelin inspector")
  INSPECTOR
}
