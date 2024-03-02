package com.ecommerce.api.TransferMoney.Controller;

import java.math.BigDecimal;

import com.ecommerce.api.Entity.User;
import com.ecommerce.api.TransferMoney.dto.TransfertDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CheckAmountRequest {
    TransfertDto transfertDto;
    User user;
    BigDecimal checkAmount;


}
