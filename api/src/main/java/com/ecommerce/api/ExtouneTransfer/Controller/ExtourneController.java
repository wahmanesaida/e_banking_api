package com.ecommerce.api.ExtouneTransfer.Controller;

import java.io.IOException;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecommerce.api.ExtouneTransfer.Service.ExtourneService;
import com.ecommerce.api.ServingTransfer.Dto.TransferPaymentDto;
import com.lowagie.text.DocumentException;

import jakarta.servlet.http.HttpServletResponse;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/v1/auth/")
public class ExtourneController {

    @Autowired
    private ExtourneService extourneTransfer;


    @PostMapping("/generateExtourneReceipt")
    public void generateExtourneReceipt(@RequestBody TransferPaymentDto transferPaymentDto, HttpServletResponse response) {
        try {
            extourneTransfer.generateExtourneReceipt(transferPaymentDto, response);
        } catch (IOException | DocumentException e) {
            e.printStackTrace(); 
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/reverseTransfer")
    public ResponseEntity<?> reverseTransfer(@RequestBody TransferPaymentDto transferPaymentDto,
            HttpServletResponse response) {
        try {
            extourneTransfer.reverseTransfer(transferPaymentDto, response);
            return ResponseEntity.ok().build();
        } catch (NoSuchElementException | IllegalStateException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred");
        }
    }


}
