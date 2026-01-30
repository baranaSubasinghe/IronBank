package com.ironbank.money_transfer.model;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

@Entity // 1. Tells Hibernate: "Make a table called 'bank_user' in MySQL"
@Table(name = "bank_users")
@Data // 2. Lombok: Auto-generates Getters, Setters, and toString
public class BankUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-increment ID (1, 2, 3...)
    private Long id;

    @Column(unique = true, nullable = false) // Username must be unique
    private String username;

    @Column(nullable = false)
    private BigDecimal balance; // ⚠️ ALWAYS use BigDecimal for money!

    private String accountNumber;

    // Constructor for easy testing
    public BankUser(String username, BigDecimal balance, String accountNumber) {
        this.username = username;
        this.balance = balance;
        this.accountNumber = accountNumber;
    }

    // Default constructor needed for JPA
    public BankUser() {}
}