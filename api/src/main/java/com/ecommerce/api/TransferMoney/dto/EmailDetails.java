package com.ecommerce.api.TransferMoney.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class EmailDetails {
    private String recipient;
    private  String message;
    private  String subject;
    private String attachment;
}
