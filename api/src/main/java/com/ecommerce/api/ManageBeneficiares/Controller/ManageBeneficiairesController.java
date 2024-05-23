package com.ecommerce.api.ManageBeneficiares.Controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecommerce.api.Entity.Beneficiary;
import com.ecommerce.api.Repository.BeneficiaryRepository;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/v1/auth/")
public class ManageBeneficiairesController {



    @Autowired
    private BeneficiaryRepository beneRepository;


    @PostMapping("/getBeneficiariesHome")
    public ResponseEntity<List<Beneficiary>> getAllBeneficiaries(@RequestBody Long id) {
        List<Beneficiary> beneficiaries = beneRepository.findBeneficiariesByClientId(id);
        if (beneficiaries.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(beneficiaries, HttpStatus.OK);
    }


     // Method to add a new beneficiary
    @PostMapping("/addBeneficiaryHome")
    public ResponseEntity<Beneficiary> addBeneficiary(@RequestBody Beneficiary beneficiary) {
        Beneficiary savedBeneficiary = beneRepository.save(beneficiary);
        return new ResponseEntity<>(savedBeneficiary, HttpStatus.CREATED);
    }

    @PatchMapping("/updateBeneficiaryHome")
    public ResponseEntity<Beneficiary> updateBeneficiary(@RequestBody Beneficiary beneficiaryDetails) {
        Optional<Beneficiary> optionalBeneficiary = beneRepository.findById(beneficiaryDetails.getId());
        if (!optionalBeneficiary.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Beneficiary beneficiary = optionalBeneficiary.get();
        
        if (beneficiaryDetails.getFirstName() != null) {
            beneficiary.setFirstName(beneficiaryDetails.getFirstName());
        }
        if (beneficiaryDetails.getLastname() != null) {
            beneficiary.setLastname(beneficiaryDetails.getLastname());
        }
        if (beneficiaryDetails.getUsername() != null) {
            beneficiary.setUsername(beneficiaryDetails.getUsername());
        }
        if (beneficiaryDetails.getTitle() != null) {
            beneficiary.setTitle(beneficiaryDetails.getTitle());
        }
        if (beneficiaryDetails.getDatenaissance() != null) {
            beneficiary.setDatenaissance(beneficiaryDetails.getDatenaissance());
        }
        if (beneficiaryDetails.getProfession() != null) {
            beneficiary.setProfession(beneficiaryDetails.getProfession());
        }
        if (beneficiaryDetails.getVille() != null) {
            beneficiary.setVille(beneficiaryDetails.getVille());
        }
        if (beneficiaryDetails.getExpirationPieceIdentite() != null) {
            beneficiary.setExpirationPieceIdentite(beneficiaryDetails.getExpirationPieceIdentite());
        }
        if (beneficiaryDetails.getValiditePieceIdentite() != null) {
            beneficiary.setValiditePieceIdentite(beneficiaryDetails.getValiditePieceIdentite());
        }
        if (beneficiaryDetails.getAccount_amount() != null) {
            beneficiary.setAccount_amount(beneficiaryDetails.getAccount_amount());
        }
        if (beneficiaryDetails.getGSM() != null) {
            beneficiary.setGSM(beneficiaryDetails.getGSM());
        }
        if (beneficiaryDetails.getPayeNationale() != null) {
            beneficiary.setPayeNationale(beneficiaryDetails.getPayeNationale());
        }
        if (beneficiaryDetails.getPaysEmission() != null) {
            beneficiary.setPaysEmission(beneficiaryDetails.getPaysEmission());
        }
        if (beneficiaryDetails.getNumeroPieceIdentite() != null) {
            beneficiary.setNumeroPieceIdentite(beneficiaryDetails.getNumeroPieceIdentite());
        }
        if (beneficiaryDetails.getPieceIdentite() != null) {
            beneficiary.setPieceIdentite(beneficiaryDetails.getPieceIdentite());
        }

        Beneficiary updatedBeneficiary = beneRepository.save(beneficiary);
        return new ResponseEntity<>(updatedBeneficiary, HttpStatus.OK);
    }
    // Method to delete a beneficiary
    @PostMapping("/deleteBeneficiaryHome")
    public ResponseEntity<Void> deleteBeneficiary(@RequestBody Long id) {
        Optional<Beneficiary> optionalBeneficiary = beneRepository.findById(id);
        if (!optionalBeneficiary.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        beneRepository.deleteById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }




}
