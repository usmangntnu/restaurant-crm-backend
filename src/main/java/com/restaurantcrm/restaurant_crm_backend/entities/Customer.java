package com.restaurantcrm.restaurant_crm_backend.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.Accessors;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@Table(name = "customer")
public class Customer {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotBlank(message = "Navn må oppgis")
  @Size(max = 100)
  @Column(nullable = false)
  private String name;

  @NotBlank(message = "Telefonnummer må oppgis")
  @Size(max = 30)
  @Column(nullable = false)
  private String phone;

  @Email(message = "Ugyldig e-postadresse")
  @NotBlank(message = "E-post må oppgis")
  @Size(max = 100)
  @Column(nullable = false, unique = false) // Sett ev. unique=true hvis det er krav
  private String email;

  @Size(max = 255)
  private String allergies;

  @Column(nullable = false)
  private int visitCount = 0;

  @Size(max = 1000)
  private String notes;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private MichelinStatus michelinStatus = MichelinStatus.REGULAR;
}