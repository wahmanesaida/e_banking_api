package com.ecommerce.api.ServingTransfer.Controller;

import java.io.IOException;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecommerce.api.Entity.Transfert;
import com.ecommerce.api.Entity.User;
import com.ecommerce.api.Repository.UserRepository;
import com.ecommerce.api.ServingTransfer.Dto.BeneficiaryDto;
import com.ecommerce.api.ServingTransfer.Dto.TransferPaymentDto;
import com.ecommerce.api.ServingTransfer.Dto.TransferRefDTO;
import com.ecommerce.api.ServingTransfer.Service.ServingTransfer;
import com.lowagie.text.DocumentException;

import jakarta.servlet.http.HttpServletResponse;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/v1/auth/")
public class ShowTransfer {

    @Autowired
    private ServingTransfer servingTransfer;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepo;

    @PostMapping("/showTransfer")
    public ResponseEntity<?> searchTransfer(@RequestBody TransferRefDTO transferRefDTO) {
        try {
            Transfert transfert = servingTransfer.searchTransfer(transferRefDTO);
            if (transfert != null) {
                return ResponseEntity.ok(transfert);
            } else {
                return ResponseEntity.notFound().build();
            }
            
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            
        } catch (Exception ex){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
        }
    }

    @PostMapping("/addKycBeneficiary")
    public ResponseEntity enterBeneficiaryInformation(@RequestBody BeneficiaryDto beneficiaryDto) {
        servingTransfer.enterBeneficiaryInformation(beneficiaryDto);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/users")
    public ResponseEntity<User> createUser(@RequestBody User user) {
        // Encode the password
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        // Set the encoded password back to the user object
        user.setPassword(encodedPassword);

        // Save the user
        User newUser = userRepo.save(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(newUser);
    }

    @PostMapping("/validateTransfer")
    public ResponseEntity<?> validatePayment(@RequestBody TransferPaymentDto transferPaymentDto,
            HttpServletResponse response) {
        try {
            servingTransfer.validatePayment(transferPaymentDto, response);
            return ResponseEntity.ok().build();
        } catch (NoSuchElementException | IllegalStateException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred");
        }
    }

    @PostMapping("/generatePaymentReciept")
    public void generateReceipt(@RequestBody TransferPaymentDto transferPaymentDto, HttpServletResponse response) {
        try {
            servingTransfer.generatePaymentReceipt(transferPaymentDto, response);
        } catch (IOException | DocumentException e) {
            e.printStackTrace(); 
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }


}
