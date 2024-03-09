package com.ecommerce.api.TransferMoney.ServiceImp;

import com.ecommerce.api.Entity.Otp;
import com.ecommerce.api.Repository.OtpRepository;
import com.ecommerce.api.TransferMoney.Response.MessageResponse;
import com.ecommerce.api.TransferMoney.dto.MailStructure;
import com.ecommerce.api.TransferMoney.service.EmailService;

import java.util.Optional;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImp implements EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private OtpRepository otpRepository;

    @Value("${spring.mail.username}")
    private String fromMail;

    @Override
    public void sendMail(MailStructure mailStructure) {

        try {

            SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
            simpleMailMessage.setFrom(fromMail);
            simpleMailMessage.setSubject(mailStructure.getSubject());
            simpleMailMessage.setText(mailStructure.getMessage());
            simpleMailMessage.setTo(mailStructure.getRecipient());

            mailSender.send(simpleMailMessage);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public String generateOTP() {
        int length = 6;
        String numbers = "1234567890";
        Random random = new Random();
        char[] otp = new char[length];
        for (int i = 0; i < length; i++) {
            otp[i] = numbers.charAt(random.nextInt(numbers.length()));
        }
        return new String(otp);
    }

    @Override
    public void sendOtpEmail(String toEmail, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromMail);
        message.setTo(toEmail);
        message.setSubject("OTP Verification");
        message.setText("Your OTP is: " + otp);
        mailSender.send(message);
    }

    @Override
    public MessageResponse sendOTP(String email) {
        Optional<Otp> existingOtp = otpRepository.findByUsername(email);
        if (existingOtp.isPresent()) {
            // Email already has an OTP, do not send a new one
            return new MessageResponse("OTP already sent for this email");
        }

        String otp = generateOTP();
        sendOtpEmail(email, otp);
        Otp otpEntity = new Otp(email, otp);
        otpRepository.save(otpEntity); // Save OTP to the database
        return new MessageResponse("OTP sent successfully");
    }

    @Override
    public MessageResponse validateOTP(String email, String otp) {
        Optional<Otp> otpEntityOptional = otpRepository.findByUsername(email);
        if (otpEntityOptional.isPresent()) {
            Otp otpEntity = otpEntityOptional.get();
            if (otpEntity.getOtp().compareTo(otp) == 0) {
                otpRepository.delete(otpEntity); // Delete OTP from the database//
                return new MessageResponse("OTP is valid");
            }
        }
        return new MessageResponse("Invalid OTP");
    }

}
