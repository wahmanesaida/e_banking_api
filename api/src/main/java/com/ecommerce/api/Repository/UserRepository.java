package com.ecommerce.api.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;
import java.util.Optional;

import com.ecommerce.api.Entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByNumeroPieceIdentite(String numeroPieceIdentite);
    Optional<User> findByName(String name);

     //now we will update the role of  users 
   @Modifying
    @Query("update User set title= :title, pieceIdentite= :pieceIdentite, numeroPieceIdentite = :numeroPieceIdentite, profession = :profession, ville= :ville, GSM = :GSM, account_amount = :account_amount  where username= :username")
     public void UpdateUserKYC(@Param("username") String username, @Param("title") Title title, @Param("pieceIdentite")TypePieceIdentite pieceIdentite, @Param("numeroPieceIdentite") String numeroPieceIdentite, @Param("profession")Profession profession, @Param("ville") Ville ville, @Param("GSM") String GSM, @Param("account_amount")BigDecimal account_amount);

    @Query("SELECT u FROM User u WHERE u.GSM = :phoneNumber")
    Optional<User> findUserByPhoneNumber(@Param("phoneNumber") String phoneNumber);

    @Query("SELECT a FROM User a WHERE a.id = :id_agent AND a.role = com.ecommerce.api.Entity.Role.AGENT")
    Optional<User> findAgent(@Param("id_agent") long id_agent);
    
    @Query("SELECT a FROM User a WHERE a.id = :id_client AND a.role = com.ecommerce.api.Entity.Role.USER")
    Optional<User> findClient(@Param("id_client") long id_client);
}
