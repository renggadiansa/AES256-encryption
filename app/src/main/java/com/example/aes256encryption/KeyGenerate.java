package com.example.aes256encryption;

import java.security.SecureRandom;
import java.util.Base64;

public class KeyGenerate {
    private static final int KEY_LENGTH = 32; // Panjang kunci dalam byte
    private static final int IV_LENGTH = 16; // Panjang IV dalam byte

    public static void main(String[] args) {
        String aesKey = generateAESKey();
        String aesIV = generateAESIV();
        System.out.println("AES Key: " + aesKey);
        System.out.println("AES IV: " + aesIV);
    }

    private static String generateAESKey() {
        byte[] key = new byte[KEY_LENGTH];
        SecureRandom random = new SecureRandom();
        random.nextBytes(key);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(key);
    }

    private static String generateAESIV() {
        byte[] iv = new byte[IV_LENGTH];
        SecureRandom random = new SecureRandom();
        random.nextBytes(iv);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(iv);
    }
}