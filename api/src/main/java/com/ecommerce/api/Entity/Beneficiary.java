package com.ecommerce.api.Entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;

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


    @Enumerated(EnumType.STRING)
    @Column(name = "title")
    private Title title;

    @Enumerated(EnumType.STRING)
    @Column(name = "pieceIdentite")
    private TypePieceIdentite pieceIdentite;

    @Column(name = "paysEmission")
    private String paysEmission = "Maroc";

    @Column(name = "numeroPieceIdentite")
    private String numeroPieceIdentite;

    @Temporal(TemporalType.DATE)
    @Column(name = "expirationPieceIdentite")
    private Date expirationPieceIdentite;

    @Temporal(TemporalType.DATE)
    @Column(name = "validitePieceIdentite")
    private Date validitePieceIdentite;

    @Column(name = "datenaissance")
    private LocalDate datenaissance;

    @Enumerated(EnumType.STRING)
    @Column(name = "profession")
    private  Profession profession;

    @Column(name = "payeNationale")
    private String payeNationale="maroc";

    @Enumerated(EnumType.STRING)
    @Column(name = "ville")
    private Ville ville;

    @ManyToOne
    @JoinColumn(name = "client_id")
    @JsonBackReference
    private User client;
}
