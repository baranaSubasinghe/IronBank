package com.ironbank.money_transfer;

import com.ironbank.money_transfer.model.BankUser;
import com.ironbank.money_transfer.repository.BankUserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.math.BigDecimal;

@Configuration
public class DatabaseLoader {

    @Bean
    CommandLineRunner initDatabase(BankUserRepository repository) {
        return args -> {
            // Check if data already exists so we don't duplicate it
            if (repository.count() == 0) {
                // Create Alice with $1000
                BankUser alice = new BankUser("Alice", new BigDecimal("1000.00"), "ACC-111");

                // Create Bob with $500
                BankUser bob = new BankUser("Bob", new BigDecimal("500.00"), "ACC-222");

                repository.save(alice);
                repository.save(bob);

                System.out.println("✅ Sample Users Created: Alice ($1000) and Bob ($500)");
            }
        };
    }
}