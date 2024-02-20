package com.ecommerce.api.TransferMoney.Controller;

import com.ecommerce.api.TransferMoney.dto.BeneficiaryDto;
import com.ecommerce.api.TransferMoney.dto.TransfertDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.bind.annotation.RequestBody;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransferRequest {
    TransfertDto transfertDto;
    long id_beneficiary;
    long Id_user;
    BeneficiaryDto bene;
}
