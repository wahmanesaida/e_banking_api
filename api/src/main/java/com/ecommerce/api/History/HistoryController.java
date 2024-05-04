package com.ecommerce.api.History;

import com.ecommerce.api.Entity.Transfert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/api/v1/auth/")
@RestController
public class HistoryController {

    @Autowired
    HistoryServiceImp historyServiceImp;

    @GetMapping("/AllTransactions")
    public ResponseEntity<?> AllTransfers(){
        try {
            List<Transfert> transferts=historyServiceImp.getAllTransferts();

                return ResponseEntity.ok(transferts);
        }catch (Exception ex){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred: " + ex.getMessage());

        }
    }
}
