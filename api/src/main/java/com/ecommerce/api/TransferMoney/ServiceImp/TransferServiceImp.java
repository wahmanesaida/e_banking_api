package com.ecommerce.api.TransferMoney.ServiceImp;

import com.ecommerce.api.Repository.*;
import com.ecommerce.api.Entity.*;
import com.ecommerce.api.ServingTransfer.Dto.TransferPaymentDto;
import com.ecommerce.api.TransferMoney.Response.MessageResponse;
import com.ecommerce.api.TransferMoney.dto.*;
import com.ecommerce.api.TransferMoney.service.EmailService;
import com.ecommerce.api.TransferMoney.service.TransferService;
import com.ecommerce.api.TransferMoney.utils.TransferUtils;
import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.server.ResponseStatusException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

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

    @Autowired
    CodePinRepository codePinRepository;

    @Autowired
    PdfGeneratorServiceImpl pdfGeneratorService;

    TransferPaymentDto transferPaymentDto;


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
            if (kyc.getExpirationPieceIdentite() != null) {
                existingUser.setExpirationPieceIdentite(kyc.getExpirationPieceIdentite());
            }
            if (kyc.getDatenaissance() != null) {
                existingUser.setDatenaissance(kyc.getDatenaissance());
            }
            if (kyc.getValiditePieceIdentite() != null) {
                existingUser.setValiditePieceIdentite(kyc.getValiditePieceIdentite());
            }
            if (kyc.getPayeNationale() != null) {
                existingUser.setPayeNationale(kyc.getPayeNationale());
            }
            if (kyc.getPaysEmission() != null) {
                existingUser.setPaysEmission(kyc.getPaysEmission());
            }
            userRepo.save(existingUser);
        } else {
            // Handle the case where the user with the specified username does not exist
            throw new IllegalArgumentException("User with username " + username + " not found");
        }

    }

    @Override
    public User showKyc(String phone) {
        Optional<User> user = userRepo.findUserByPhoneNumber(phone);
        if (user.isPresent()) {
            return user.get();
        } else {
            return null;
        }

    }


    @Override
    public MessageResponse trs(TransfertDto transfertDto, long client_id, long bene_id, BeneficiaryDto bene) {

        if (transfertDto == null) {
            return new MessageResponse("transfer dto is null");
        }

        if (transfertDto.getTypeOftransfer() == TypeTransfer.ACCOUNT_DEBIT) {


            Optional<User> userOptional = userRepo.findById(client_id);
            Beneficiary beneficiary;
            Optional<User> AgentOptional = userRepo.findAgent(transfertDto.getId_agent());
            if(AgentOptional.isEmpty()){
                return new MessageResponse("Agent not found");
            }
            else {


                ExpenseManagement(transfertDto);
                String mycode0 = generateCodePin();
                if (userOptional.isPresent()) {
                    User user = userOptional.get();


                    //BigDecimal checkAmount = (transfertDto.getAmount_transfer()).divide(new BigDecimal(100));
                    MessageResponse checkAmountMessage = checkAmountOfTransfert(transfertDto, client_id);
                    if (checkAmountMessage.getMessage().equals("good Amount of transfert")) {

                        transfertDto.setGenerateRef(generateTransferReference());
                        beneficiary = selectOrAddBeneficiary(client_id, bene_id, bene);

                        DebitCreditAccount(transfertDto, user, beneficiary);

                        emailService.sendMail(
                                MailStructure.builder()
                                        .subject("Your account is debited")
                                        .recipient(user.getUsername())
                                        .message(beneficiary.getFirstName() + " " + "you send money to " + " "
                                                + beneficiary.getUsername() + " \n" + "your transfer reference : " + "  "
                                                + transfertDto.getGenerateRef() + " \n" + " "
                                                + "don't share this with anyone!"
                                                + "\n" + "  " + "your total amount is: " + transfertDto.getAmount_total()
                                                + " " + "your transfer amount: " + transfertDto.getAmount_transfer())
                                        .build());

                        if (transfertDto.isNotification()) {
                            emailService.sendMail(
                                    MailStructure.builder()
                                            .subject("Your account is credited")
                                            .recipient(beneficiary.getUsername())
                                            .message(beneficiary.getFirstName() + " " + "you received money from " + " "
                                                    + user.getUsername() + " \n" + "your transfer reference : " + "  "
                                                    + transfertDto.getGenerateRef() + " \n" + " "
                                                    + "your code pin: " + " " + mycode0 + "\n"
                                                    + "don't share this with anyone!"
                                                    + " " + "your transfer amount: " + transfertDto.getAmount_transfer())
                                            .build());

                        }


                        Transfert transfert = new Transfert();
                        transfert.setAmount_transfer(transfertDto.getAmount_transfer());
                        transfert.setTypeOftransfer(transfertDto.getTypeOftransfer());
                        transfert.setTypeOfFees(transfertDto.getFees());
                        transfert.setAmountOfFees(transferUtils.getFraiDuTransfert());
                        transfert.setStatus(TransferStatus.A_servir);
                        transfert.setClient(user);
                        transfert.setBeneficiary(beneficiary);
                        transfert.setTransferRef(transfertDto.getGenerateRef());
                        transfert.setAgent(AgentOptional.get());
                        transfertRepository.save(transfert);

                        processTransaction(user);
                        saveCodePin(mycode0, beneficiary.getUsername(), transfert);

                        return new MessageResponse("congratulations, your transaction has been successful with a good amount");
                    } else {
                        return new MessageResponse("Transfer not allowd " + checkAmountMessage.getMessage());
                    }


                } else {
                    return new MessageResponse("user not found !");
                }
            }//close de else  celle de l'agent
        } else if (transfertDto.getTypeOftransfer() == TypeTransfer.SPECIES) {
            Optional<User> clientDoneurOptional = userRepo.findById(client_id);
            Optional<User> AgentOptional = userRepo.findAgent(transfertDto.getId_agent());
            User Agent;
            Beneficiary beneficiary;
            ExpenseManagement(transfertDto);
            String mycode = generateCodePin();
            if (clientDoneurOptional.isPresent()) {
                User clientDoneur = clientDoneurOptional.get();
                if (AgentOptional.isPresent()) {
                    Agent = AgentOptional.get();

                    MessageResponse check_amount = checkAmountOfTransfert(transfertDto, transfertDto.getId_agent());
                    if (check_amount.getMessage().equals("good Amount of transfert")) {
                        transfertDto.setGenerateRef(generateTransferReference());
                        beneficiary = selectOrAddBeneficiary(client_id, bene_id, bene);
                        DebitCreditAccount(transfertDto, Agent, beneficiary);
                        emailService.sendMail(
                                MailStructure.builder()
                                        .subject("Your account is debited")
                                        .recipient(clientDoneur.getUsername())
                                        .message(clientDoneur.getName() + " " + "you send money to " + " "
                                                + beneficiary.getUsername() + " \n" + "your transfer reference : " + "  "
                                                + transfertDto.getGenerateRef() + " \n" + " "
                                                + "don't share this with anyone!"
                                                + "\n" + "  " + "your total amount is: " + transfertDto.getAmount_total()
                                                + " " + "your transfer amount: " + transfertDto.getAmount_transfer()
                                                + "Aggent est :  " + Agent.getId() + "Client  " + clientDoneur.getId())
                                        .build());

                        if (transfertDto.isNotification()) {
                            emailService.sendMail(
                                    MailStructure.builder()
                                            .subject("Your account is credited")
                                            .recipient(beneficiary.getUsername())
                                            .message(beneficiary.getFirstName() + " " + "you received money from " + " "
                                                    + clientDoneur.getUsername() + " \n" + "your transfer reference : " + "  "
                                                    + transfertDto.getGenerateRef() + " \n" + " "
                                                    + "your code pin: " + " " + mycode + "\n"
                                                    + "don't share this with anyone!"
                                                    + " " + "your transfer amount: " + transfertDto.getAmount_transfer())
                                            .build());

                        }
                        Transfert transfert = new Transfert();
                        transfert.setAmount_transfer(transfertDto.getAmount_transfer());
                        transfert.setTypeOftransfer(transfertDto.getTypeOftransfer());
                        transfert.setTypeOfFees(transfertDto.getFees());
                        transfert.setAmountOfFees(transferUtils.getFraiDuTransfert());
                        transfert.setStatus(TransferStatus.A_servir);
                        transfert.setClient(clientDoneur);
                        transfert.setAgent(Agent);
                        transfert.setBeneficiary(beneficiary);
                        transfert.setTransferRef(transfertDto.getGenerateRef());
                        transfertRepository.save(transfert);
                        processTransaction(clientDoneur);
                        saveCodePin(mycode, beneficiary.getUsername(), transfert);

                        return new MessageResponse("congratulations, your transaction has been successful with a good amount");
                    }else {
                        return new MessageResponse("Transfer not allowd " + check_amount.getMessage());
                    }

                } else {
                    return new MessageResponse("Agent not found");
                }

            } else {
                return new MessageResponse("Client not found");
            }

        } else if (transfertDto.getTypeOftransfer() == TypeTransfer.WALLET) {
            Optional<User> userOptional = userRepo.findById(client_id);
            User user;
            Beneficiary beneficiary;
            if(userOptional.isEmpty()){
                return  new MessageResponse("user not found !");
            }
            else {
                user = userOptional.get();
                beneficiary = selectOrAddBeneficiary(client_id, bene_id, bene);
                ExpenseManagement(transfertDto);  // gestion des frais
                MessageResponse check_amount = checkAmountOfTransfert(transfertDto, client_id);
                if (check_amount.getMessage().equals("good Amount of transfert")) {
                    transfertDto.setGenerateRef(generateTransferReference());
                    String codepin = generateCodePin();
                    DebitCreditAccount(transfertDto, user, beneficiary);

                    emailService.sendMail(
                            MailStructure.builder()
                                    .subject("Your account is debited")
                                    .recipient(user.getUsername())
                                    .message(beneficiary.getFirstName() + " " + "you send money to " + " "
                                            + beneficiary.getUsername() + " \n" + "your transfer reference : " + "  "
                                            + transfertDto.getGenerateRef() + " \n" + " "
                                            + "don't share this with anyone!"
                                            + "\n" + "  " + "your total amount is: " + transfertDto.getAmount_total()
                                            + " " + "your transfer amount: " + transfertDto.getAmount_transfer())
                                    .build());

                    if (transfertDto.isNotification()) {
                        emailService.sendMail(
                                MailStructure.builder()
                                        .subject("Your account is credited")
                                        .recipient(beneficiary.getUsername())
                                        .message(beneficiary.getFirstName() + " " + "you received money from " + " "
                                                + user.getUsername() + " \n" + "your transfer reference : " + "  "
                                                + transfertDto.getGenerateRef() + " \n" + " "
                                                + "your code pin: " + " " + codepin + "\n"
                                                + "don't share this with anyone!"
                                                + " " + "your transfer amount: " + transfertDto.getAmount_transfer())
                                        .build());
                    }

                    Transfert transfert = new Transfert();
                    transfert.setAmount_transfer(transfertDto.getAmount_transfer());
                    transfert.setTypeOftransfer(transfertDto.getTypeOftransfer());
                    transfert.setTypeOfFees(transfertDto.getFees());
                    transfert.setAmountOfFees(transferUtils.getFraiDuTransfert());
                    transfert.setStatus(TransferStatus.A_servir);
                    transfert.setClient(user);
                    transfert.setBeneficiary(beneficiary);
                    transfert.setTransferRef(transfertDto.getGenerateRef());
                    transfertRepository.save(transfert);

                    processTransaction(user);
                    saveCodePin(codepin, beneficiary.getUsername(), transfert);

                    return new MessageResponse("congratulations, your transaction has been successful with a good amount");
                }else{
                    return new MessageResponse(check_amount.getMessage());
                }
            }
            
        } else {
            return new MessageResponse("an error occurred this method for a transfer by account debit!" + " "
                    + transfertDto.getTypeOftransfer());
        }
    }

    @Override
    public String getOtpFromUser(String email) {
        Optional<Otp> otpEntityOptional = otpRepository.findByUsername(email);
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
    public MessageResponse checkAmountOfTransfert(TransfertDto transfertDto, long client_id) {
        Optional<User> userOptional = userRepo.findById(client_id);
        User user;
        BigDecimal checkAmount;
        if (userOptional.isPresent()) {
            user = userOptional.get();
            checkAmount = transfertDto.getAmount_transfer().divide(new BigDecimal(100));

            if (transfertDto.getAmount_transfer().compareTo(user.getAccount_amount()) > 0) {
                return new MessageResponse("Transfer amount is greater than your account amount");
            } else if (checkAmount.compareTo(transferUtils.getTransfertMax1()) > 0) {
                return new MessageResponse("Transfer amount is greater than maximum transfer limit");
            } else if (transfertDto.getAmount_entred().compareTo(transferUtils.getMinTotal()) < 0) {
                return new MessageResponse("you must make a transfer with a minimum amount of" + " " + transferUtils.MinTotal + " dh !"
                        + " actually your transfer amount is : " + transfertDto.getAmount_transfer());
            } else {
                return new MessageResponse("good Amount of transfert");
            }
        } else {
            return new MessageResponse("user not found for the given id !");
        }


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
        } else {
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

    //generate code pin
    @Override
    public String generateCodePin() {
        long randomNumber = (long) (random.nextDouble() * 1_000_0);
        DecimalFormat format = new DecimalFormat("0000");
        String formatCode = format.format(randomNumber);
        return formatCode;

    }

    @Override
    public void saveCodePin(String code, String username, Transfert transfert) {
        CodePin codee = CodePin.builder()
                .codepin(code)
                .username(username)
                .transfer(transfert)
                .build();
        codePinRepository.save(codee);


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

    @Override
    public Beneficiary SelectBene(long id_beneficiary) {
        Optional<Beneficiary> beneOpt = beneficiaryRepository.findById(id_beneficiary);
        if (beneOpt.isEmpty()) {
            return null;
        }
        return beneOpt.get();
    }

    @Override
    public Beneficiary AddBeneficiary(BeneficiaryDto beneficiaryDto, long id_user) {
        Optional<User> userOptional = userRepo.findById(id_user);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            Beneficiary beneficiary = Beneficiary.builder()
                    .firstName(beneficiaryDto.getFirstName())
                    .lastname(beneficiaryDto.getLastname())
                    .username(beneficiaryDto.getUsername())
                    .client(user)
                    .account_amount(new BigDecimal(0))
                    .build();
            beneficiaryRepository.save(beneficiary);
            return beneficiary;
        } else {
            return null;
        }


    }

    @Override
    public User ShowKycByPieceIdentite(String numeroPieceIdentite) {
        Optional<User> doneurDordreOptional = userRepo.findByNumeroPieceIdentite(numeroPieceIdentite);
        if (doneurDordreOptional.isPresent()) {
            return doneurDordreOptional.get();
        } else {
            return null;
        }

    }

    @Override
    public MessageResponse deleteCodePin(String username, long transfer_id) {
        Optional<Transfert> transfertOptional = transfertRepository.findById(transfer_id);
        if (transfertOptional.isEmpty()) {
            return new MessageResponse("transfer not found");
        } else {
            Transfert transfert = transfertOptional.get();
            if (transfert.getStatus() == TransferStatus.Payé) {
                Optional<CodePin> codeOptional = codePinRepository.findcodepinbyUsernameTransfer(username, transfert);
                if (codeOptional.isEmpty()) {
                    return new MessageResponse("code pin not found !");
                } else {
                    CodePin code = codeOptional.get();
                    codePinRepository.delete(code);
                    return new MessageResponse("code pin deleted successfully");
                }
            }else{
                return new MessageResponse("Transfer status is not 'Payé'. Cannot delete CodePin.");
            }
        }
    }

    @Override
    public List<Beneficiary> getBeneficiariesByClientId(long clientId) {
        List<Beneficiary> beneficiaries= beneficiaryRepository.findBeneficiariesByClientId(clientId);
        if(beneficiaries.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No beneficiary found for client with id " + clientId);

        }
        return beneficiaries;
    }






    @Override
    public ByteArrayOutputStream generatetransfertReceipt(Transfert transfert, User client, Beneficiary beneficiary) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        Document document = new Document(PageSize.A4);
        PdfWriter writer = PdfWriter.getInstance(document, outputStream);

        document.open();

        String imagePath = "static/images/icon_bank.png";
        Image logo = Image.getInstance(getClass().getClassLoader().getResource(imagePath));
        logo.scaleToFit(100, 100);
        logo.scaleAbsolute(100, 80);
        document.add(logo);

        Font fontTitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
        fontTitle.setSize(18);

        Paragraph title = new Paragraph("Le reçu de transfert", fontTitle);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);

        Font font = FontFactory.getFont(FontFactory.HELVETICA);
        Paragraph para = new Paragraph("Cher client,", font);
        para.setAlignment(Element.ALIGN_LEFT);
        document.add(para);
        document.add(Chunk.NEWLINE);

        Paragraph paraTwo = new Paragraph(
                "Nous vous remercions d'avoir utilisé notre service de transfert bancaire. Nous tenons à vous informer que votre transfert a été effectuée avec succès. Veuillez trouver ci-dessous les détails de la transaction :",
                font);
        paraTwo.setAlignment(Element.ALIGN_LEFT);
        document.add(paraTwo);

        PdfContentByte line = writer.getDirectContent();
        line.setLineWidth(1f);
        line.moveTo(document.left(), document.bottom() + 10);
        line.lineTo(document.right(), document.bottom() + 10);
        line.stroke();

        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentDateTime = dateFormatter.format(new Date());
        ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_RIGHT,
                new Phrase("Date: " + currentDateTime, fontTitle),
                document.right(), document.bottom() + 15, 0);

        font.setSize(12);

        document.add(Chunk.NEWLINE);

        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setSpacingBefore(20f);
        table.setSpacingAfter(30f);

        addTableCell(table, "Identifiant du transfert", String.valueOf(transfert.getId()), font);
        addTableCell(table, "Expéditeur", " " + client.getName(), font);

        addTableCell(table, "Montant du Transfert", String.valueOf(transfert.getAmount_transfer()), font);
        addTableCell(table, "Date d'émission", String.valueOf(transfert.getCreateTime()), font);
        addTableCell(table, "État", String.valueOf(transfert.getStatus()), font);
        addTableCell(table, "Référence du Transfert", String.valueOf(transfert.getTransferRef()), font);
        addTableCell(table, "Bénéficiaire",
                transfert.getBeneficiary().getFirstName() + " " + transfert.getBeneficiary().getLastname(),
                font);
        addTableCell(table, "Email du bénéficaire", transfert.getBeneficiary().getUsername(), font);
        addTableCell(table, "GSM", transfert.getBeneficiary().getGSM(), font);

        document.add(table);

        Paragraph paraT = new Paragraph(
                "Nous confirmons que votre transfert est bien effectué . Si vous avez des questions ou des préoccupations, n'hésitez pas à nous contacter. Nous sommes là pour vous aider.",
                font);
        paraT.setAlignment(Element.ALIGN_LEFT);
        document.add(paraT);

        Paragraph paraV = new Paragraph("Cordialement,", font);
        paraV.setAlignment(Element.ALIGN_LEFT);
        document.add(paraV);

        Paragraph paraM = new Paragraph("L'équipe de BankTransfer", font);
        paraM.setAlignment(Element.ALIGN_LEFT);
        document.add(paraM);

        document.close();
        writer.close();

        return outputStream;
    }

    private void addTableCell(PdfPTable table, String title, String value, Font font) {
        PdfPCell cell1 = new PdfPCell(new Phrase(title, font));
        PdfPCell cell2 = new PdfPCell(new Phrase(value, font));
        table.addCell(cell1);
        table.addCell(cell2);
    }




}
