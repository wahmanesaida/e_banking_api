package com.ecommerce.api.Repository;

import com.ecommerce.api.Entity.TransferStatus;
import com.ecommerce.api.Entity.Transfert;
import com.ecommerce.api.Entity.TypeTransfer;
import com.ecommerce.api.Entity.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TransfertRepository extends JpaRepository<Transfert, Long> {

    Optional<Transfert> findByTransferRef(String transferRef);
    List<Transfert> findByClient(User codeAgentClient);
    List<Transfert> findByAgent(User codeAgentClient);
    List<Transfert> findByTypeOftransfer(TypeTransfer typeOftransfer);
    List<Transfert> findByStatus(TransferStatus status);
    List<Transfert> findByCreateTime(LocalDateTime createTime);
 
}
