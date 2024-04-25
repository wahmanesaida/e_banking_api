package com.ecommerce.api.Repository;

import com.ecommerce.api.Entity.Transfert;
import com.ecommerce.api.Entity.Type_transfer;
import com.ecommerce.api.Entity.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TransfertRepository extends JpaRepository<Transfert, Long> {
    
    Optional<Transfert> findByTransferRef(String transferRef);
    List<Transfert> findByClient(User codeAgentClient);
    List<Transfert> findByAgent(User codeAgentClient);
    List<Transfert> findByTypeTransfer(Type_transfer typeTransfer);
    List<Transfert> findByStatus(String status);
    List<Transfert> findByCreateTime(LocalDateTime createTime);
}
