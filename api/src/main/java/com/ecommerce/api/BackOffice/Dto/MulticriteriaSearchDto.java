package com.ecommerce.api.BackOffice.Dto;

import java.time.LocalDateTime;

import com.ecommerce.api.Entity.Type_transfer;
import com.ecommerce.api.Entity.User;

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
    private Type_transfer typeTransfer;
    private String status;
    private LocalDateTime createTime;

}
