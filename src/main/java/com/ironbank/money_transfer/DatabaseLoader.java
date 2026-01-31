//package com.ironbank.money_transfer;
//
//import com.ironbank.money_transfer.model.BankUser;
//import com.ironbank.money_transfer.repository.BankUserRepository;
//import com.ironbank.money_transfer.service.TransferService; // Import Service
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import java.math.BigDecimal;
//
//@Configuration
//public class DatabaseLoader {
//
//    @Bean
//    CommandLineRunner initDatabase(BankUserRepository repository, TransferService transferService) {
//        return args -> {
//            if (repository.count() == 0) {
//                // 1. Create Users
//                BankUser alice = new BankUser("Alice", new BigDecimal("1000.00"), "ACC-111");
//                BankUser bob = new BankUser("Bob", new BigDecimal("500.00"), "ACC-222");
//
//                repository.save(alice);
//                repository.save(bob);
//
//                System.out.println("✅ Users Created.");
//
//                // 2. TEST THE TRANSFER LOGIC
//                System.out.println("💸 Attempting Transfer: $100 from Alice to Bob...");
//
//                // Alice (ID 1) sends to Bob (ID 2)
//                transferService.transferMoney(alice.getId(), bob.getId(), new BigDecimal("100.00"));
//            }
//        };
//    }
//}