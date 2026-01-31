package com.ironbank.money_transfer.service;

import com.ironbank.money_transfer.model.BankUser;
import com.ironbank.money_transfer.model.Transaction;
import com.ironbank.money_transfer.repository.BankUserRepository;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;

@Service
public class PdfService {

    private final BankUserRepository userRepository;

    // Define minimal colors
    private final Color BRAND_COLOR = new Color(44, 62, 80);    // Dark Navy
    private final Color ACCENT_COLOR = new Color(39, 174, 96);  // Elegant Green
    private final Color LIGHT_GRAY = new Color(240, 242, 245);  // Soft Background
    private final Color TEXT_GRAY = new Color(100, 100, 100);   // For labels

    public PdfService(BankUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public ByteArrayInputStream generateReceipt(Transaction transaction) {
        Document document = new Document(PageSize.A4);
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            // ==========================================
            // 1. HEADER (Logo + Title)
            // ==========================================
            PdfPTable headerTable = new PdfPTable(2);
            headerTable.setWidthPercentage(100);
            headerTable.setWidths(new float[]{1, 2}); // Logo takes 1 part, Title takes 2 parts

            // Logo Cell
            PdfPCell logoCell = new PdfPCell();
            logoCell.setBorder(PdfPCell.NO_BORDER);
            try {
                Image logo = Image.getInstance(getClass().getResource("/static/images/logo.png"));
                logo.scaleToFit(80, 80);
                logoCell.addElement(logo);
            } catch (Exception e) {
                // Fallback text if image is missing
                Font logoFallback = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, BRAND_COLOR);
                logoCell.addElement(new Paragraph("IRON BANK", logoFallback));
            }
            headerTable.addCell(logoCell);

            // Title Cell
            PdfPCell titleCell = new PdfPCell();
            titleCell.setBorder(PdfPCell.NO_BORDER);
            titleCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            titleCell.setHorizontalAlignment(Element.ALIGN_RIGHT);

            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 22, BRAND_COLOR);
            Paragraph title = new Paragraph("TRANSACTION RECEIPT", titleFont);
            title.setAlignment(Element.ALIGN_RIGHT);
            titleCell.addElement(title);

            headerTable.addCell(titleCell);
            document.add(headerTable);

            // Spacer
            document.add(new Paragraph("\n"));
            document.add(new Paragraph("\n"));

            // ==========================================
            // 2. TRANSACTION METADATA (Date, Time, Ref)
            // ==========================================
            PdfPTable metaTable = new PdfPTable(2);
            metaTable.setWidthPercentage(100);

            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy");
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a");

            addMetaRow(metaTable, "Transaction Date", transaction.getTimestamp().format(dateFormatter));
            addMetaRow(metaTable, "Transaction Time", transaction.getTimestamp().format(timeFormatter));
            addMetaRow(metaTable, "Reference ID", "#TXN-" + transaction.getId());

            document.add(metaTable);
            document.add(new Paragraph("\n"));

            // ==========================================
            // 3. SENDER & RECEIVER CARD
            // ==========================================

            // Fetch Users safely
            BankUser sender = userRepository.findByUsername(transaction.getSenderName()).orElse(null);
            BankUser receiver = userRepository.findByUsername(transaction.getReceiverName()).orElse(null);

            String senderAcc = (sender != null) ? formatAccount(sender.getAccountNumber()) : "N/A";
            String receiverAcc = (receiver != null) ? formatAccount(receiver.getAccountNumber()) : "N/A";

            PdfPTable detailsTable = new PdfPTable(2);
            detailsTable.setWidthPercentage(100);
            detailsTable.setSpacingBefore(10f);
            detailsTable.setWidths(new float[]{1, 1}); // Equal width

            // -- Sender Column --
            PdfPCell senderCell = new PdfPCell();
            senderCell.setBorderColor(Color.LIGHT_GRAY);
            senderCell.setPadding(15f);

            senderCell.addElement(new Paragraph("FROM", FontFactory.getFont(FontFactory.HELVETICA, 10, TEXT_GRAY)));
            senderCell.addElement(new Paragraph(transaction.getSenderName().toUpperCase(), FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, BRAND_COLOR)));
            senderCell.addElement(new Paragraph(senderAcc, FontFactory.getFont(FontFactory.COURIER, 12, Color.DARK_GRAY)));

            detailsTable.addCell(senderCell);

            // -- Receiver Column --
            PdfPCell receiverCell = new PdfPCell();
            receiverCell.setBorderColor(Color.LIGHT_GRAY);
            receiverCell.setPadding(15f);

            receiverCell.addElement(new Paragraph("TO", FontFactory.getFont(FontFactory.HELVETICA, 10, TEXT_GRAY)));
            receiverCell.addElement(new Paragraph(transaction.getReceiverName().toUpperCase(), FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, BRAND_COLOR)));
            receiverCell.addElement(new Paragraph(receiverAcc, FontFactory.getFont(FontFactory.COURIER, 12, Color.DARK_GRAY)));

            detailsTable.addCell(receiverCell);
            document.add(detailsTable);

            // ==========================================
            // 4. AMOUNT SECTION (Clean & Big)
            // ==========================================
            document.add(new Paragraph("\n"));

            PdfPTable amountTable = new PdfPTable(1);
            amountTable.setWidthPercentage(100);

            PdfPCell amountCell = new PdfPCell();
            amountCell.setBackgroundColor(LIGHT_GRAY); // Very subtle gray bg
            amountCell.setBorder(PdfPCell.NO_BORDER);
            amountCell.setPaddingTop(20f);
            amountCell.setPaddingBottom(20f);
            amountCell.setHorizontalAlignment(Element.ALIGN_CENTER);

            Paragraph amountLabel = new Paragraph("TOTAL AMOUNT TRANSFERRED", FontFactory.getFont(FontFactory.HELVETICA, 10, TEXT_GRAY));
            amountLabel.setAlignment(Element.ALIGN_CENTER);
            amountCell.addElement(amountLabel);

            Font amountFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 24, ACCENT_COLOR);
            Paragraph amountValue = new Paragraph("Rs. " + String.format("%.2f", transaction.getAmount()), amountFont);
            amountValue.setAlignment(Element.ALIGN_CENTER);
            amountCell.addElement(amountValue);

            amountTable.addCell(amountCell);
            document.add(amountTable);

            // ==========================================
            // 5. FOOTER
            // ==========================================
            document.add(new Paragraph("\n\n"));
            Paragraph footer = new Paragraph("Thank you for banking with Iron Bank.\nFor support, contact support@ironbank.com",
                    FontFactory.getFont(FontFactory.HELVETICA, 10, TEXT_GRAY));
            footer.setAlignment(Element.ALIGN_CENTER);
            document.add(footer);

            document.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return new ByteArrayInputStream(out.toByteArray());
    }

    // Helper for Meta Data Rows
    private void addMetaRow(PdfPTable table, String label, String value) {
        PdfPCell labelCell = new PdfPCell(new Paragraph(label, FontFactory.getFont(FontFactory.HELVETICA, 11, TEXT_GRAY)));
        labelCell.setBorder(PdfPCell.BOTTOM);
        labelCell.setBorderColor(new Color(230, 230, 230)); // Very faint line
        labelCell.setPaddingBottom(8f);
        labelCell.setPaddingTop(8f);
        table.addCell(labelCell);

        PdfPCell valueCell = new PdfPCell(new Paragraph(value, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, Color.BLACK)));
        valueCell.setBorder(PdfPCell.BOTTOM);
        valueCell.setBorderColor(new Color(230, 230, 230));
        valueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        valueCell.setPaddingBottom(8f);
        valueCell.setPaddingTop(8f);
        table.addCell(valueCell);
    }

    // Helper to shorten/format long account numbers if needed
    private String formatAccount(String acc) {
        if (acc == null) return "";
        return acc.length() > 20 ? acc.substring(0, 20) + "..." : acc;
    }
}