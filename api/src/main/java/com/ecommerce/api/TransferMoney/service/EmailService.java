package com.ecommerce.api.TransferMoney.service;

import org.springframework.http.ResponseEntity;

import com.ecommerce.api.TransferMoney.dto.MailStructure;

public interface EmailService {
    String generateOTP();

    void sendMail(MailStructure mailStructure);

    void sendOtpEmail(String toEmail, String otp);

    ResponseEntity<String> sendOTP(String email);

    ResponseEntity<String> validateOTP(String email, String otp);

}
