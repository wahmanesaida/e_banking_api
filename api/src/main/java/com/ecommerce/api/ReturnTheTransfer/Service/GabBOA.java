package com.ecommerce.api.ReturnTheTransfer.Service;

import com.ecommerce.api.Entity.Motif;
import com.ecommerce.api.Entity.Transfert;
import com.ecommerce.api.ReturnTheTransfer.DTO.ReturnTransferDTO;
import com.ecommerce.api.TransferMoney.Response.MessageResponse;
import org.springframework.stereotype.Service;

@Service
public interface GabBOA {
    Transfert AccessTheTransactionByRef(String transferRef);
    MessageResponse ReturnTransfer(ReturnTransferDTO returnTransferDTO);
}
