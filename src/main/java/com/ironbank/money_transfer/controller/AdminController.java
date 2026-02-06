package com.ironbank.money_transfer.controller;

import com.ironbank.money_transfer.model.BankUser;
import com.ironbank.money_transfer.repository.BankUserRepository;
import com.ironbank.money_transfer.service.TransferService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final BankUserRepository repository;
    private final TransferService transferService;

    public AdminController(BankUserRepository repository, TransferService transferService) {
        this.repository = repository;
        this.transferService = transferService;
    }

    @GetMapping("/dashboard")
    public String adminDashboard(Model model) {
        List<BankUser> allUsers = repository.findAll();

        // 1. Calculate Total Money
        BigDecimal totalReserves = allUsers.stream()
                .map(BankUser::getBalance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 2. Calculate Stats for Charts (Active vs Frozen)
        long activeCount = allUsers.stream().filter(BankUser::isActive).count();
        long frozenCount = allUsers.size() - activeCount;

        // 3. Add to Model
        model.addAttribute("users", allUsers);
        model.addAttribute("totalReserves", totalReserves);
        model.addAttribute("activeCount", activeCount);
        model.addAttribute("frozenCount", frozenCount);

        return "admin"; // Matches templates/admin.html
    }

    @PostMapping("/toggle-status/{id}")
    public String toggleUserStatus(@PathVariable Long id) {
        BankUser user = repository.findById(id).orElseThrow();
        user.setActive(!user.isActive());
        repository.save(user);
        return "redirect:/admin/dashboard?success=Status Updated";
    }

    @PostMapping("/deposit")
    public String depositMoney(@RequestParam Long userId, @RequestParam BigDecimal amount) {
        transferService.deposit(userId, amount);
        return "redirect:/admin/dashboard?success=Deposit Successful";
    }
}