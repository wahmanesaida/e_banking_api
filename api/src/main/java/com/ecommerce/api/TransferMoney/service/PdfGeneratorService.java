package com.ecommerce.api.TransferMoney.service;

import java.io.IOException;

import org.springframework.stereotype.Service;

import com.ecommerce.api.Entity.Transfert;
import com.ecommerce.api.TransferMoney.dto.BeneficiaryDto;
import com.ecommerce.api.TransferMoney.dto.TransfertDto;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.PdfPTable;

import jakarta.servlet.http.HttpServletResponse;

@Service
public interface PdfGeneratorService {

    void export(HttpServletResponse response,TransfertDto transfertDto, long client_id, long bene_id, BeneficiaryDto bene) throws IOException, DocumentException;

    void addTableCell(PdfPTable table, String key, String value, Font font);




}
