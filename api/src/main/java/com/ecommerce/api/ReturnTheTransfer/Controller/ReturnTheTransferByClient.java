package com.ecommerce.api.ReturnTheTransfer.Controller;

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
public class ReturnTheTransferByClient {
    @Autowired
    GabBOAImp gabBOAImp;

    @GetMapping("/AllTheTTransersOfAClient/{clientd}")
    public ResponseEntity<?> AllTheTTransersOfAClient(@PathVariable long clientd){
        try{
            return ResponseEntity.status(HttpStatus.OK).body(gabBOAImp.allTransfersOfClient(clientd));

        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("there is no transfer for this Client ");

        }

    }

    @PostMapping("restoreTheTransferByClient")
    public ResponseEntity<MessageResponse> returnTheTransferOfAClent(@RequestBody ReturnTransferDTO returnTransferDTO){
        try {
            return ResponseEntity.status(HttpStatus.OK).body(gabBOAImp.ReturnTheTransferByTheClient(returnTransferDTO));

        }catch (Exception e){

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageResponse("something went wrong !"));
        }
    }

    @PostMapping("/generateReturnReceipt")
    public void generateExtourneReceipt(@RequestBody TransferPaymentDto transferPaymentDto, HttpServletResponse response) {
        try {
            gabBOAImp.generateReturnReceipt(transferPaymentDto, response);
        } catch (IOException | DocumentException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
