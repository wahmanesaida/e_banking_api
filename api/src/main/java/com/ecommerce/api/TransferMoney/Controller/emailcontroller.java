package com.ecommerce.api.TransferMoney.Controller;

import com.ecommerce.api.TransferMoney.ServiceImp.EmailServiceImp;
import com.ecommerce.api.TransferMoney.dto.EmailDetails;
import com.ecommerce.api.TransferMoney.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth/")
public class emailcontroller {

    @Autowired
    private EmailService emailService;

    @PostMapping("/send")
    public String sendEmail(@RequestBody EmailDetails emailDetails) {
        emailService.SendEmailAlert(emailDetails);
        return "Email sent successfully";
    }
}
