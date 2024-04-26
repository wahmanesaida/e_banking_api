package com.ecommerce.api.BackOffice.Dto;

import java.time.LocalDateTime;

import com.ecommerce.api.Entity.TransferStatus;
import com.ecommerce.api.Entity.TypeTransfer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MulticriteriaSearchDto {

    private String transferRef;
    private Long codeClient;
    private Long codeAgent;
    private TypeTransfer typeOftransfer;
    private TransferStatus status;
    private LocalDateTime createTime;
}
