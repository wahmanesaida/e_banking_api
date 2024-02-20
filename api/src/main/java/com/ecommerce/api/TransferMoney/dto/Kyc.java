package com.ecommerce.api.TransferMoney.dto;

import com.ecommerce.api.Entity.Profession;
import com.ecommerce.api.Entity.Title;
import com.ecommerce.api.Entity.TypePieceIdentite;
import com.ecommerce.api.Entity.Ville;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Kyc {
    private Title title;
    private TypePieceIdentite pieceIdentite;
    private String paysEmission ;
    private String numeroPieceIdentite;
    private Date expirationPieceIdentite;
    private Date validitePieceIdentite;
    private LocalDate datenaissance;
    private Profession profession;
    private String payeNationale;
    private Ville ville;
    private String GSM;
    private BigDecimal account_amount;

}
