package com.ironbank.money_transfer.controller;

import com.ironbank.money_transfer.model.BankUser;
import com.ironbank.money_transfer.repository.BankUserRepository;
import com.ironbank.money_transfer.service.NotificationService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;

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

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String register() {
        return "register";
    }

    @PostMapping("/register")
    public void registerUser(@RequestParam String username,
                             @RequestParam String password,
                             @RequestParam String email,
                             @RequestParam String phone,
                             HttpServletResponse response) throws IOException { // <--- Added response object

        System.out.println("1. Request received for: " + username);

        // 1. Check if user exists (Cleaned up duplicate code)
        if (repository.findByUsername(username).isPresent()) {
            System.out.println("2. User already exists!");
            response.sendRedirect("/register?error"); // Force relative redirect
            return;
        }

        // 2. Create new user
        BankUser newUser = new BankUser(
                username,
                passwordEncoder.encode(password),
                email,
                phone,
                new BigDecimal("0.00"),
                "ACC-" + System.currentTimeMillis(),
                "ROLE_USER"
        );

        // 3. Save & Notify
        repository.save(newUser);
        notificationService.sendWelcomeEmail(email, username);

        // 4. Force browser to handle the redirect path relative to current URL
        // This prevents the HTTP vs HTTPS conflict on Railway
        response.sendRedirect("/login?success");
    }
}