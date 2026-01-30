package com.ironbank.money_transfer.repository;

import com.ironbank.money_transfer.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    // We get findAll() for free!
}