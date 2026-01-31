package com.ironbank.money_transfer.controller;

import com.ironbank.money_transfer.model.BankUser;
import com.ironbank.money_transfer.model.Transaction;
import com.ironbank.money_transfer.repository.BankUserRepository;
import com.ironbank.money_transfer.repository.TransactionRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import com.ironbank.money_transfer.repository.BeneficiaryRepository;
import com.ironbank.money_transfer.model.Beneficiary;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.ironbank.money_transfer.service.PdfService;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

import java.io.ByteArrayInputStream;
import java.security.Principal;
import java.util.List;

@Controller
public class WebController {

    private final BankUserRepository repository;
    private final TransactionRepository transactionRepository;
    private final BeneficiaryRepository beneficiaryRepository;
    private final PdfService pdfService;

    public WebController(BankUserRepository repository, TransactionRepository transactionRepository,BeneficiaryRepository beneficiaryRepository,PdfService pdfService) {
        this.repository = repository;
        this.transactionRepository = transactionRepository;
        this.beneficiaryRepository = beneficiaryRepository;
        this.pdfService = pdfService;
    }

    // 🏠 Dashboard: ONLY shows Balance & History
//    @GetMapping("/")
//    public String home(Model model, Principal principal) {
//        String username = principal.getName();
//        BankUser currentUser = repository.findByUsername(username).orElseThrow();
//
//        // Fetch transaction history
//        List<Transaction> myHistory = transactionRepository.findBySenderNameOrReceiverName(username, username);
//
//        model.addAttribute("username", username);
//        model.addAttribute("balance", currentUser.getBalance());
//        model.addAttribute("accountNumber", currentUser.getAccountNumber());
//        model.addAttribute("transactions", myHistory);
//
//        return "dashboard";
//    }

    @GetMapping("/")
    public String home(Model model, Principal principal) {
        String username = principal.getName();
        BankUser currentUser = repository.findByUsername(username).orElseThrow();

        // 1. Get History
        List<Transaction> myHistory = transactionRepository.findBySenderNameOrReceiverName(username, username);

        // 2. NEW: Get My Saved Contacts
        List<Beneficiary> myContacts = beneficiaryRepository.findByUserId(currentUser.getId());

        model.addAttribute("username", username);
        model.addAttribute("balance", currentUser.getBalance());
        model.addAttribute("accountNumber", currentUser.getAccountNumber());
        model.addAttribute("transactions", myHistory);
        model.addAttribute("contacts", myContacts); // Send contacts to HTML

        return "dashboard";
    }

    // NEW: Save a Friend
    @PostMapping("/add-contact")
    public String addContact(@RequestParam String name,
                             @RequestParam String accountNumber,
                             Principal principal) {

        String username = principal.getName();
        BankUser currentUser = repository.findByUsername(username).orElseThrow();

        // Save
        Beneficiary contact = new Beneficiary(currentUser, name, accountNumber, "");
        beneficiaryRepository.save(contact);

        return "redirect:/?success=Contact Added";
    }

    @GetMapping("/download-receipt/{id}")
    public ResponseEntity<InputStreamResource> downloadReceipt(@PathVariable Long id, Principal principal) {

        // 1. Find the transaction
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));

        // 2. Security Check (Ensure the logged-in user is part of this transaction)
        String currentUsername = principal.getName();
        if (!transaction.getSenderName().equals(currentUsername) &&
                !transaction.getReceiverName().equals(currentUsername)) {
            throw new RuntimeException("Unauthorized access to receipt");
        }

        // 3. Generate PDF
        ByteArrayInputStream pdfStream = pdfService.generateReceipt(transaction);

        // 4. Return as a file download
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=receipt_" + id + ".pdf");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(pdfStream));
    }
    // ❌ DELETED: /sendMoney (Moved to TransferController)
    // ❌ DELETED: /verify-otp (Moved to TransferController)
    // ❌ DELETED: /process-otp (Moved to TransferController)
}