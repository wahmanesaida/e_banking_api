package com.ecommerce.api.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ecommerce.api.Entity.Otp;

public interface OtpRepository extends JpaRepository<Otp, Long>  {

    Optional<Otp> findByUsername(String username);

}
