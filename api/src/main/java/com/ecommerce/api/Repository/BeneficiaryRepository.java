package com.ecommerce.api.Repository;

import com.ecommerce.api.Entity.Beneficiary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BeneficiaryRepository extends JpaRepository<Beneficiary, Long> {
    Optional<Beneficiary> findBeneficiaryByUsername(String username);

}
