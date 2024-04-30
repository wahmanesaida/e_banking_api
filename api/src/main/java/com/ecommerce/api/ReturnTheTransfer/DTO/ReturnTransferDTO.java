package com.ecommerce.api.ReturnTheTransfer.DTO;

import com.ecommerce.api.Entity.Motif;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class ReturnTransferDTO {

   private String transferRef;
   private Motif motif;
   private long id_agent;
   private boolean notification;
   private long client_id;
}
