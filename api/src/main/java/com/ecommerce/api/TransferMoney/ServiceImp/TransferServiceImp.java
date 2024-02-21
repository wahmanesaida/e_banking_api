package com.ecommerce.api.TransferMoney.ServiceImp;

import com.ecommerce.api.Repository.BeneficiaryRepository;
import com.ecommerce.api.Repository.OtpRepository;
import com.ecommerce.api.Repository.ProspectsRepository;
import com.ecommerce.api.Repository.TransfertRepository;
import com.ecommerce.api.Repository.UserRepository;
import com.ecommerce.api.Entity.*;
import com.ecommerce.api.TransferMoney.dto.*;
import com.ecommerce.api.TransferMoney.service.EmailService;
import com.ecommerce.api.TransferMoney.service.TransferService;
import com.ecommerce.api.TransferMoney.utils.TransferUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Optional;
import java.util.Random;

@Service
public class TransferServiceImp implements TransferService {

    @Autowired
    UserRepository userRepo;

    @Autowired
    OtpRepository otpRepository;

    @Autowired
    BeneficiaryRepository beneficiaryRepository;

    @Autowired
    TransferUtils transferUtils;

    @Autowired
    EmailService emailService;

    @Autowired
    ProspectsRepository prospectsRepository;

    @Autowired
    TransfertRepository transfertRepository;

    private static final long EDP_Code = 837;
    private static final Random random = new Random();

    @Transactional
    @Override
    public void modifyuKYC(String username, Kyc kyc) {
        Optional<User> OptionalUser = userRepo.findByUsername(username);
        if (OptionalUser.isPresent()) {
            User existingUser = OptionalUser.get();
            if (kyc.getTitle() != null) {
                existingUser.setTitle(kyc.getTitle());
            }

            if (kyc.getPieceIdentite() != null) {
                existingUser.setPieceIdentite(kyc.getPieceIdentite());
            }

            if (kyc.getNumeroPieceIdentite() != null) {
                existingUser.setNumeroPieceIdentite(kyc.getNumeroPieceIdentite());
            }

            if (kyc.getProfession() != null) {
                existingUser.setProfession(kyc.getProfession());
            }

            if (kyc.getVille() != null) {
                existingUser.setVille(kyc.getVille());
            }

            if (kyc.getGSM() != null) {
                existingUser.setGSM(kyc.getGSM());
            }

            if (kyc.getAccount_amount() != null) {
                existingUser.setAccount_amount(kyc.getAccount_amount());
            }

            userRepo.save(existingUser);
        } else {
            // Handle the case where the user with the specified username does not exist
            throw new IllegalArgumentException("User with username " + username + " not found");
        }

    }

    @Override
    public User showKyc(String phone) {

        return userRepo.findUserByPhoneNumber(phone).get();
    }

    @Override
    public Beneficiary transferMoney(TransfertDto transfertDto, long client_id, long bene_id, BeneficiaryDto bene) {
        return null;
    }

    @Override
    public String trs(TransfertDto transfertDto, long client_id, long bene_id, BeneficiaryDto bene) {

        // Send OTP
        String email = getEmailForClient(client_id);
        ResponseEntity<String> sendOtpResponse = emailService.sendOTP(email);
        if (!sendOtpResponse.getStatusCode().is2xxSuccessful()) {
            return "Failed to send OTP, Transfer cannot be completed !!";
        }

        if (transfertDto.getTypeOftransfer() == Type_transfer.ACCOUNT_DEBIT) {

            Optional<User> userOptional = userRepo.findById(client_id);
            Beneficiary beneficiary;

            // Verify OTP before completing the transfer
            String otp = getOtpFromUser(email); 
            ResponseEntity<String> otpValidationResponse = emailService.validateOTP(email, otp);

          /*   if (otpValidationResponse.getStatusCode().is2xxSuccessful()) {
                // Proceed with the transfer if OTP is valid
               
            
                return "Congratulations, your transaction has been successful with a good amount " + transfertDto.getAmount_transfer();
            }
 */
            ExpenseManagement(transfertDto);
            if (userOptional.isPresent()) {
                User user = userOptional.get();

                // transfertDto.setAmount_transfer(transfertDto.getAmount_entred().divide(new
                // BigDecimal(100)));
                BigDecimal checkAmount = (transfertDto.getAmount_transfer()).divide(new BigDecimal(100));
                checkAmountOfTransfert(transfertDto, user, checkAmount);

                transfertDto.setGenerateRef(generateTransferReference());
                beneficiary = selectOrAddBeneficiary(client_id, bene_id, bene);

                DebitCreditAccount(transfertDto, user, beneficiary);

                if (transfertDto.isNotification()) {
                    emailService.sendMail(
                            MailStructure.builder()
                                    .subject("Your account is credited")
                                    .recipient(beneficiary.getUsername())
                                    .message(beneficiary.getFirstName() + " " + "you received money from " + " "
                                            + user.getUsername() + " \n" + "your transfer reference : " + "  "
                                            + transfertDto.getGenerateRef() + " \n" + " "
                                            + "don't share this with anyone!"
                                            + "\n" + "  " + "your total amount is: " + transfertDto.getAmount_total()
                                            + " " + "your transfer amount: " + transfertDto.getAmount_transfer())
                                    .build());

                }

                Transfert transfert = new Transfert();
                transfert.setAmount_transfer(transfertDto.getAmount_transfer());
                transfert.setType_transfer(transfertDto.getTypeOftransfer());
                transfert.setTypeOfFees(transfertDto.getFees());
                transfert.setAmountOfFees(transferUtils.getFraiDuTransfert());
                transfert.setStatus("A servir");
                transfert.setClient(user);
                transfertRepository.save(transfert);

                processTransaction(user);

                return " congratulations, your transaction has been successful with a good amount   "
                        + transfertDto.getAmount_transfer();

            }
            return "user not found !";
        } else {
            return "an error occurred this method for a transfer by account debit!" + " "
                    + transfertDto.getTypeOftransfer();
        }

    }

    @Override
    public String getOtpFromUser(String email) {
        Optional<Otp> otpEntityOptional = otpRepository.findByEmail(email);
        if (otpEntityOptional.isPresent()) {
            return otpEntityOptional.get().getOtp();
        }
        return null; 
    }

    @Override
    public String getEmailForClient(long client_id) {
        User userMail = userRepo.getReferenceById(client_id);
        return userMail.getUsername();
    }

    @Override
    @Transactional
    public void processTransaction(User user) {
        Prospects prospects = prospectsRepository.findByUser(user);

        if (prospects != null) {
            // If Prospects entity already exists, update it within the same transaction
            prospects.setUser(user);
        } else {
            // If Prospects entity doesn't exist, create a new one within the same
            // transaction
            Prospects newProspect = Prospects.builder()
                    .user(user)
                    .build();
            prospectsRepository.save(newProspect);
        }
    }

    @Override
    public String checkAmountOfTransfert(TransfertDto transfertDto, User user, BigDecimal checkAmount) {
        if (transfertDto.getAmount_transfer().compareTo(user.getAccount_amount()) > 0) {
            return "Transfer amount is greater than your account amount";
        }

        if (checkAmount.compareTo(transferUtils.getTransfertMax1()) > 0) {
            return "Transfer amount is greater than maximum transfer limit";
        }

        if (transfertDto.getAmount_entred().compareTo(transferUtils.getMinTotal()) < 0) {
            return "you must make a transfer with a minimum amount of" + " " + transferUtils.MinTotal + " dh !"
                    + " actually your transfer amount is : " + transfertDto.getAmount_transfer();
        }
        return "good Amount of transfert";

    }

    @Override
    public void ExpenseManagement(TransfertDto transfertDto) {

        if (transfertDto.isNotification()) {

            if (transfertDto.getFees().equals(TypeOfFees.donor)) {
                transfertDto.setAmount_total(transfertDto.getAmount_entred().add(transferUtils.getFraiDuTransfert())
                        .add(transferUtils.getNotificationCosts()));
                transfertDto.setAmount_transfer(transfertDto.getAmount_entred());
            }
            if (transfertDto.getFees().equals(TypeOfFees.beneficiary)) {
                transfertDto.setAmount_total(transfertDto.getAmount_entred().add(transferUtils.getNotificationCosts()));
                transfertDto
                        .setAmount_transfer(transfertDto.getAmount_entred().subtract(transferUtils.fraiDuTransfert));
            }
            if (transfertDto.getFees().equals(TypeOfFees.shared)) {
                transfertDto.setAmount_total(
                        transfertDto.getAmount_entred().add((transferUtils.fraiDuTransfert).divide(new BigDecimal(2)))
                                .add(transferUtils.getNotificationCosts()));
                transfertDto.setAmount_transfer(transfertDto.getAmount_entred()
                        .subtract((transferUtils.fraiDuTransfert).divide(new BigDecimal(2))));
            }
        }

        else {
            if (transfertDto.getFees().equals(TypeOfFees.donor)) {
                transfertDto.setAmount_total(transfertDto.getAmount_entred().add(transferUtils.getFraiDuTransfert()));
                transfertDto.setAmount_transfer(transfertDto.getAmount_entred());
            }
            if (transfertDto.getFees().equals(TypeOfFees.beneficiary)) {
                transfertDto.setAmount_total(transfertDto.getAmount_entred());
                transfertDto
                        .setAmount_transfer(transfertDto.getAmount_entred().subtract(transferUtils.fraiDuTransfert));
            }
            if (transfertDto.getFees().equals(TypeOfFees.shared)) {
                transfertDto.setAmount_total(
                        transfertDto.getAmount_entred().add((transferUtils.fraiDuTransfert).divide(new BigDecimal(2))));
                transfertDto.setAmount_transfer(transfertDto.getAmount_entred()
                        .subtract((transferUtils.fraiDuTransfert).divide(new BigDecimal(2))));
            }

        }

    }

    @Override
    public void DebitCreditAccount(TransfertDto transfertDto, User user, Beneficiary beneficiary) {
        if (user != null && user.getAccount_amount() != null && beneficiary != null
                && beneficiary.getAccount_amount() != null) {
            BigDecimal userNewAmount = user.getAccount_amount().subtract(transfertDto.getAmount_total());
            BigDecimal beneficiaryNewAmount = beneficiary.getAccount_amount().add(transfertDto.getAmount_transfer());

            user.setAccount_amount(userNewAmount);
            beneficiary.setAccount_amount(beneficiaryNewAmount);
            userRepo.save(user);
            beneficiaryRepository.save(beneficiary);
        } else {
            throw new IllegalArgumentException("Invalid user or beneficiary provided for account update");
        }
    }

    // generate reference
    @Override
    public String generateTransferReference() {

        long random_number = (long) (random.nextDouble() * 1_000_000_000_0L);
        DecimalFormat format = new DecimalFormat("0000000000");
        String formatedRef = format.format(random_number);
        return EDP_Code + formatedRef;

    }

    @Override
    public Beneficiary selectOrAddBeneficiary(long id_user, long id_beneficiary, BeneficiaryDto beneficiaryDto) {
        Optional<Beneficiary> beneficiary1Optional = beneficiaryRepository.findById(id_beneficiary);
        Optional<User> userOptional = userRepo.findById(id_user);
        User user;
        if (userOptional.isEmpty()) {
            throw new IllegalArgumentException("User with id " + id_user + " not found");
        }
        user = userOptional.get();
        Beneficiary beneficiary1;

        if (beneficiary1Optional.isPresent()) {
            beneficiary1 = beneficiary1Optional.get();
            return beneficiary1;
        } else {
            beneficiary1 = Beneficiary.builder()
                    .firstName(beneficiaryDto.getFirstName())
                    .lastname(beneficiaryDto.getLastname())
                    .GSM(beneficiaryDto.getGSM())
                    .username(beneficiaryDto.getUsername())
                    .account_amount(new BigDecimal(0))
                    .client(user)
                    .build();

            beneficiaryRepository.save(beneficiary1);
            return beneficiary1;
        }

    }

}
