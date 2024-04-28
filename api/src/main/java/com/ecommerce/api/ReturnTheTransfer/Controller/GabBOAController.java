package com.ecommerce.api.ReturnTheTransfer.Controller;


import com.ecommerce.api.Entity.Transfert;
import com.ecommerce.api.ReturnTheTransfer.DTO.ReturnTransferDTO;
import com.ecommerce.api.ReturnTheTransfer.ServiceImp.GabBOAImp;
import com.ecommerce.api.TransferMoney.Response.MessageResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
            if(transfert == null){
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Transert not found");
            }
            else{
                return ResponseEntity.status(HttpStatus.OK).body(transfert);
            }
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
}
