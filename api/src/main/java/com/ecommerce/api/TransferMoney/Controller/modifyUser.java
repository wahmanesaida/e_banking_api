package com.ecommerce.api.TransferMoney.Controller;

import com.ecommerce.api.Entity.Beneficiary;
import com.ecommerce.api.Entity.User;
import com.ecommerce.api.TransferMoney.Response.BeneficiaryResponseDTO;
import com.ecommerce.api.TransferMoney.dto.BeneficiaryDto;
import com.ecommerce.api.TransferMoney.dto.Kyc;
import com.ecommerce.api.TransferMoney.dto.TransfertDto;
import com.ecommerce.api.TransferMoney.service.TransferService;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth/")
public class modifyUser {
    @Autowired
    private TransferService transferService;

    @PostMapping("/modify/{username}")
    public ResponseEntity<String> modifykyc(@PathVariable String username, @RequestBody Kyc kyc) {
        try {
            transferService.modifyuKYC(username, kyc);
            return new ResponseEntity<>("KYC information modified successfully", HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>("Failed to modify KYC information: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("showkyc/{phone}")
    public ResponseEntity<User> showkyc(@PathVariable String phone){
        try {
            transferService.showKyc(phone);
            return new ResponseEntity<>(transferService.showKyc(phone), HttpStatus.OK);
        }catch (Exception e) {
            return  new ResponseEntity<>(transferService.showKyc(phone) , HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }



    @PostMapping("/transfert/{id}")
    public ResponseEntity<?>transfertController(@RequestBody TransferRequest request, @PathVariable long id, HttpServletResponse response){
        try{
            String rp=transferService.trs(request.getTransfertDto(), id, request.getId_beneficiary(), request.getBene(), response);
            return ResponseEntity.status(HttpStatus.OK).body(rp);
            
        }catch (IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("error");

        }
    }


    @PostMapping("/add/{id_user}/{id_beneficiary}")
    public ResponseEntity<?> addBeneficiary(
            @RequestBody BeneficiaryDto beneficiaryDto,
            @PathVariable long id_user,
            @PathVariable long id_beneficiary) {
        try {
            Beneficiary addedBeneficiary = transferService.selectOrAddBeneficiary(id_user, id_beneficiary, beneficiaryDto);
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
    public ResponseEntity<String> generateReference(){
        try {
            String reff=transferService.generateTransferReference();
            return ResponseEntity.status(HttpStatus.OK).body(reff);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
        }
    }




}
