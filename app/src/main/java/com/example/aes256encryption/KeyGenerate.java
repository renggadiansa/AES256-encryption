package com.example.aes256encryption;

import java.security.SecureRandom;

public class KeyGenerate {
    public static void main(String[] args) {
        int stringLength1 = 16;
        int stringLength2 = 32;

        String randomString = generateRandomString(stringLength1);
        String randomString2 = generateRandomString(stringLength2);

        System.out.println("AES Key: " + randomString2);
        System.out.println("AES IV: " + randomString);
    }

    private static String generateRandomString(int stringLength) {
        if (stringLength < 1) {
            throw new IllegalArgumentException("String length should be at least 1.");
        }

        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()_-+=[]{}|;:,.<>?";
        StringBuilder stringBuilder = new StringBuilder(stringLength);
        SecureRandom secureRandom = new SecureRandom();

        for (int i = 0; i < stringLength; i++) {
            int randomIndex = secureRandom.nextInt(characters.length());
            char randomChar = characters.charAt(randomIndex);
            stringBuilder.append(randomChar);
        }

        return stringBuilder.toString();
    }
}