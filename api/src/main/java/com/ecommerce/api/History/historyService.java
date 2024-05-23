package com.ecommerce.api.History;

import com.ecommerce.api.Entity.Beneficiary;
import com.ecommerce.api.Entity.Transfert;
import com.ecommerce.api.Repository.TransfertRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface historyService {

 List<Transfert> getAllTransferts();
 List<Beneficiary> getAllBeneficiaries();
}
