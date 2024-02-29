package com.ecommerce.api.TransferMoney.Controller;

import com.ecommerce.api.TransferMoney.Response.MessageResponse;
import com.ecommerce.api.TransferMoney.dto.MailStructure;
import com.ecommerce.api.TransferMoney.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/v1/auth")
public class emailcontroller {

    @Autowired
    private EmailService emailService;

    @PostMapping("/send/{mail}")
    public String sendMail(@RequestBody MailStructure mailStructure){
        emailService.sendMail(mailStructure);
        return "Successfully sent the mail";

    }



    @PostMapping("/send-otp")
    public ResponseEntity<MessageResponse> sendOTP(@RequestBody String email) {
        try {
            MessageResponse sendotp= emailService.sendOTP(email);
            return ResponseEntity.ok(sendotp);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new MessageResponse("Internal server error"));
        }

    }

    @PostMapping("/validate-otp")
    public ResponseEntity<MessageResponse> validateOTP(@RequestBody validateOtpRequest request) {
        try {
            MessageResponse validate= emailService.validateOTP(request.getEmail(), request.getOtp());
            return ResponseEntity.ok(validate);

        }catch (Exception e){
            return ResponseEntity.internalServerError().body(new MessageResponse("Internal server error"));
        }

    }



}
