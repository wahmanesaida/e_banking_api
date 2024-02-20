package com.ecommerce.api.TransferMoney.dto;

import com.ecommerce.api.Entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BeneficiaryDto {
    private String firstName;
    private String lastname;
    private String GSM;
    private String username;
}
