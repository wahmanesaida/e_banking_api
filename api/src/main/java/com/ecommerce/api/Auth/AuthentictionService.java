package com.ecommerce.api.Auth;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.ecommerce.api.Config.JwtService;
import com.ecommerce.api.Entity.Role;
import com.ecommerce.api.Entity.User;
import com.ecommerce.api.Repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthentictionService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    // this method allow us to create user save it to the database and return the token
    public AuthenticationResponse register(RegisterRequest request){
        var user=User.userBuilder()
            .username(request.getUsername())
            .name(request.getName())
            .password(passwordEncoder.encode(request.getPassword()))
           
            .role(Role.USER)
            .build();
        userRepository.save(user);
       var  jwtToken= jwtService.generateToken(user);
       
        return AuthenticationResponse.builder()
            .token(jwtToken)
            .build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request){
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                request.getUsername(),
                request.getPassword()
            )
        );
        var user=userRepository.findByUsername(request.getUsername()).orElseThrow();
        var jwttoken= jwtService.generateToken(user);
        return AuthenticationResponse.builder()
            .id(user.getId())
            .name(user.getName())
            .role(user.getRole())
            .token(jwttoken)
            .build();


    }


    public AuthenticationResponse registerAdmin() {
        var admin = User.userBuilder()
            .username("admin@gmail.com")
            .name("Admin_Saida")
            .password(passwordEncoder.encode("admin"))  
            .role(Role.ADMIN)
            .build();
        userRepository.save(admin);
    
        var jwtToken = jwtService.generateToken(admin);
    
        return new AuthenticationResponse.AuthenticationResponseBuilder()
            .id(admin.getId())
            .name(admin.getName())
            .role(admin.getRole())
            .token(jwtToken)
            .build();
    }

    public AuthenticationResponse authenticateAdmin(AuthenticationRequest request) throws Exception {
    authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(
            request.getUsername(),
            request.getPassword()
        )
    );
    var user = userRepository.findByUsername(request.getUsername())
        .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    
    if (user.getRole() != Role.ADMIN) {
        throw new Exception("User is not an admin");
    }

    var jwtToken = jwtService.generateToken(user);

    return new AuthenticationResponse.AuthenticationResponseBuilder()
        .id(user.getId())
        .name(user.getName())
        .role(user.getRole())
        .token(jwtToken)
        .build();
}

   
    
}
