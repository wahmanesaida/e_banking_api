package com.ecommerce.api.TransferMoney.Controller;

import com.ecommerce.api.Entity.Transfert;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeleteCodeRequest {
    String username;
    long id;
}
