package com.ironbank.money_transfer.service;

import org.springframework.stereotype.Service;
import java.util.Random;

@Service
public class OtpService {

    public String generateOtp() {
        Random random = new Random();
        int code = 1000 + random.nextInt(9000); // Generates random 1000-9999
        return String.valueOf(code);
    }
}