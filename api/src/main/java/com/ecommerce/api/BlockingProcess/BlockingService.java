package com.ecommerce.api.BlockingProcess;

import com.ecommerce.api.Entity.TransferStatus;
import com.ecommerce.api.Entity.Transfert;
import com.ecommerce.api.TransferMoney.Response.MessageResponse;
import org.springframework.stereotype.Service;

@Service
public interface BlockingService {
    MessageResponse blockTransfer(String reference, TransferStatus status);
}
