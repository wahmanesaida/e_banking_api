package com.ecommerce.api.TransferMoney.Controller;

import com.ecommerce.api.Entity.Beneficiary;
import com.ecommerce.api.Entity.Transfert;
import com.ecommerce.api.Entity.User;
import com.ecommerce.api.Repository.TransfertRepository;
import com.ecommerce.api.ReturnTheTransfer.ServiceImp.GabBOAImp;
import com.ecommerce.api.ServingTransfer.Dto.TransferPaymentDto;
import com.ecommerce.api.TransferMoney.Response.BeneficiaryResponseDTO;
import com.ecommerce.api.TransferMoney.Response.MessageResponse;
import com.ecommerce.api.TransferMoney.Response.TransferResponse;
import com.ecommerce.api.TransferMoney.dto.BeneficiaryDto;
import com.ecommerce.api.TransferMoney.dto.Kyc;
import com.ecommerce.api.TransferMoney.dto.TransfertDto;
import com.ecommerce.api.TransferMoney.service.TransferService;
import com.lowagie.text.DocumentException;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.validation.ConstraintViolationException;
import javax.validation.constraints.Pattern;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/v1/auth/")
public class modifyUser {

    @Autowired
    private TransferService transferService;

    @Autowired
    private GabBOAImp gabBOAImp;

    @Autowired
    private  TransfertRepository transfertRepository;

    @PostMapping("/modify/{username}")
    public ResponseEntity<String> modifykyc(@PathVariable String username, @RequestBody Kyc kyc) {
        try {
            transferService.modifyuKYC(username, kyc);
            return new ResponseEntity<>("KYC information modified successfully", HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>("Failed to modify KYC information: " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("showkyc/{phone}")
    public ResponseEntity<?> showkyc(@PathVariable @Pattern(regexp = "^\\+212\\d{9}$", message = "Invalid Moroccan phone number") String phone) {
        try {
           User user= transferService.showKyc(phone);

            return new ResponseEntity<>(user, HttpStatus.OK);
        }catch (ConstraintViolationException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        catch (Exception e) {
            return new ResponseEntity<>(transferService.showKyc(phone), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

//    @PostMapping("/transfert/{id}")
//    public ResponseEntity<?> transfertController(@RequestBody TransferRequest request,
//                                                               @PathVariable long id) {
//        try {
//            MessageResponse rp = transferService.trs(request.getTransfertDto(), id, request.getId_beneficiary(),
//                    request.getBene());
//            if (rp.getMessage().startsWith("congratulations")) {
//                Optional<Transfert> transfertOptional = transfertRepository.findByTransferRef(request.getTransfertDto().getGenerateRef());
//                if (transfertOptional.isPresent()) {
//                    Transfert transfert = transfertOptional.get();
//                    ByteArrayOutputStream pdfBuffer = transferService.generatetransfertReceipt(transfert, transfert.getClient(), transfert.getBeneficiary());
//
//                    // Configurer la réponse HTTP
//                    HttpHeaders headers = new HttpHeaders();
//                    headers.setContentType(MediaType.APPLICATION_PDF);
//                    headers.setContentDispositionFormData("filename", "receipt.pdf");
//
//                    // Retourner le PDF en tant que pièce jointe
//                    return ResponseEntity.ok()
//                            .headers(headers)
//                            .contentType(MediaType.APPLICATION_PDF)
//                            .body(new InputStreamResource(new ByteArrayInputStream(pdfBuffer.toByteArray())));
//                } else {
//                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResponse("Transfert not found"));
//                }
//            } else {
//                return ResponseEntity.status(HttpStatus.OK).body(rp);
//            }
//        } catch (IllegalArgumentException e) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageResponse("error"));
//        } catch (IOException | DocumentException e) {
//            // Gérer les erreurs d'E/S ou de génération de document
//            e.printStackTrace();
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body(new MessageResponse("An error occurred while generating the transfer receipt"));
//        }
//    }

    @PostMapping("/transfert/{id}")
    public ResponseEntity<TransferResponse> transfertController(@RequestBody TransferRequest request,
                                                                @PathVariable long id) {
        try {
            MessageResponse rp = transferService.trs(request.getTransfertDto(), id, request.getId_beneficiary(),
                    request.getBene());
            if (rp.getMessage().startsWith("congratulations")) {
                Optional<Transfert> transfertOptional = transfertRepository.findByTransferRef(request.getTransfertDto().getGenerateRef());
                if (transfertOptional.isPresent()) {
                    Transfert transfert = transfertOptional.get();
                    ByteArrayOutputStream pdfBuffer = transferService.generatetransfertReceipt(transfert, transfert.getClient(), transfert.getBeneficiary());

                    TransferResponse transferResponse = new TransferResponse();
                    transferResponse.setMessage(rp);
                    transferResponse.setReceiptPdf(pdfBuffer.toByteArray());

                    return ResponseEntity.ok(transferResponse);
                } else {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new TransferResponse(null, null));
                }
            } else {
                return ResponseEntity.status(HttpStatus.OK).body(new TransferResponse(rp, null));
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new TransferResponse(new MessageResponse("error"), null));
        } catch (IOException | DocumentException e) {
            // Gérer les erreurs d'E/S ou de génération de document
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new TransferResponse(new MessageResponse("An error occurred while generating the transfer receipt"), null));
        }
    }




    @PostMapping("/add/{id_user}/{id_beneficiary}")
    public ResponseEntity<?> addBeneficiary(
            @RequestBody BeneficiaryDto beneficiaryDto,
            @PathVariable long id_user,
            @PathVariable long id_beneficiary) {
        try {
            Beneficiary addedBeneficiary = transferService.selectOrAddBeneficiary(id_user, id_beneficiary,
                    beneficiaryDto);
            if (addedBeneficiary != null) {
                BeneficiaryResponseDTO responseDTO = new BeneficiaryResponseDTO(
                        addedBeneficiary.getId(),
                        addedBeneficiary.getFirstName(),
                        addedBeneficiary.getLastname(),
                        addedBeneficiary.getUsername(),
                        addedBeneficiary.getGSM()
                // Include other fields as needed
                );
                return ResponseEntity.status(HttpStatus.OK).body(responseDTO);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("beneficiary not found");
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("bad request");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
        }
    }

    @GetMapping("/generateReference")
    public ResponseEntity<String> generateReference() {
        try {
            String reff = transferService.generateTransferReference();
            return ResponseEntity.status(HttpStatus.OK).body(reff);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
        }
    }

    @PostMapping("/check-amount")
    public ResponseEntity<MessageResponse> checkAmountOfTransfer(@RequestBody CheckAmountRequest checkAmountRequest) {
        //checkAmountRequest.checkAmount = checkAmountRequest.transfertDto.getAmount_transfer().divide(new BigDecimal(100));
        try{
            MessageResponse result = transferService.checkAmountOfTransfert(checkAmountRequest.transfertDto, checkAmountRequest.id);
            return ResponseEntity.ok(result);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new MessageResponse("Internal server error"));
        }

    }

    @GetMapping("/selectbene/{id_beneficiary}")
    public ResponseEntity<?> SelectBene(@PathVariable long id_beneficiary){
        try {
            Beneficiary beneee=transferService.SelectBene(id_beneficiary);
            return new ResponseEntity<>(beneee, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(transferService.SelectBene(id_beneficiary), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }
    @PostMapping("/addnew_beneficiary")
    public ResponseEntity<Beneficiary> AddNewBeneficiary(@RequestBody BeneficiaryRequest bene){
        try{
            return new ResponseEntity<Beneficiary>(transferService.AddBeneficiary(bene.getBeneficiaryDto(), bene.getId_user()), HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<Beneficiary>(transferService.AddBeneficiary(bene.getBeneficiaryDto(), bene.getId_user()), HttpStatus.INTERNAL_SERVER_ERROR);

        }
    }

   @PostMapping("/showKycBynumeroPieceIdentite")
    public ResponseEntity<?> showKycBynumeroPieceIdentite(@RequestBody findAgentRequest request){
        try {
            User user=transferService.ShowKycByPieceIdentite(request.numeroPieceIdentite);
           return new ResponseEntity<>(user, HttpStatus.OK);
        } catch (Exception ex){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
        }
    }

    @PostMapping("/deleteCodePin")
    public ResponseEntity<MessageResponse> deleteCodePin(@RequestBody DeleteCodeRequest request){
        try {
            return ResponseEntity.ok(transferService.deleteCodePin(request.username, request.id));

        }catch (Exception ex){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new MessageResponse(ex.getMessage()));

        }
    }

    @GetMapping("/listOfBenfficiaries/{ClientId}")
    public ResponseEntity<?> getListOfBeneficiaries(@PathVariable long ClientId){
        try {
            return ResponseEntity.ok(transferService.getBeneficiariesByClientId(ClientId));
        }catch (Exception ex){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred: " + ex.getMessage());

        }

    }

    @PostMapping("/generateTransferReceiptByAgent")
    public void generateTransferReceiptByAgent(@RequestBody TransferPaymentDto transferPaymentDto, HttpServletResponse response) {
        try {
            gabBOAImp.generateReturnReceipt(transferPaymentDto, response);
        } catch (IOException | DocumentException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
