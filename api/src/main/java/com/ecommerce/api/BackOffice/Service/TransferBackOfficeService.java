package com.ecommerce.api.BackOffice.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.ecommerce.api.Entity.Transfert;
import com.ecommerce.api.Entity.Type_transfer;
import com.ecommerce.api.Entity.User;

@Service
public interface TransferBackOfficeService {

    public Optional<Transfert> findByTransferRef(String transferRef);
    public List<Transfert> findByClient(User codeClient);
    public List<Transfert> findByAgent(User codeAgent);
    public List<Transfert> findByTypeTransfer(Type_transfer typeTransfer);
    public List<Transfert> findByStatus(String status) ;
    public List<Transfert> findByCreateTime(LocalDateTime createTime);



        

}
