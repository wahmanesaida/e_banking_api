package com.ecommerce.api.TransferMoney.dto;

import com.ecommerce.api.Entity.TypeOfFees;
import com.ecommerce.api.Entity.TypePieceIdentite;
import com.ecommerce.api.Entity.Type_transfer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class TransfertDto {
    private Long id;
    private BigDecimal amount_entred;
    private BigDecimal amount_transfer;
    private boolean notification;
    private String generateRef;
    private TypeOfFees fees;
    private BigDecimal amount_total;
    private Type_transfer typeOftransfer;
    private String status;
    private LocalDateTime createTime;
    private Long id_bene;
    private TypePieceIdentite typePieceIdentite;
    private String numeroPieceIdentite;
    private long id_agent;
}
