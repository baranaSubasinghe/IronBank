package com.ironbank.money_transfer.dto;

import lombok.Data;
import java.math.BigDecimal;

// DTO = Data Transfer Object. Just a box for data.
@Data
public class TransferRequest {
    private Long senderId;
    //private Long receiverId;
    private String receiverAccountNumber;
    private BigDecimal amount;
    private String secretOtp; // We store the correct OTP here to check later
}