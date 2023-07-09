package com.example.aes256encryption;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.nio.charset.StandardCharsets;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class MainActivity extends AppCompatActivity {
    private EditText editTextInput;
    private Button buttonEncrypt;
    private Button buttonDecrypt;
    private TextView textViewEncrypt;
    private TextView textViewDecrypt;

    private static final String AES_KEY = "YOU_AES_KEY";
    private static final String AES_IV = "YOU_AES_IV";

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        editTextInput = findViewById(R.id.editTextInput);
        buttonEncrypt = findViewById(R.id.buttonEncrypt);
        buttonDecrypt = findViewById(R.id.buttonDecrypt);
        textViewEncrypt = findViewById(R.id.textViewEncrypt);
        textViewDecrypt = findViewById(R.id.textViewDescrypt);

        buttonEncrypt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inputText = editTextInput.getText().toString();

                try {
                    String encryptedText = encrypt(inputText, AES_KEY, AES_IV);
                    textViewEncrypt.setText(encryptedText);
                    ClipboardManager cm = (ClipboardManager)getApplicationContext().getSystemService(Context.CLIPBOARD_SERVICE);
                    cm.setText(textViewEncrypt.getText().toString());
                    Toast.makeText(getApplicationContext(), "Copied :)", Toast.LENGTH_SHORT).show();
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
                    String decryptedText = decrypt(encryptedText, AES_KEY, AES_IV);
                    textViewDecrypt.setText(decryptedText);
                    ClipboardManager cm = (ClipboardManager)getApplicationContext().getSystemService(Context.CLIPBOARD_SERVICE);
                    cm.setText(textViewDecrypt.getText().toString());
                    Toast.makeText(getApplicationContext(), "Copied :)", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private String encrypt(String inputText, String encryptionKey, String initializationVector) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
        SecretKeySpec keySpec = new SecretKeySpec(encryptionKey.getBytes(StandardCharsets.UTF_8), "AES");
        IvParameterSpec ivSpec = new IvParameterSpec(initializationVector.getBytes(StandardCharsets.UTF_8));
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
        byte[] encryptedBytes = cipher.doFinal(inputText.getBytes(StandardCharsets.UTF_8));
        return Base64.encodeToString(encryptedBytes, Base64.DEFAULT);
    }

    private String decrypt(String encryptedText, String encryptionKey, String initializationVector) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
        SecretKeySpec keySpec = new SecretKeySpec(encryptionKey.getBytes(StandardCharsets.UTF_8), "AES");
        IvParameterSpec ivSpec = new IvParameterSpec(initializationVector.getBytes(StandardCharsets.UTF_8));
        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
        byte[] encryptedBytes = Base64.decode(encryptedText, Base64.DEFAULT);
        byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }
}
