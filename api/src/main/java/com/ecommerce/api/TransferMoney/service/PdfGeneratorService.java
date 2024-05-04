package com.ecommerce.api.TransferMoney.service;

import java.io.IOException;

import com.ecommerce.api.ServingTransfer.Dto.TransferPaymentDto;
import org.springframework.stereotype.Service;

import com.ecommerce.api.Entity.Transfert;
import com.ecommerce.api.TransferMoney.dto.BeneficiaryDto;
import com.ecommerce.api.TransferMoney.dto.TransfertDto;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.PdfPTable;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.RequestBody;

@Service
public interface PdfGeneratorService {

    void generatetransfertReceipt(@RequestBody TransferPaymentDto transferPaymentDto, HttpServletResponse response) throws IOException, DocumentException;
    void addTableCell(PdfPTable table, String key, String value, Font font);



}
