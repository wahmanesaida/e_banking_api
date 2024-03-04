package com.ecommerce.api.Repository;

import com.ecommerce.api.Entity.Transfert;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TransfertRepository extends JpaRepository<Transfert, Long> {

    Optional<Transfert> findByTransferRef(String transferRef);
}
