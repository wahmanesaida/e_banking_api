package com.ecommerce.api.GabBoa.Controller;

import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecommerce.api.Entity.Transfert;
import com.ecommerce.api.GabBoa.Dto.TransferRefDTO;
import com.ecommerce.api.GabBoa.Service.ServingTransferGab;


@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/v1/auth/")
public class GabController {

    @Autowired
    private ServingTransferGab servingTransferGab;


    @PostMapping("/showTransferGab")
    public ResponseEntity<?> searchTransferGab(@RequestBody TransferRefDTO transferRefDTO) {
        try {
            Transfert transfert = servingTransferGab.searchTransferGab(transferRefDTO);
            if (transfert != null) {
                return ResponseEntity.ok(transfert);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (NoSuchElementException | IllegalStateException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        } catch (Exception ex){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
        } 
      
    }

}
