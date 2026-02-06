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
                             @RequestParam String phone, // Ensure HTML input name="phone" matches this!
                             HttpServletResponse response) throws IOException {

        System.out.println("DEBUG: Request received for " + username);

        // 1. Check if user exists
        if (repository.findByUsername(username).isPresent()) {
            response.sendRedirect("/register?error");
            return;
        }

        // 2. Create User
        BankUser newUser = new BankUser(
                username,
                passwordEncoder.encode(password),
                email,
                phone,
                new BigDecimal("0.00"),
                "ACC-" + System.currentTimeMillis(),
                "ROLE_USER"
        );

        // 3. Save User (This commits the data)
        repository.save(newUser);
        System.out.println("DEBUG: User saved to database!");

        // 4. THE FIX: Try to send email, but ignore errors!
        try {
            notificationService.sendWelcomeEmail(email, username);
        } catch (Exception e) {
            System.out.println("⚠️ Email failed (Ignored): " + e.getMessage());
        }

        // 5. Redirect
        response.sendRedirect("/login?success");
    }

}