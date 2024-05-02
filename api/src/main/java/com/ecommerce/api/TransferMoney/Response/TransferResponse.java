package com.ecommerce.api.TransferMoney.Response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransferResponse {
    private MessageResponse message;
    private byte[] receiptPdf;

}
