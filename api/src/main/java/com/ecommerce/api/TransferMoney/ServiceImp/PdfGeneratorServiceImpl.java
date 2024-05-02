package com.ecommerce.api.TransferMoney.ServiceImp;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

import com.ecommerce.api.Repository.TransfertRepository;
import com.ecommerce.api.ServingTransfer.Dto.TransferPaymentDto;
import com.lowagie.text.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ecommerce.api.Entity.Beneficiary;
import com.ecommerce.api.Entity.Transfert;
import com.ecommerce.api.Entity.User;
import com.ecommerce.api.Repository.BeneficiaryRepository;
import com.ecommerce.api.Repository.UserRepository;
import com.ecommerce.api.TransferMoney.dto.BeneficiaryDto;
import com.ecommerce.api.TransferMoney.dto.TransfertDto;
import com.ecommerce.api.TransferMoney.service.PdfGeneratorService;

import com.lowagie.text.pdf.ColumnText;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.RequestBody;

@Service
public class PdfGeneratorServiceImpl implements PdfGeneratorService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    BeneficiaryRepository beneficiaryRepository;

    @Autowired
    TransfertRepository transfertRepository;

    @Override
    public void generatetransfertReceipt(@RequestBody TransferPaymentDto transferPaymentDto, HttpServletResponse response) throws IOException, DocumentException {

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

        Paragraph title = new Paragraph("Le reçu de réstitution du transfert", fontTitle);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);
        Font font = FontFactory.getFont(FontFactory.HELVETICA);

        Paragraph para = new Paragraph("Cher client,", font);
        para.setAlignment(Element.ALIGN_LEFT);
        document.add(para);
        document.add(Chunk.NEWLINE);

        Paragraph paraTwo = new Paragraph(
                "Nous vous remercions d'avoir utilisé notre service de transfert bancaire. Nous tenons à vous informer qu'une réstitution a été effectuée sur votre transfert. Veuillez trouver ci-dessous les détails de la transaction :",
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
                "Nous confirmons que vous avez  bien effectué la réstitution du transfert. Si vous avez des questions ou des préoccupations, n'hésitez pas à nous contacter. Nous sommes là pour vous aider.",
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

}
