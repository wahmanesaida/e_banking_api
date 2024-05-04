package com.ecommerce.api.BlockingProcess;

import com.ecommerce.api.Entity.TransferStatus;
import com.ecommerce.api.Entity.Transfert;
import com.ecommerce.api.Repository.TransfertRepository;
import com.ecommerce.api.TransferMoney.Response.MessageResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class BlockingServiceImp implements BlockingService {

    @Autowired
    TransfertRepository transfertRepository;

    @Override
    public MessageResponse blockTransfer(String reference, TransferStatus status) {
        Optional<Transfert> transfertOptional=transfertRepository.findByTransferRef(reference);
        if(transfertOptional.isEmpty()){
            return new MessageResponse("Il n y'a pas de transfert asscocié a ce référence");
        }else{
            Transfert transfert=transfertOptional.get();
            if(status.equals(TransferStatus.Bloqué)){
                if(transfert.getStatus() == TransferStatus.A_servir){
                    transfert.setStatus(status);
                    transfertRepository.save(transfert);
                    return new MessageResponse("Vous avez bloqué avec succés le transfert souhaité");

                }else{
                    return new MessageResponse("le transfert doit être à l'état à servir !");
                }
            } else if (status.equals(TransferStatus.Débloqué_a_servir)) {
                if(transfert.getStatus() == TransferStatus.Bloqué){
                    transfert.setStatus(status);
                    transfertRepository.save(transfert);
                    return new MessageResponse("Vous avez débloqué avec succés le transfert souhaité");

                }else{
                    return new MessageResponse("le transfert doit être à l'état bloqué !");
                }

            }else {
                return new MessageResponse("vous devez choisir soit le statut bloqué soit le statut débloqué !");
            }

        }

    }
}
