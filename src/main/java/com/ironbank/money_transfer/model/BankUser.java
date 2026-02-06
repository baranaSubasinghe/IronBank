package com.ironbank.money_transfer.model;

import jakarta.persistence.*;
import jakarta.persistence.Column;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;

@Entity
@Data
public class BankUser implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String username;

    @Column(length = 1000) // <--- Add this! Forces the column to be huge.
    private String password;

    private String email;
    private String phoneNumber;

    private BigDecimal balance;
    private String accountNumber;

    private String role;
    private boolean active = true;

    // Empty Constructor
    public BankUser() {}

    // Constructor for Registration
    public BankUser(String username, String password, String email, String phoneNumber, BigDecimal balance, String accountNumber, String role) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.balance = balance;
        this.accountNumber = accountNumber;
        this.role = role;
        this.active = true;
    }

    //  Spring Security Required Methods

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority(role));
    }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() {
        //  If active is false, Spring Security blocks login!
        return this.active;
    }
}