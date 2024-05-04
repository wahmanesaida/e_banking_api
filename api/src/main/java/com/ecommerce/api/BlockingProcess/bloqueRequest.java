package com.ecommerce.api.BlockingProcess;

import com.ecommerce.api.Entity.TransferStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class bloqueRequest {
    private String reference;
    private TransferStatus status;
}
