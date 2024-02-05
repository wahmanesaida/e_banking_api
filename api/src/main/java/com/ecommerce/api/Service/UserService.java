package com.ecommerce.api.Service;

import java.util.Optional;

import com.ecommerce.api.Entity.User;

public interface UserService {
    User SaveUser(User user);
    Optional<User> findByUsername(String username);
    void MakeAdmin(String username);
    
}
