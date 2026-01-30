package com.ironbank.money_transfer.service;

import com.ironbank.money_transfer.model.BankUser;
import com.ironbank.money_transfer.model.Transaction; // Import this!
import com.ironbank.money_transfer.repository.BankUserRepository;
import com.ironbank.money_transfer.repository.TransactionRepository; // Import this!
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;

@Service
public class TransferService {

    private final BankUserRepository bankUserRepository;
    private final TransactionRepository transactionRepository; // New dependency

    // Update Constructor to include the new Repository
    public TransferService(BankUserRepository bankUserRepository, TransactionRepository transactionRepository) {
        this.bankUserRepository = bankUserRepository;
        this.transactionRepository = transactionRepository;
    }

    @Transactional
    public void transferMoney(Long senderId, Long receiverId, BigDecimal amount) {
        // ... (Your existing validation logic stays here) ...
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Amount cannot be negative!");
        }

        BankUser sender = bankUserRepository.findById(senderId)
                .orElseThrow(() -> new RuntimeException("Sender not found"));
        BankUser receiver = bankUserRepository.findById(receiverId)
                .orElseThrow(() -> new RuntimeException("Receiver not found"));

        BigDecimal senderBalance = sender.getBalance();
        if (senderBalance.compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient funds!");
        }

        // 1. Perform the Transfer
        sender.setBalance(senderBalance.subtract(amount));
        receiver.setBalance(receiver.getBalance().add(amount));

        bankUserRepository.save(sender);
        bankUserRepository.save(receiver);

        // 2. 🕵️‍♂️ SAVE THE HISTORY (The New Part)
        Transaction record = new Transaction(
                sender.getUsername(),
                receiver.getUsername(),
                amount
        );
        transactionRepository.save(record); // Saved to DB!
    }
}