package com.ecommerce.api.Entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Beneficiary {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "firstName")
    private String firstName;

    @Column(name = "lastname")
    private  String lastname;

    @Column(name = "account_amount")
    private BigDecimal account_amount;

    @Column(name = "GSM")
    private String GSM;

    @Column(name = "username")
    private String username;

    @ManyToOne
    @JoinColumn(name = "client_id")
    @JsonBackReference
    private User client;
}
