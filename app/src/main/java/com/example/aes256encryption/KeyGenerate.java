package com.example.aes256encryption;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class KeyGenerate {
    private static final int KEY_LENGTH = 32;
    private static final int IV_LENGTH = 16;

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
        return Base64.getEncoder().encodeToString(key);
    }

    private static String generateAESIV() {
        byte[] iv = new byte[IV_LENGTH];
        SecureRandom random = new SecureRandom();
        random.nextBytes(iv);
        return Base64.getEncoder().encodeToString(iv);
    }
}