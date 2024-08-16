package com.burak.safa_bank_app.service.impl;

import com.burak.safa_bank_app.dto.EmailDetails;
import com.burak.safa_bank_app.entity.Transaction;
import com.burak.safa_bank_app.entity.User;
import com.burak.safa_bank_app.repository.TransactionRepository;
import com.burak.safa_bank_app.repository.UserRepository;
import com.itextpdf.text.*;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class BankStatement {

    private final TransactionRepository transactionRepository;
    private static final String DIRECTORY = "D:\\bankStatements\\";

    private UserRepository userRepository;

    private EmailService emailService;

    public List<Transaction> generateStatement(String accountNumber, String startDate, String endDate) throws DocumentException, FileNotFoundException {
        LocalDate start;
        LocalDate end;
        try {
            start = LocalDate.parse(startDate, DateTimeFormatter.ISO_DATE);
            end = LocalDate.parse(endDate, DateTimeFormatter.ISO_DATE);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid date format. Expected format is yyyy-MM-dd.", e);
        }
        List<Transaction> transactionList = transactionRepository.findByAccountNumberAndCreatedAtBetween(accountNumber, start, end);
        designStatement(transactionList, start, end, accountNumber);
        return transactionList;
    }

    private void designStatement(List<Transaction> transactionList, LocalDate startDate, LocalDate endDate, String accountNumber) throws FileNotFoundException, DocumentException {

        User user = userRepository.findByAccountNumber(accountNumber);
        String customerName = user.getFirstName() + " " + user.getLastName() + " " + user.getOtherName();

        String timestamp = String.valueOf(System.currentTimeMillis());
        String uniqueFilename = DIRECTORY + "BankStatement_" + accountNumber + "_" + timestamp + ".pdf";

        Rectangle statementSize = new Rectangle(PageSize.A4);
        Document document = new Document(statementSize);
        log.info("setting size of document");
        OutputStream outputStream = new FileOutputStream(uniqueFilename);
        PdfWriter.getInstance(document, outputStream);
        document.open();

        // Bank Info Table
        PdfPTable bankInfoTable = new PdfPTable(1);
        PdfPCell bankName = new PdfPCell(new Phrase("Safe Bank"));
        bankName.setBorder(Rectangle.NO_BORDER);
        bankName.setBackgroundColor(BaseColor.CYAN);
        bankName.setPadding(20f);
        bankName.setHorizontalAlignment(Element.ALIGN_CENTER);

        PdfPCell banksAddress = new PdfPCell(new Phrase("123 Abc Street Istanbul Turkey"));
        banksAddress.setBorder(Rectangle.NO_BORDER);
        banksAddress.setPadding(10f);
        banksAddress.setHorizontalAlignment(Element.ALIGN_CENTER);

        bankInfoTable.addCell(bankName);
        bankInfoTable.addCell(banksAddress);

        // Statement Info Table
        PdfPTable statementInfo = new PdfPTable(2);
        statementInfo.setWidthPercentage(100);
        statementInfo.setSpacingBefore(20f);
        statementInfo.setSpacingAfter(20f);

        PdfPCell customerInfo = new PdfPCell(new Phrase("Start Date: " + startDate.toString()));
        customerInfo.setBorder(Rectangle.NO_BORDER);
        customerInfo.setPadding(10f);

        PdfPCell statement = new PdfPCell(new Phrase("Statement of Account"));
        statement.setBorder(Rectangle.NO_BORDER);
        statement.setPadding(10f);
        statement.setHorizontalAlignment(Element.ALIGN_RIGHT);

        PdfPCell stopDate = new PdfPCell(new Phrase("End Date: " + endDate.toString()));
        stopDate.setBorder(Rectangle.NO_BORDER);
        stopDate.setPadding(10f);

        PdfPCell name = new PdfPCell(new Phrase("Customer name: " + customerName));
        name.setBorder(Rectangle.NO_BORDER);
        name.setPadding(10f);
        name.setHorizontalAlignment(Element.ALIGN_RIGHT);

        PdfPCell address = new PdfPCell(new Phrase("Address: " + user.getAddress()));
        address.setBorder(Rectangle.NO_BORDER);
        address.setPadding(10f);
        address.setColspan(2);

        statementInfo.addCell(customerInfo);
        statementInfo.addCell(statement);
        statementInfo.addCell(stopDate);
        statementInfo.addCell(name);
        statementInfo.addCell(address);

        // Transaction Table
        PdfPTable transactionTable = new PdfPTable(4);
        transactionTable.setWidthPercentage(100);
        transactionTable.setSpacingBefore(20f);
        transactionTable.setSpacingAfter(20f);

        PdfPCell date = new PdfPCell(new Phrase("DATE"));
        date.setBackgroundColor(BaseColor.CYAN);
        date.setPadding(10f);
        date.setHorizontalAlignment(Element.ALIGN_CENTER);

        PdfPCell transactionType = new PdfPCell(new Phrase("TRANSACTION TYPE"));
        transactionType.setBackgroundColor(BaseColor.CYAN);
        transactionType.setPadding(10f);
        transactionType.setHorizontalAlignment(Element.ALIGN_CENTER);

        PdfPCell transactionAmount = new PdfPCell(new Phrase("TRANSACTION AMOUNT"));
        transactionAmount.setBackgroundColor(BaseColor.CYAN);
        transactionAmount.setPadding(10f);
        transactionAmount.setHorizontalAlignment(Element.ALIGN_CENTER);

        PdfPCell status = new PdfPCell(new Phrase("STATUS"));
        status.setBackgroundColor(BaseColor.CYAN);
        status.setPadding(10f);
        status.setHorizontalAlignment(Element.ALIGN_CENTER);

        transactionTable.addCell(date);
        transactionTable.addCell(transactionType);
        transactionTable.addCell(transactionAmount);
        transactionTable.addCell(status);

        boolean alternateColor = false;
        for (Transaction transaction : transactionList) {
            BaseColor rowColor = alternateColor ? BaseColor.LIGHT_GRAY : BaseColor.WHITE;
            PdfPCell dateCell = new PdfPCell(new Phrase(transaction.getCreatedAt().toString()));
            dateCell.setPadding(10f);
            dateCell.setBackgroundColor(rowColor);
            dateCell.setHorizontalAlignment(Element.ALIGN_CENTER);

            PdfPCell typeCell = new PdfPCell(new Phrase(transaction.getTransactionType()));
            typeCell.setPadding(10f);
            typeCell.setBackgroundColor(rowColor);
            typeCell.setHorizontalAlignment(Element.ALIGN_CENTER);

            PdfPCell amountCell = new PdfPCell(new Phrase(transaction.getAmount().toString()));
            amountCell.setPadding(10f);
            amountCell.setBackgroundColor(rowColor);
            amountCell.setHorizontalAlignment(Element.ALIGN_CENTER);

            PdfPCell statusCell = new PdfPCell(new Phrase(transaction.getStatus()));
            statusCell.setPadding(10f);
            statusCell.setBackgroundColor(rowColor);
            statusCell.setHorizontalAlignment(Element.ALIGN_CENTER);

            transactionTable.addCell(dateCell);
            transactionTable.addCell(typeCell);
            transactionTable.addCell(amountCell);
            transactionTable.addCell(statusCell);

            alternateColor = !alternateColor;
        }


        document.add(bankInfoTable);
        document.add(statementInfo);
        document.add(transactionTable);

        document.close();

        EmailDetails emailDetails = EmailDetails.builder()
                .recipient(user.getEmail())
                .subject("STATEMENT OF ACCOUNT")
                .messageBody("You can find your account statement attached.")
                .attachment(uniqueFilename)
                .build();

        emailService.sendEmailWithAttachment(emailDetails);
    }

}
