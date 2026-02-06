package com.ironbank.money_transfer.controller;

import com.ironbank.money_transfer.model.BankUser;
import com.ironbank.money_transfer.repository.BankUserRepository;
import com.ironbank.money_transfer.service.NotificationService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.UUID;

@Controller
public class AuthController {

    private final BankUserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final NotificationService notificationService;

    public AuthController(BankUserRepository repository,
                          PasswordEncoder passwordEncoder,
                          NotificationService notificationService) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.notificationService = notificationService;
    }

    // Show the Login Page
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    // Show the Register Page
    @GetMapping("/register")
    public String register() {
        return "register";
    }

    // Handle the Registration Form
    @PostMapping("/register")
    public String registerUser(@RequestParam String username,
                               @RequestParam String password,
                               @RequestParam String email,
                               @RequestParam String phone) {

        System.out.println("1. Request received for: " + username); // Debug 1

        if (repository.findByUsername(username).isPresent()) {
            System.out.println("2. User already exists!"); // Debug 2
            return "redirect:/register?error";
        }
        // Check if user exists
        if (repository.findByUsername(username).isPresent()) {
            return "redirect:/register?error";
        }

        // Create new user with ENCRYPTED password
        BankUser newUser = new BankUser(
                username,
                passwordEncoder.encode(password),
                email,
                phone,         // Phone (Make sure this is in the right spot!)
                new BigDecimal("0.00"), // Balance
                "ACC-" + System.currentTimeMillis(), // Account Number
                "ROLE_USER"           // Role

        );

        repository.save(newUser);
        notificationService.sendWelcomeEmail(email, username);
        return "redirect:/login?success";

    }
}