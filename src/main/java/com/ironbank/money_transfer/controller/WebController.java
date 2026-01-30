package com.ironbank.money_transfer.controller;

import com.ironbank.money_transfer.model.BankUser;
import com.ironbank.money_transfer.repository.BankUserRepository;
import com.ironbank.money_transfer.repository.TransactionRepository; // ⚠️ Don't forget this import!
import com.ironbank.money_transfer.service.TransferService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.List;

@Controller
public class WebController {

    private final BankUserRepository repository;
    private final TransferService transferService;
    private final TransactionRepository transactionRepository; // 1. New Field

    // 2. Updated Constructor to inject TransactionRepository
    public WebController(BankUserRepository repository,
                         TransferService transferService,
                         TransactionRepository transactionRepository) {
        this.repository = repository;
        this.transferService = transferService;
        this.transactionRepository = transactionRepository;
    }

    @GetMapping("/")
    public String home(Model model) {
        // Fetch all users
        List<BankUser> users = repository.findAll();

        // Fetch all transactions (History)
        var transactions = transactionRepository.findAll(); // 3. Get history

        // Put them in the "Model" so HTML can see them
        model.addAttribute("users", users);
        model.addAttribute("transactions", transactions); // 4. Send history to HTML

        return "dashboard";
    }

    @PostMapping("/sendMoney")
    public String sendMoney(@RequestParam Long senderId,
                            @RequestParam Long receiverId,
                            @RequestParam BigDecimal amount,
                            Model model) { // 1. Add 'Model' here to send data back

        try {
            // Try to move money
            transferService.transferMoney(senderId, receiverId, amount);

            // If successful, go back home cleanly
            return "redirect:/";

        } catch (RuntimeException e) {
            // 2. IF it fails (caught the error!), do this:

            // Log it for us developers
            System.out.println("🔴 Transfer Failed: " + e.getMessage());

            // Add the error message to the Model so HTML can see it
            model.addAttribute("errorMessage", e.getMessage());

            // RELOAD the data (because we are staying on the same page)
            model.addAttribute("users", repository.findAll());
            model.addAttribute("transactions", transactionRepository.findAll());

            // Return to the dashboard (NOT redirect) to show the error
            return "dashboard";
        }
    }
}