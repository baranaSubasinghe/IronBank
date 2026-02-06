package com.ironbank.money_transfer.service;

import com.ironbank.money_transfer.model.BankUser;
import com.ironbank.money_transfer.model.Transaction;
import com.ironbank.money_transfer.repository.BankUserRepository;
import com.ironbank.money_transfer.repository.TransactionRepository;
import jakarta.transaction.Transactional; // Important for data safety
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class TransferService {

    private final BankUserRepository bankUserRepository;
    private final TransactionRepository transactionRepository;

    public TransferService(BankUserRepository bankUserRepository, TransactionRepository transactionRepository) {
        this.bankUserRepository = bankUserRepository;
        this.transactionRepository = transactionRepository;
    }

    public void deposit(Long userId, BigDecimal amount) {
        BankUser user = bankUserRepository.findById(userId).orElseThrow();
        user.setBalance(user.getBalance().add(amount));
        bankUserRepository.save(user);
    }

    @Transactional
    public void transferMoney(Long senderId, String receiverAccountNumber, BigDecimal amount) {
        // 1. Find Sender
        BankUser sender = bankUserRepository.findById(senderId)
                .orElseThrow(() -> new RuntimeException("Sender not found"));

        // 2. Find Receiver by Account Number
        BankUser receiver = bankUserRepository.findByAccountNumber(receiverAccountNumber)
                .orElseThrow(() -> new RuntimeException("Invalid Account Number"));

        // 3. Check Balance
        if (sender.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient funds");
        }

        // 4. Move Money
        sender.setBalance(sender.getBalance().subtract(amount));
        receiver.setBalance(receiver.getBalance().add(amount));

        // 5. Save Users
        bankUserRepository.save(sender);
        bankUserRepository.save(receiver);

        // 6. Record Transaction
        // Used .getUsername() to turn the User Object into a String
        Transaction transaction = new Transaction(
                sender.getUsername(),
                receiver.getUsername(),
                amount
        );

        transactionRepository.save(transaction);
    }
}