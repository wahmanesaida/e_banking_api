package com.ecommerce.api.ManageUsers.Controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecommerce.api.BackOffice.Controller.TransferBackOfficeController;
import com.ecommerce.api.BackOffice.Service.TransferBackOfficeService;
import com.ecommerce.api.Entity.User;
import com.ecommerce.api.ManageUsers.Dto.UserDto;
import com.ecommerce.api.Repository.UserRepository;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/v1/auth/")
public class ManageUsersController {


    @Autowired
    private TransferBackOfficeService transfertService;

    @Autowired
    private UserRepository userRepository;

        private static final Logger logger = LoggerFactory.getLogger(TransferBackOfficeController.class);



    @Autowired
    private PasswordEncoder passwordEncoder;



    @GetMapping("/getAllUsers")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = transfertService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @PostMapping("/deleteUser")
    public void deleteUser(@RequestBody Long id) {
        userRepository.deleteById(id);
    }

    @PostMapping("/searchUserByName")
    public User searchUser(@RequestBody String name) {
        Optional<User> userOptional = userRepository.findByName(name);
        User user = userOptional.orElseThrow(() -> new IllegalArgumentException("User not found with name: " + name));
        return user;
    }

    @PatchMapping("/updateUserInfo")
    public ResponseEntity<?> updateUserProperty(@RequestBody UserDto userDto) {
        User user = userRepository.findById(userDto.getId())
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userDto.getId()));

        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }
        if (userDto.getTitle() != null) {
            user.setTitle(userDto.getTitle());
        }
        if (userDto.getRole() != null) {
            user.setRole(userDto.getRole());
        }
        if (userDto.getAccount_amount() != null) {
            user.setAccount_amount(userDto.getAccount_amount());
        }
        if (userDto.getCreateTime() != null) {
            user.setCreateTime(userDto.getCreateTime());
        }
        if (userDto.getUsername() != null) {
            user.setUsername(userDto.getUsername());
        }
        if (userDto.getGSM() != null) {
            user.setGSM(userDto.getGSM());
        }
        if (userDto.getDatenaissance() != null) {
            user.setDatenaissance(userDto.getDatenaissance());
        }
        if (userDto.getExpirationPieceIdentite() != null) {
            user.setExpirationPieceIdentite(userDto.getExpirationPieceIdentite());
        }
        if (userDto.getNumeroPieceIdentite() != null) {
            user.setNumeroPieceIdentite(userDto.getNumeroPieceIdentite());
        }
        if (userDto.getPayeNationale() != null) {
            user.setPayeNationale(userDto.getPayeNationale());
        }
        if (userDto.getPaysEmission() != null) {
            user.setPaysEmission(userDto.getPaysEmission());
        }
        if (userDto.getProfession() != null) {
            user.setProfession(userDto.getProfession());
        }
        if (userDto.getVille() != null) {
            user.setVille(userDto.getVille());
        }
        if (userDto.getPieceIdentite() != null) {
            user.setPieceIdentite(userDto.getPieceIdentite());
        }
        if (userDto.getValiditePieceIdentite() != null) {
            user.setValiditePieceIdentite(userDto.getValiditePieceIdentite());
        }

        userRepository.save(user);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/addUserIntoDb")
    public ResponseEntity<?> addUser(@RequestBody UserDto userDto) {
        String encodedPassword = passwordEncoder.encode(userDto.getPassword());
        User user = User.userBuilder()
                .username(userDto.getUsername())
                .password(encodedPassword)
                .name(userDto.getName())
                .createTime(LocalDateTime.now())
                .role(userDto.getRole())
                .title(userDto.getTitle())
                .pieceIdentite(userDto.getPieceIdentite())
                .paysEmission(userDto.getPaysEmission())
                .numeroPieceIdentite(userDto.getNumeroPieceIdentite())
                .expirationPieceIdentite(userDto.getExpirationPieceIdentite())
                .validitePieceIdentite(userDto.getValiditePieceIdentite())
                .datenaissance(userDto.getDatenaissance())
                .profession(userDto.getProfession())
                .payeNationale(userDto.getPayeNationale())
                .ville(userDto.getVille())
                .GSM(userDto.getGSM())
                .account_amount(userDto.getAccount_amount())
                .build();

        userRepository.save(user);
        return ResponseEntity.ok().build();
    }



    @PostMapping("/adminProfile")
    public ResponseEntity<?> getAdminProfile(@RequestBody long id) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();              
                return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.notFound().build(); 
        }
    }


   /*  @PostMapping("/adminProfile")
    public ResponseEntity<?> getAdminProfile(@RequestBody long id) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            logger.info("this is the role :",user);
            if (user.getRole().equals("ADMIN") || user.getRole().equals("AGENT") || user.getRole().equals("SYSTEM_MANAGER")) {                
                return ResponseEntity.ok(user);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("you are unauthorized to access this page");
            }
        } else {
            return ResponseEntity.notFound().build(); 
        }
    } */
    







}
