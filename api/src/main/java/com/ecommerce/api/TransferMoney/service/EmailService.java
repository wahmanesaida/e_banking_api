package com.ecommerce.api.TransferMoney.service;

import com.ecommerce.api.TransferMoney.Response.MessageResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.ecommerce.api.TransferMoney.dto.MailStructure;


@Service
public interface EmailService {
    String generateOTP();

    void sendMail(MailStructure mailStructure);

    void sendOtpEmail(String toEmail, String otp);

   /*  void deleteExpiredOtpsScheduler(); */

    MessageResponse sendOTP(String email);

    MessageResponse validateOTP(String email, String otp);

}
