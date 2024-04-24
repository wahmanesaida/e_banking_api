package com.ecommerce.api.ServingTransfer.Dto;

import java.time.LocalDate;
import java.util.Date;

import com.ecommerce.api.Entity.Profession;
import com.ecommerce.api.Entity.Title;
import com.ecommerce.api.Entity.TypePieceIdentite;
import com.ecommerce.api.Entity.Ville;

import jakarta.persistence.Column;
import jakarta.persistence.Enumerated;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BeneficiaryDto {

    private Long id;
    private Title title;
    private TypePieceIdentite pieceIdentite;
    private String paysEmission;
    private String numeroPieceIdentite;
    private LocalDate expirationPieceIdentite;
    private LocalDate validitePieceIdentite;
    private LocalDate datenaissance;
    private Profession profession;
    private String payeNationale;
    private Ville ville;
}
