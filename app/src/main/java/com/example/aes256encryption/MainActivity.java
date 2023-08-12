package com.example.aes256encryption;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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

    private ImageView imageView;

    private ImageView imageView2;

    private static final String AES_KEY = "YOU_AES_KEY"; //32 digits
    private static final String AES_IV = "YOU_AES_IV"; //16 digits

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
        imageView = findViewById(R.id.imageViewCopy);
        imageView2 = findViewById(R.id.imageViewCopy2);
        buttonEncrypt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inputText = editTextInput.getText().toString();
                editTextInput.setText("");
                if (inputText.isEmpty()) {
                    editTextInput.setError("Input cannot be empty");
                } else {
                    try {
                        String encryptedText = encrypt(inputText, AES_KEY, AES_IV);
                        textViewEncrypt.setText(encryptedText);
                        //ClipboardManager cm = (ClipboardManager)getApplicationContext().getSystemService(Context.CLIPBOARD_SERVICE);
                        //cm.setText(textViewEncrypt.getText().toString());
                        //Toast.makeText(getApplicationContext(), "Copied :)", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void onClick(View v) {
                String encryptedText = textViewEncrypt.getText().toString();

                if (encryptedText.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Nothing to Copy", Toast.LENGTH_SHORT).show();
                } else {
                    ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clipData = ClipData.newPlainText("text", textViewEncrypt.getText().toString());
                    clipboardManager.setPrimaryClip(clipData);

                    imageView.setImageDrawable(getResources().getDrawable(R.drawable.checklist));
                    Toast.makeText(getApplicationContext(), "Text Copied to Clipboard", Toast.LENGTH_SHORT).show();
                    textViewEncrypt.setText("");

                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            imageView.setImageDrawable(getResources().getDrawable(R.drawable.clipboard));
                        }
                    }, 500);
                }
            }

        });

        buttonDecrypt.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                String encryptedText = editTextInput.getText().toString();
                String inputText = editTextInput.getText().toString();
                editTextInput.setText("");
                if (inputText.isEmpty()) {
                    editTextInput.setError("Input cannot be empty");
                } else {
                    try {
                        String decryptedText = decrypt(encryptedText, AES_KEY, AES_IV);
                        textViewDecrypt.setText(decryptedText);
                        //ClipboardManager cm = (ClipboardManager)getApplicationContext().getSystemService(Context.CLIPBOARD_SERVICE);
                        //cm.setText(textViewDecrypt.getText().toString());
                        //Toast.makeText(getApplicationContext(), "Copied :)", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), "Cannot decrypt the input", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        imageView2.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void onClick(View v) {
                ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText("text", textViewDecrypt.getText().toString());
                clipboardManager.setPrimaryClip(clipData);

                imageView2.setImageDrawable(getResources().getDrawable(R.drawable.checklist));
                Toast.makeText(getApplicationContext(), "Text Copied to Clipboard", Toast.LENGTH_SHORT).show();
                textViewDecrypt.setText("");

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        imageView2.setImageDrawable(getResources().getDrawable(R.drawable.clipboard));
                    }
                }, 500);
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
