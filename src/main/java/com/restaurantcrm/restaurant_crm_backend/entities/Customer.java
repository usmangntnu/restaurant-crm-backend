package com.restaurantcrm.restaurant_crm_backend.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
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


}
