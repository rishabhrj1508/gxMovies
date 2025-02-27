package com.endava.example.utils;

import java.util.Random;

public class OtpGenerator {
    public static String generateOTP() {
        Random random = new Random();
        return String.valueOf(100000 + random.nextInt(900000)); // 6-digit OTP
    }
}
