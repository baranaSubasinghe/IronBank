package com.ironbank.money_transfer.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class Beneficiary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private BankUser user;

    private String name;
    private String accountNumber;
    private String email;

    public Beneficiary(BankUser user, String name, String accountNumber, String email) {
        this.user = user;
        this.name = name;
        this.accountNumber = accountNumber;
        this.email = email;
    }
}