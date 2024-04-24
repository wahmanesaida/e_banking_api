package com.ecommerce.api.TransferMoney.Response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BeneficiaryResponseDTO {
    private long id;
    private String firstName;
    private String lastName;
    private String Username;
    private String GSM;
}
