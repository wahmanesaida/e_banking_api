package com.ecommerce.api.GabBoa.Dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransferRefDTO {

    private String transferRef;
    private String codepin;

}
