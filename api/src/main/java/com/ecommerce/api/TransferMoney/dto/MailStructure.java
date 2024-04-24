package com.ecommerce.api.TransferMoney.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MailStructure {
    private String recipient;
    private String subject;
    private String message;

}
