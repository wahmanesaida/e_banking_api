package com.ecommerce.api.BackOffice.Controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ecommerce.api.BackOffice.Dto.MulticriteriaSearchDto;
import com.ecommerce.api.BackOffice.Service.TransferBackOfficeService;
import com.ecommerce.api.Entity.Transfert;
import com.ecommerce.api.Entity.Type_transfer;
import com.ecommerce.api.Entity.User;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/v1/auth/")
public class TransferBackOfficeController {


    @Autowired
    private TransferBackOfficeService transfertService;

    @GetMapping("/searchByTransferRef")
    public ResponseEntity<Transfert> findByTransferRef(@RequestBody MulticriteriaSearchDto searchDto) {
        Optional<Transfert> transfert = transfertService.findByTransferRef(searchDto.getTransferRef());
        return transfert.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/searchByClient")
    public List<Transfert> findByClient(@RequestBody MulticriteriaSearchDto searchDto) {
        User client = new User();
        client.setId(searchDto.getCodeClient());
        return transfertService.findByClient(client);
    }
    
    @GetMapping("/searchByAgent")
    public List<Transfert> findByAgent(@RequestBody MulticriteriaSearchDto searchDto) {
        User agent = new User();
        agent.setId(searchDto.getCodeAgent());
        return transfertService.findByAgent(agent);
    }
    
    

    @GetMapping("/searchByTypeTransfer")
    public List<Transfert> findByTypeTransfer(@RequestBody MulticriteriaSearchDto searchDto) {
        return transfertService.findByTypeTransfer(searchDto.getTypeTransfer());
    }

    @GetMapping("/searchByStatus")
    public List<Transfert> findByStatus(@RequestBody MulticriteriaSearchDto searchDto) {
        return transfertService.findByStatus(searchDto.getStatus());
    }

    @GetMapping("/searchByCreateTime")
    public List<Transfert> findByCreateTime(@RequestBody  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) MulticriteriaSearchDto searchDto) {
        return transfertService.findByCreateTime(searchDto.getCreateTime());
    }




}
