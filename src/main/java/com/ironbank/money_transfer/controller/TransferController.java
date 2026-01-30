package com.ironbank.money_transfer.controller;

import com.ironbank.money_transfer.service.TransferService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
public class TransferController {

    private final TransferService transferService;

    public TransferController(TransferService transferService) {
        this.transferService = transferService;
    }

    // Endpoint: POST /transfer
    // Usage: /transfer?senderId=1&receiverId=2&amount=500
    @PostMapping("/transfer")
    public String transferMoney(
            @RequestParam Long senderId,
            @RequestParam Long receiverId,
            @RequestParam BigDecimal amount
    ) {

        transferService.transferMoney(senderId, receiverId, amount);

        return "✅ Transfer Successful! LKR " + amount + " sent.";
    }
}