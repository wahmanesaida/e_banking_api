package com.ecommerce.api.BackOffice.ServiceImpl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ecommerce.api.BackOffice.Service.TransferBackOfficeService;
import com.ecommerce.api.Entity.Transfert;
import com.ecommerce.api.Entity.Type_transfer;
import com.ecommerce.api.Entity.User;
import com.ecommerce.api.Repository.TransfertRepository;

@Service
public class TransferBackOfficeServiceImpl implements  TransferBackOfficeService{

    @Autowired
    private TransfertRepository transfertRepository;

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
    public List<Transfert> findByTypeTransfer(Type_transfer typeTransfer) {
        return transfertRepository.findByTypeTransfer(typeTransfer);
    }

    @Override
    public List<Transfert> findByStatus(String status) {
        return transfertRepository.findByStatus(status);
    }

    @Override
    public List<Transfert> findByCreateTime(LocalDateTime createTime) {
        return transfertRepository.findByCreateTime(createTime);
    }

    

}
