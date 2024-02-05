package com.ecommerce.api.Service;
import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ecommerce.api.Entity.Role;
import com.ecommerce.api.Entity.User;
import com.ecommerce.api.Repository.UserRepository;

@Service
public class UserServiceImp implements UserService {

    @Autowired
    private UserRepository userRepo;  // this userRepo contain all crud operations

    @Autowired
    private PasswordEncoder passwordEncoder; 
    @Override
    public User SaveUser(User user) {
        // encode the password in a random characters
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        //set the default role : user
        user.setRole(Role.USER);
        user.setCreateTime(LocalDateTime.now());
        return userRepo.save(user);
    }

    @Override
    public Optional<User> findByUsername(String username) {
       return userRepo.findByUsername(username);
    }

    @Transactional
    @Override
    public void MakeAdmin(String username) {
         userRepo.UpdateUserRole(username, Role.ADMIN);
         //username is email 
    }
    
}
