package com.ironbank.money_transfer.model;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String senderName;
    private String receiverName;
    private BigDecimal amount;
    private LocalDateTime timestamp;

    public Transaction() {}

    public Transaction(String senderName, String receiverName, BigDecimal amount) {
        this.senderName = senderName;
        this.receiverName = receiverName;
        this.amount = amount;
        this.timestamp = LocalDateTime.now(); // Auto-set time to "right now"
    }
}