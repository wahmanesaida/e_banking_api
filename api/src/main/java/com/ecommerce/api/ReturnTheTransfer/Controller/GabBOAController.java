package com.ecommerce.api.ReturnTheTransfer.Controller;


import com.ecommerce.api.Entity.Transfert;
import com.ecommerce.api.ReturnTheTransfer.DTO.ReturnTransferDTO;
import com.ecommerce.api.ReturnTheTransfer.ServiceImp.GabBOAImp;
import com.ecommerce.api.ServingTransfer.Dto.TransferPaymentDto;
import com.ecommerce.api.TransferMoney.Response.MessageResponse;
import com.lowagie.text.DocumentException;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/v1/auth/")
public class GabBOAController {

    @Autowired
    GabBOAImp gabBOAImp;
    @GetMapping("/GetTransactionByRef/{transferRef}")
    public ResponseEntity<?> GetTransactionByRef(@PathVariable String transferRef){
        try{
            Transfert transfert=gabBOAImp.AccessTheTransactionByRef(transferRef);
            return ResponseEntity.status(HttpStatus.OK).body(transfert);

        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("bad request");
        }

    }

    @PostMapping("/ReturnTheTransfer")
    public ResponseEntity<MessageResponse> ReturnTheTransfer(@RequestBody ReturnTransferDTO request){
        try {
           return ResponseEntity.status(HttpStatus.OK).body(gabBOAImp.ReturnTransfer(request));
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageResponse("something went wrong"));
        }

    }

    @PostMapping("/generateReturnReceiptByAgent")
    public void generateReturnReceiptByAgent(@RequestBody TransferPaymentDto transferPaymentDto, HttpServletResponse response) {
        try {
            gabBOAImp.generateReturnReceipt(transferPaymentDto, response);
        } catch (IOException | DocumentException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
