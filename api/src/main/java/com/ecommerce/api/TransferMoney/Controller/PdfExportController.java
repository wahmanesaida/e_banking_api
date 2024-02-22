package com.ecommerce.api.TransferMoney.Controller;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ecommerce.api.Entity.Transfert;
import com.ecommerce.api.Repository.TransfertRepository;
import com.ecommerce.api.TransferMoney.ServiceImp.PdfGeneratorServiceImpl;
import com.ecommerce.api.TransferMoney.dto.BeneficiaryDto;
import com.ecommerce.api.TransferMoney.dto.TransfertDto;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth/")
public class PdfExportController {


     private final PdfGeneratorServiceImpl pdfGeneratorService;

    private final TransfertRepository transfertRepository;

    TransfertDto transfert;

    @GetMapping("/pdf/generate")
    public void generatePdf(@RequestParam Long id_client,@RequestParam Long id_bene, @RequestBody BeneficiaryDto bene, HttpServletResponse response) throws IOException{
        /* Transfert transfert = transfertRepository.findById(id).orElse(null);
        if (transfert == null) {
            throw new RuntimeException("Transfert not found");
        } */

       
        

        response.setContentType("application/pdf");
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentDateTime = dateFormatter.format(new Date());

        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=pdf_"+ currentDateTime +".pdf";
        response.setHeader(headerKey, headerValue);

        pdfGeneratorService.export(response,transfert, id_client,id_bene, bene);
    }

}
