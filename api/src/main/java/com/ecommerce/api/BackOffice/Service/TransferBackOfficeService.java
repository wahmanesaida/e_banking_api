package com.ecommerce.api.BackOffice.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.ecommerce.api.Entity.TransferStatus;
import com.ecommerce.api.Entity.Transfert;
import com.ecommerce.api.Entity.TypeTransfer;
import com.ecommerce.api.Entity.User;

@Service
public interface TransferBackOfficeService {

    public Optional<Transfert> findByTransferRef(String transferRef);
    public List<Transfert> findByClient(User codeClient);
    public List<Transfert> findByAgent(User codeAgent);
    public List<Transfert> findByTypeTransfer(TypeTransfer type_transfer);
    public List<Transfert> findByStatus(TransferStatus status) ;
    public List<Transfert> findByCreateTime(LocalDateTime createTime);

}
