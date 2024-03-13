package com.ecommerce.api.ServingTransfer.Dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransferPaymentDto {

    private TransferRefDTO transferRefDTO;
    private BeneficiaryDto beneficiaryDto;

}
