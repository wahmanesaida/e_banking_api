package com.ecommerce.api.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ecommerce.api.Entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    // now we will update the role of  users 
    @Modifying
    @Query("update User set role = :role where username= :username")
    public void UpdateUserRole(@Param("username") String username, @Param("role") com.ecommerce.api.Entity.Role admin);
    
}
