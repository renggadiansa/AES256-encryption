package com.example.aes256encryption;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class MainActivity extends AppCompatActivity {
    private EditText editTextInput;
    private Button buttonEncrypt;
    private Button buttonDecrypt;
    private TextView textViewResult;

    private static final String AES_KEY = "YOUR_ENCRYPTION_KEY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Menghubungkan elemen UI dengan variabel
        editTextInput = findViewById(R.id.editTextInput);
        buttonEncrypt = findViewById(R.id.buttonEncrypt);
        buttonDecrypt = findViewById(R.id.buttonDecrypt);
        textViewResult = findViewById(R.id.textViewResult);

        buttonEncrypt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inputText = editTextInput.getText().toString();

                try {
                    String encryptedText = encrypt(inputText, AES_KEY);
                    textViewResult.setText("Encrypted Text: " + encryptedText);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        buttonDecrypt.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                String encryptedText = editTextInput.getText().toString();

                try {
                    String decryptedText = decrypt(encryptedText, AES_KEY);
                    textViewResult.setText("Decrypted Text: " + decryptedText);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private String encrypt(String inputText, String encryptionKey) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        SecretKeySpec keySpec = new SecretKeySpec(encryptionKey.getBytes("UTF-8"), "AES");
        cipher.init(Cipher.ENCRYPT_MODE, keySpec);
        byte[] encryptedBytes = cipher.doFinal(inputText.getBytes());
        return Base64.encodeToString(encryptedBytes, Base64.DEFAULT);
    }

    private String decrypt(String encryptedText, String encryptionKey) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        SecretKeySpec keySpec = new SecretKeySpec(encryptionKey.getBytes("UTF-8"), "AES");
        cipher.init(Cipher.DECRYPT_MODE, keySpec);
        byte[] encryptedBytes = Base64.decode(encryptedText, Base64.DEFAULT);
        byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
        return new String(decryptedBytes);
    }
}
