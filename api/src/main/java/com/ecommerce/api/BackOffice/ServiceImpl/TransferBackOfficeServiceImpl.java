package com.ecommerce.api.BackOffice.ServiceImpl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ecommerce.api.BackOffice.Service.TransferBackOfficeService;
import com.ecommerce.api.Entity.TransferStatus;
import com.ecommerce.api.Entity.Transfert;
import com.ecommerce.api.Entity.TypeTransfer;
import com.ecommerce.api.Entity.User;
import com.ecommerce.api.Repository.TransfertRepository;
import com.ecommerce.api.Repository.UserRepository;

@Service
public class TransferBackOfficeServiceImpl implements TransferBackOfficeService{

    @Autowired
    private TransfertRepository transfertRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public Optional<Transfert> findByTransferRef(String transferRef) {
        return transfertRepository.findByTransferRef(transferRef);
    }

    @Override
    public List<Transfert> findByClient(User codeClient) {
        return transfertRepository.findByClient(codeClient);
    }

    @Override
    public List<Transfert> findByAgent(User codeAgent) {
        return transfertRepository.findByAgent(codeAgent);
    }

    @Override
    public List<Transfert> findByTypeTransfer(TypeTransfer type_transfer) {
        return transfertRepository.findByTypeOftransfer(type_transfer);
    }

    @Override
    public List<Transfert> findByStatus(TransferStatus status) {
        return transfertRepository.findByStatus(status);
    }

    @Override
    public List<Transfert> findByCreateTime(LocalDateTime createTime) {
        return transfertRepository.findByCreateTime(createTime);
    }

    @Override
    public List<Transfert> getAllTransfers() {
        return transfertRepository.findAll();
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }



  
    

}
