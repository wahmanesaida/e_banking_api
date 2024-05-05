package com.ecommerce.api.ExtouneTransfer.ServiceImpl;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import com.ecommerce.api.Entity.TransferStatus;
import com.ecommerce.api.Entity.Transfert;
import com.ecommerce.api.Entity.User;
import com.ecommerce.api.ExtouneTransfer.Service.ExtourneService;
import com.ecommerce.api.Repository.TransfertRepository;
import com.ecommerce.api.Repository.UserRepository;
import com.ecommerce.api.ServingTransfer.Dto.TransferPaymentDto;
import com.ecommerce.api.TransferMoney.service.EmailService;
import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.ColumnText;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.http.HttpServletResponse;

@Service
public class ExtourneServiceImpl implements ExtourneService {

    @Autowired
    private TransfertRepository transfertRepository;

    private static final Logger logger = LoggerFactory.getLogger(ExtourneServiceImpl.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    EmailService emailService;

    @Override
    public void generateExtourneReceipt(@RequestBody TransferPaymentDto transferPaymentDto,
            HttpServletResponse response) throws IOException, DocumentException {
        Optional<User> clientOptional = userRepository.findById(transferPaymentDto.getTransferRefDTO().getIdAgent());
        Optional<Transfert> transferOptional = transfertRepository
                .findById(transferPaymentDto.getTransferRefDTO().getId());

        User client = clientOptional.get();
        Transfert transfert = transferOptional.get();

        response.setContentType("application/pdf");
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentDateTime = dateFormatter.format(new Date());

        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=pdf_" + currentDateTime + ".pdf";
        response.setHeader(headerKey, headerValue);

        Rectangle demiPageSize = new Rectangle(PageSize.A4);

        Document document = new Document(demiPageSize);
        PdfWriter writer = PdfWriter.getInstance(document, response.getOutputStream());

        document.open();

        String imagePath = "static/images/icon_bank.png";

        // Load the image
        Image logo = Image.getInstance(getClass().getClassLoader().getResource(imagePath));
        logo.scaleToFit(100, 100);
        logo.scaleAbsolute(100, 80);

        // Add the image to the document
        document.add(logo);

        Font fontTitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
        fontTitle.setSize(18);

        Paragraph title = new Paragraph("Le reçu de l'extourne du transfert", fontTitle);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);
        Font font = FontFactory.getFont(FontFactory.HELVETICA);

        Paragraph para = new Paragraph("Cher client,", font);
        para.setAlignment(Element.ALIGN_LEFT);
        document.add(para);
        document.add(Chunk.NEWLINE);

        Paragraph paraTwo = new Paragraph(
                "Nous vous remercions d'avoir utilisé notre service de transfert bancaire. Nous tenons à vous informer qu'une extourne a été effectuée sur votre transfert. Veuillez trouver ci-dessous les détails de la transaction :",
                font);
        paraTwo.setAlignment(Element.ALIGN_LEFT);
        document.add(paraTwo);

        // Add line at the bottom
        PdfContentByte line = writer.getDirectContent();
        line.setLineWidth(1f);
        line.moveTo(document.left(), document.bottom() + 10);
        line.lineTo(document.right(), document.bottom() + 10);
        line.stroke();

        // Add date in the right corner above the line
        ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_RIGHT,
                new Phrase("Date: " + currentDateTime, fontTitle),
                document.right(), document.bottom() + 15, 0);

        font.setSize(12);

        document.add(Chunk.NEWLINE);

        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setSpacingBefore(20f);
        table.setSpacingAfter(30f);

        PdfPTable table2 = new PdfPTable(2);
        table2.setWidthPercentage(100);
        table2.setSpacingBefore(20f);
        table2.setSpacingAfter(30f);

        addTableCell(table, "Identifiant du transfert", String.valueOf(transfert.getId()), font);
        addTableCell(table, "Expéditeur",
                " " + client.getName(), font);
        addTableCell(table, "Montant du Transfert", String.valueOf(transfert.getAmount_transfer()), font);
        addTableCell(table, "Date d'émission", String.valueOf(transfert.getCreateTime()), font);
        addTableCell(table, "État", String.valueOf(transfert.getStatus()), font);
        addTableCell(table, "Réference du Transfert", String.valueOf(transfert.getTransferRef()), font);

        addTableCell(table2, "Identifiant du transfert", String.valueOf(transfert.getId()), font);
        addTableCell(table2, "Bénéficiaire",
                transfert.getBeneficiary().getFirstName() + " " + transfert.getBeneficiary().getLastname(),
                font);
        addTableCell(table2, "Montant du Transfert", String.valueOf(transfert.getAmount_transfer()), font);
        addTableCell(table2, "Date d'émission", String.valueOf(transfert.getCreateTime()), font);
        addTableCell(table2, "État", String.valueOf(transfert.getStatus()), font);
        addTableCell(table2, "Réference du Transfert", String.valueOf(transfert.getTransferRef()), font);

        document.add(table);
        document.add(table2);

        Paragraph paraT = new Paragraph(
                "Nous confirmons que l'agent a bien effectué l'extourne du transfert. Si vous avez des questions ou des préoccupations, n'hésitez pas à nous contacter. Nous sommes là pour vous aider.",
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

    }

    @Override
    public void addTableCell(PdfPTable table, String key, String value, Font font) {
        PdfPCell cell1 = new PdfPCell(new Phrase(key, font));
        cell1.setBorder(Rectangle.BOX); // Set cell border to be a box
        cell1.setPadding(5); // Set cell padding to 5
        PdfPCell cell2 = new PdfPCell(new Phrase(value, font));
        cell2.setBorder(Rectangle.BOX); // Set cell border to be a box
        cell2.setPadding(5); // Set cell padding to 5
        table.addCell(cell1);
        table.addCell(cell2);
    }

    @Override
    public void reverseTransfer(@RequestBody TransferPaymentDto transferPaymentDto, HttpServletResponse response)
            throws DocumentException, IOException {
        String transferRef = transferPaymentDto.getTransferRefDTO().getTransferRef();
        Optional<Transfert> optionalTransfert = transfertRepository.findByTransferRef(transferRef);
        Optional<User> optionalUser = userRepository.findById(transferPaymentDto.getTransferRefDTO().getIdAgent());

        if (optionalTransfert.isPresent()) {
            Transfert transfert = optionalTransfert.get();
            if (transfert.getStatus().equals(TransferStatus.A_servir)) {
                if (isSameDay(transfert.getCreateTime(), new Date())) {
                    transfert.setMotif(transferPaymentDto.getTransferRefDTO().getMotif());
                                        if (optionalUser.isPresent()) {
                        User existUser = optionalUser.get();
                        BigDecimal transferAmount = transferPaymentDto.getTransferRefDTO().getAmount_transfer();
                        BigDecimal userAccountAmount = existUser.getAccount_amount();

                        BigDecimal newAccountAmount = userAccountAmount.add(transferAmount);
                        existUser.setAccount_amount(newAccountAmount);
                        userRepository.save(existUser);
                    } else {
                        throw new NoSuchElementException(
                                "Agent not found for ID: " + transferPaymentDto.getTransferRefDTO().getIdAgent());
                    }

                    transfert.setStatus(TransferStatus.Extourné);
                    transfertRepository.save(transfert);

                }else{
                    throw new NoSuchElementException("The transfer is not initiated on the same day and by the same agent");
                }


            }else{
                throw new NoSuchElementException("Transfer is already paid or blocked for transfer reference: " + transferRef);
            }

        } else {
            throw new NoSuchElementException("Transfer not found for reference: " + transferRef);
        }

    }

    @Override
    public boolean isSameDay(LocalDateTime date1, Date date2) {
        Instant instant = date2.toInstant();
        ZoneId zoneId = ZoneId.systemDefault();
        LocalDate localDate = instant.atZone(zoneId).toLocalDate();
        return date1.toLocalDate().equals(localDate);
    }
}
