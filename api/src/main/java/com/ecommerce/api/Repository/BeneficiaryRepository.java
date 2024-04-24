package com.ecommerce.api.Repository;

import com.ecommerce.api.Entity.Beneficiary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BeneficiaryRepository extends JpaRepository<Beneficiary, Long> {
    Optional<Beneficiary> findBeneficiaryByUsername(String username);
    @Query("SELECT b FROM Beneficiary b where b.client.id= :clientId")
    List<Beneficiary> findBeneficiariesByClientId(@Param("clientId") long clientId);

}
