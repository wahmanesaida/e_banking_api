package com.ecommerce.api.BlockingProcess;

import com.ecommerce.api.TransferMoney.Response.MessageResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/v1/auth/")
public class blockingProcess {
    @Autowired
    BlockingServiceImp blockingServiceImp;
    @PostMapping("/blocking-process")
   ResponseEntity<MessageResponse> blocktheTransfer(@RequestBody bloqueRequest request){
        try {
            return ResponseEntity.ok().body(blockingServiceImp.blockTransfer(request.getReference(), request.getStatus()));

        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new MessageResponse("something went wrong!"));

        }

   }
}
