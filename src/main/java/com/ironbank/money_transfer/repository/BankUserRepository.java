package com.ironbank.money_transfer.repository;

import com.ironbank.money_transfer.model.BankUser;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

//JpaRepository<Entity Type, ID Type>
public interface BankUserRepository extends JpaRepository<BankUser, Long> {

    // Custom query method: Spring Boot writes the SQL for us automatically!
    // SQL generated: SELECT * FROM bank_users WHERE account_number = ?
    Optional<BankUser> findByAccountNumber(String accountNumber);
    Optional<BankUser> findByUsername(String username);

}