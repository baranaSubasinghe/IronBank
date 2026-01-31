package com.ironbank.money_transfer.repository;

import com.ironbank.money_transfer.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    // Find history where I am the Sender OR the Receiver
    List<Transaction> findBySenderNameOrReceiverName(String senderName, String receiverName);


}
