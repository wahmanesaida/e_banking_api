package com.ecommerce.api.BackOffice.Dto;

import org.apache.poi.ss.usermodel.*;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.io.ByteArrayOutputStream;
import java.util.List;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import com.ecommerce.api.Entity.Transfert;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ExcelUtil {

    public static byte[] generateExcel(List<Transfert> transfers) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Transfers");

        // Create header row
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("Transfer ID");
        headerRow.createCell(1).setCellValue("Amount of transfer");
        headerRow.createCell(2).setCellValue("Type of transfer");
        headerRow.createCell(3).setCellValue("Agent Name");
        headerRow.createCell(4).setCellValue("Client Name");
        headerRow.createCell(5).setCellValue("Motif");
        headerRow.createCell(6).setCellValue("Transfer reference");
        headerRow.createCell(7).setCellValue("Amount of fees");
        headerRow.createCell(8).setCellValue("Type of fees");
        headerRow.createCell(9).setCellValue("Status");
        headerRow.createCell(10).setCellValue("CreateTime");
        headerRow.createCell(11).setCellValue("Beneficiary Name");

        // Create data rows
        int rowNum = 1;
        for (Transfert transfer : transfers) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(transfer.getId());
            row.createCell(1).setCellValue(transfer.getAmount_transfer().toString());
            row.createCell(2).setCellValue(transfer.getTypeOftransfer().toString());
            row.createCell(3).setCellValue(transfer.getAgent().getName());
            row.createCell(4).setCellValue(transfer.getClient().getName());
            row.createCell(5).setCellValue(transfer.getMotif());
            row.createCell(6).setCellValue(transfer.getTransferRef());
            row.createCell(7).setCellValue(transfer.getAmountOfFees().toString());
            row.createCell(8).setCellValue(transfer.getTypeOfFees().toString());
            row.createCell(9).setCellValue(transfer.getStatus().toString());
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String createTimeStr = transfer.getCreateTime().format(formatter);
            row.createCell(10).setCellValue(createTimeStr);
            row.createCell(11).setCellValue(
                    transfer.getBeneficiary().getFirstName() + " " + transfer.getBeneficiary().getLastname());
        }

        // Convert the workbook to a byte array
        return workbookToByteArray(workbook);
    }

    public static byte[] workbookToByteArray(Workbook workbook) throws IOException {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            workbook.write(outputStream);
            return outputStream.toByteArray();
        }
    }

}
