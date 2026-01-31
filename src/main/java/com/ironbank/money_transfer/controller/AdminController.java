package com.ironbank.money_transfer.controller;

import com.ironbank.money_transfer.model.BankUser;
import com.ironbank.money_transfer.repository.BankUserRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.List;
import com.ironbank.money_transfer.service.TransferService;

@Controller
@RequestMapping("/admin") // All URLs start with /admin
public class AdminController {

    private final BankUserRepository repository;
    private final TransferService transferService;

    public AdminController(BankUserRepository repository,TransferService transferService) {
        this.repository = repository;
        this.transferService = transferService;

    }

    @GetMapping("/dashboard")
    public String adminDashboard(Model model) {
        List<BankUser> allUsers = repository.findAll();

        // Calculate Total Money in the Bank
        BigDecimal totalReserves = allUsers.stream()
                .map(BankUser::getBalance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        model.addAttribute("users", allUsers);
        model.addAttribute("totalReserves", totalReserves);

        return "admin_dashboard";
    }

    @PostMapping("/toggle-status/{id}")
    public String toggleUserStatus(@PathVariable Long id) {
        BankUser user = repository.findById(id).orElseThrow();

        // Flip the switch (True -> False, False -> True)
        user.setActive(!user.isActive());
        repository.save(user);

        return "redirect:/admin/dashboard";
    }

    @PostMapping("/deposit")
    public String depositMoney(@RequestParam Long userId, @RequestParam BigDecimal amount) {
        transferService.deposit(userId, amount);
        return "redirect:/admin/dashboard";
    }
}