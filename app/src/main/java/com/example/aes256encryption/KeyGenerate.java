package com.example.aes256encryption;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class KeyGenerate {
    private static final int ITERATIONS = 65536;
    private static final int KEY_LENGTH =256;

    public static String generateKey(String password) throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] salt = new byte[16];
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(salt);

        SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        PBEKeySpec pbeKeySpec = new PBEKeySpec(password.toCharArray(), salt, ITERATIONS, KEY_LENGTH);
        SecretKey secretKey = secretKeyFactory.generateSecret(pbeKeySpec);
        byte[] keyBytes = secretKey.getEncoded();

        return bytesToHex(keyBytes);
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte b : bytes) {
            result.append(String.format("%02x", b));
        }
        return result.toString();
    }

    public static void main(String[] args) {
        try {
            String password = "1234";
            String encryptionKey = generateKey(password);
            System.out.println("Encryption Key: " + encryptionKey);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
        }
    }
}