package com.ecommerce.api.TransferMoney.Controller;

import com.ecommerce.api.TransferMoney.dto.MailStructure;
import com.ecommerce.api.TransferMoney.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<String> sendOTP(@RequestParam String email) {
        return emailService.sendOTP(email);
        
    }

    @PostMapping("/validate-otp")
    public ResponseEntity<String> validateOTP(@RequestParam String email, @RequestParam String otp) {
        return emailService.validateOTP(email, otp);
       
    }



}
