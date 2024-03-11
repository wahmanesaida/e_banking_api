package com.ecommerce.api.TransferMoney.Controller;

import com.ecommerce.api.TransferMoney.dto.BeneficiaryDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BeneficiaryRequest {
     BeneficiaryDto beneficiaryDto;
     long id_user;
}
