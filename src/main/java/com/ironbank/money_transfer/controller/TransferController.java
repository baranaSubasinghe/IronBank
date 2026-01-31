package com.ironbank.money_transfer.controller;

import com.ironbank.money_transfer.dto.TransferRequest;
import com.ironbank.money_transfer.model.BankUser;
import com.ironbank.money_transfer.repository.BankUserRepository;
import com.ironbank.money_transfer.service.NotificationService;
import com.ironbank.money_transfer.service.OtpService;
import com.ironbank.money_transfer.service.TransferService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.security.Principal;

@Controller
public class TransferController {

    private final BankUserRepository repository;
    private final TransferService transferService;
    private final OtpService otpService;
    private final NotificationService notificationService;

    public TransferController(BankUserRepository repository,
                              TransferService transferService,
                              OtpService otpService,
                              NotificationService notificationService) {
        this.repository = repository;
        this.transferService = transferService;
        this.otpService = otpService;
        this.notificationService = notificationService;
    }

    // 💸 Step 1: Initiate Transfer (Validations + Generate OTP)
    @PostMapping("/sendMoney")
    public String initiateTransfer(@RequestParam String receiverAccountNumber, // String Input
                                   @RequestParam BigDecimal amount,
                                   Principal principal,
                                   HttpSession session) {

        String username = principal.getName();
        BankUser sender = repository.findByUsername(username).orElseThrow();

        // 1. Validation: Cannot send to self
        if (sender.getAccountNumber().equals(receiverAccountNumber)) {
            return "redirect:/?error=Cannot send money to yourself";
        }

        // 2. Validation: Check Balance
        if (sender.getBalance().compareTo(amount) < 0) {
            return "redirect:/?error=Insufficient Funds";
        }

        // 3. Validation: Check Receiver Exists (by Account Number)
        if (repository.findByAccountNumber(receiverAccountNumber).isEmpty()) {
            return "redirect:/?error=Invalid Account Number";
        }

        // 4. Generate OTP
        String otpCode = otpService.generateOtp();

        // 5. Send OTP (Simulated Email)
        notificationService.sendWelcomeEmail(sender.getEmail(), "Your OTP is: " + otpCode);
        System.out.println("🔐 OTP for " + username + ": " + otpCode);

        // 6. Store in Session
        TransferRequest request = new TransferRequest();
        request.setSenderId(sender.getId());
        request.setReceiverAccountNumber(receiverAccountNumber); // Store String
        request.setAmount(amount);
        request.setSecretOtp(otpCode);

        session.setAttribute("pendingTransfer", request);

        return "redirect:/verify-otp";
    }

    // 🔐 Step 2: Show OTP Page
    @GetMapping("/verify-otp")
    public String verifyOtpPage() {
        return "verify_otp";
    }

    // ✅ Step 3: Process OTP & Execute Transfer
    @PostMapping("/process-otp")
    public String processOtp(@RequestParam String otpInput, HttpSession session) {

        // Retrieve the temporary data
        TransferRequest request = (TransferRequest) session.getAttribute("pendingTransfer");

        if (request == null) {
            return "redirect:/?error=Session Expired";
        }

        // Check if OTP matches
        if (request.getSecretOtp().equals(otpInput)) {
            try {
                // Call the Service (Fixed to accept String Account Number)
                transferService.transferMoney(
                        request.getSenderId(),
                        request.getReceiverAccountNumber(),
                        request.getAmount()
                );

                session.removeAttribute("pendingTransfer"); // Clean up
                return "redirect:/?success=Transfer Complete";

            } catch (Exception e) {
                return "redirect:/?error=" + e.getMessage();
            }
        } else {
            return "redirect:/verify-otp?error=Invalid OTP";
        }
    }
}