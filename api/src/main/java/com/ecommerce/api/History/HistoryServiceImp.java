package com.ecommerce.api.History;

import com.ecommerce.api.Entity.Beneficiary;
import com.ecommerce.api.Entity.Transfert;
import com.ecommerce.api.Repository.BeneficiaryRepository;
import com.ecommerce.api.Repository.TransfertRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HistoryServiceImp implements historyService{
    @Autowired
    TransfertRepository transfertRepository;

    @Autowired
    BeneficiaryRepository beneficiaryRepository;

    @Override
    public List<Transfert> getAllTransferts() {

        List<Transfert> transferts=transfertRepository.findAll();
        return transferts;
    }

    @Override
    public List<Beneficiary> getAllBeneficiaries() {
        return beneficiaryRepository.findAll();
    }
}
