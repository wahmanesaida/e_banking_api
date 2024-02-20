package com.ecommerce.api.TransferMoney.utils;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Service
public class TransferUtils {
    //EMISSION D’UN TRANSFERT PAR DEBIT DE COMPTE DU CLIENT DEPUIS LA CONSOLE AGENT
    public BigDecimal transfertMax1= BigDecimal.valueOf(2000.00);
    public  BigDecimal fraiDuTransfert=BigDecimal.valueOf(20);
    public BigDecimal notificationCosts= BigDecimal.valueOf(1);
    public  BigDecimal MinTotal=BigDecimal.valueOf(100);



}
