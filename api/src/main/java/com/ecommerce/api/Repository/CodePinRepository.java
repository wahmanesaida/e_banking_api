package com.ecommerce.api.Repository;

import com.ecommerce.api.Entity.CodePin;
import com.ecommerce.api.Entity.TransferStatus;
import com.ecommerce.api.Entity.Transfert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CodePinRepository extends JpaRepository<CodePin, Long> {
    @Query("SELECT c FROM CodePin c WHERE c.username= :username AND c.transfer= :transfer")
    Optional<CodePin> findcodepinbyUsernameTransfer(@Param("username") String username, @Param("transfer")Transfert transfer);





}
