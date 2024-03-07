package com.ecommerce.api.ServingTransfer.Dto;

import java.math.BigDecimal;

import com.ecommerce.api.Entity.Type_transfer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransferRefDTO {

    private Long id;

    private Long idAgent;

    private String transferRef;

    private BigDecimal amount_transfer;

    private Type_transfer typeOftransfer;
    
    private Long idClient;

    public String getTransferRef() {
        return transferRef;
    }

    public void setTransferRef(String transferRef) {
        this.transferRef = transferRef;
    }

}
