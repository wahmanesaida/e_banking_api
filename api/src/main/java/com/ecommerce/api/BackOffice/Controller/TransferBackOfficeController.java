package com.ecommerce.api.BackOffice.Controller;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecommerce.api.BackOffice.Dto.ExcelUtil;
import com.ecommerce.api.BackOffice.Dto.MulticriteriaSearchDto;
import com.ecommerce.api.BackOffice.Dto.RenvoiDto;
import com.ecommerce.api.BackOffice.Service.TransferBackOfficeService;
import com.ecommerce.api.Entity.TransferStatus;
import com.ecommerce.api.Entity.Transfert;
import com.ecommerce.api.Entity.User;
import com.ecommerce.api.TransferMoney.dto.MailStructure;
import com.ecommerce.api.TransferMoney.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.http.HttpServletResponse;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/v1/auth/")
public class TransferBackOfficeController {

    @Autowired
    private TransferBackOfficeService transfertService;

    @Autowired
    private EmailService emailService;


    private static final Logger logger = LoggerFactory.getLogger(TransferBackOfficeController.class);

    @PostMapping("/searchCriteria")
    public ResponseEntity<List<Transfert>> searchTransfertBackOffice(@RequestBody MulticriteriaSearchDto searchDto) {
        if (searchDto == null) {
            return ResponseEntity.badRequest().build(); // Add input validation
        }

        if (searchDto.getTransferRef() != null) {
            Optional<Transfert> transfert = transfertService.findByTransferRef(searchDto.getTransferRef());
            return transfert.map(t -> ResponseEntity.ok(List.of(t))).orElseGet(() -> ResponseEntity.notFound().build());
        } else if (searchDto.getTypeOftransfer() != null) {
            return ResponseEntity.ok(transfertService.findByTypeTransfer(searchDto.getTypeOftransfer()));
        } else if (searchDto.getStatus() != null) {
            return ResponseEntity.ok(transfertService.findByStatus(searchDto.getStatus()));
        } else if (searchDto.getCreateTime() != null) {
            return ResponseEntity.ok(transfertService.findByCreateTime(searchDto.getCreateTime()));
        }

        return ResponseEntity.notFound().build();
    }

    @PostMapping("/searchByTransferRef")
    public ResponseEntity<Transfert> findByTransferRef(@RequestBody MulticriteriaSearchDto searchDto) {
        Optional<Transfert> transfert = transfertService.findByTransferRef(searchDto.getTransferRef());
        return transfert.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/searchByClient")
    public List<Transfert> findByClient(@RequestBody MulticriteriaSearchDto searchDto) {
        User client = new User();
        client.setId(searchDto.getCodeClient());
        return transfertService.findByClient(client);
    }

    @PostMapping("/searchByAgent")
    public List<Transfert> findByAgent(@RequestBody MulticriteriaSearchDto searchDto) {
        User agent = new User();
        agent.setId(searchDto.getCodeAgent());
        return transfertService.findByAgent(agent);
    }

    @PostMapping("/searchByTypeTransfer")
    public List<Transfert> findByTypeTransfer(@RequestBody MulticriteriaSearchDto searchDto) {
        return transfertService.findByTypeTransfer(searchDto.getTypeOftransfer());
    }

    @PostMapping("/searchByStatus")
    public List<Transfert> findByStatus(@RequestBody MulticriteriaSearchDto searchDto) {
        return transfertService.findByStatus(searchDto.getStatus());
    }

    @PostMapping("/searchByCreateTime")
    public List<Transfert> findByCreateTime(
            @RequestBody @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) MulticriteriaSearchDto searchDto) {
        return transfertService.findByCreateTime(searchDto.getCreateTime());
    }

    @PostMapping("/exportFile")
    public void exportTransfersToExcel(HttpServletResponse response, @RequestBody List<Transfert> transfers)
            throws IOException {
        // Générer le fichier Excel avec Apache POI
        byte[] excelBytes = ExcelUtil.generateExcel(transfers);

        // Écrire le fichier Excel dans la réponse HTTP
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=transfers.xlsx");
        response.getOutputStream().write(excelBytes);
    }

    @GetMapping("/transfers")
    public ResponseEntity<List<Transfert>> getAllTransfers() {
        List<Transfert> transfers = transfertService.getAllTransfers();
        return ResponseEntity.ok(transfers);
    }

    @PostMapping("/renvoiNotification")
    public void renvoyerNotification(@RequestBody RenvoiDto renvoiDto) throws IOException {
        Optional<Transfert> transfert = transfertService.findByTransferRef(renvoiDto.getTransferRef());
        if (transfert.isPresent() && TransferStatus.A_servir.equals(transfert.get().getStatus())) {
            TransferStatus status = transfert.get().getStatus();
            logger.info("Transfert status: {}", status);
            MailStructure mailStructure = MailStructure.builder()
                    .subject("Your account is debited")
                    .recipient(transfert.get().getClient().getUsername())
                    .message(transfert.get().getClient().getName() + " " + "you send money to " + " "
                            + transfert.get().getBeneficiary().getUsername() + " \n" + "your transfer reference : "
                            + "  "
                            + transfert.get().getTransferRef() + " \n" + " "
                            + "don't share this with anyone!"
                            + "\n" + "  " + "your total amount is: " + transfert.get().getAmount_transfer()
                            + transfert.get().getAmountOfFees()
                            + " " + "your transfer amount: " + transfert.get().getAmount_transfer()
                            + "Aggent est :  " + transfert.get().getAgent().getName() + "Client  "
                            + transfert.get().getClient().getName())
                    .build();
            emailService.sendMail(mailStructure);
        } else {
            throw new NoSuchElementException(
                    "Cannot renvoyer notification for transfers with status other than 'A_servir'");
        }
    }

   

}
