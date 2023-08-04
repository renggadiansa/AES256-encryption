package com.example.aes256encryption;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.aes256encryption.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class InputFileActivity extends AppCompatActivity {

    private static final int READ_REQUEST_CODE = 42;
    private static final int STORAGE_PERMISSION_CODE = 1;

    private Button btnSelectFile;
    private Button btnEncrypt;
    private Button btnDecrypt;

    private String encryptionKey = "YOU_ENC_KEY";
    private String salt = "YOU_SALT";

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_file);

        btnSelectFile = findViewById(R.id.btnSelectFile);
        btnEncrypt = findViewById(R.id.btnEncrypt);
        btnDecrypt = findViewById(R.id.btnDecrypt);

        btnSelectFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkStoragePermission()) {
                    performFileSearch();
                } else {
                    requestStoragePermission();
                }
            }
        });

        btnEncrypt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                encryptFile(selectedFileUri);
            }
        });

        btnDecrypt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                decryptFile(selectedFileUri);
            }
        });
    }

    private boolean checkStoragePermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
    }

    private void performFileSearch() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        startActivityForResult(intent, READ_REQUEST_CODE);
    }

    private Uri selectedFileUri;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                performFileSearch();
            } else {
                Toast.makeText(this, "Storage permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                selectedFileUri = data.getData();
                if (selectedFileUri != null) {
                    String fileName = selectedFileUri.getLastPathSegment();
                    Toast.makeText(this, "Selected File: " + fileName, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void encryptFile(Uri uri) {

        File inputFile = new File(uri.getPath());
        File encryptedFile = new File(Environment.getExternalStorageDirectory(), "Encrypted_" + inputFile.getName());

        try {
            FileInputStream inputStream = new FileInputStream(inputFile);
            FileOutputStream outputStream = new FileOutputStream(encryptedFile);

            SecretKeySpec secretKey = generateSecretKey(encryptionKey, salt);
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            CipherOutputStream cipherOutputStream = new CipherOutputStream(outputStream, cipher);

            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                cipherOutputStream.write(buffer, 0, bytesRead);
            }

            cipherOutputStream.flush();
            cipherOutputStream.close();
            inputStream.close();
            outputStream.close();

            Toast.makeText(this, "File successfully encrypted and saved.", Toast.LENGTH_SHORT).show();
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to encrypt file.", Toast.LENGTH_SHORT).show();
        }
    }

    private void decryptFile(Uri uri) {

        File inputFile = new File(uri.getPath());
        File decryptedFile = new File(Environment.getExternalStorageDirectory(), "Decrypted_" + inputFile.getName());

        try {
            FileInputStream inputStream = new FileInputStream(inputFile);
            FileOutputStream outputStream = new FileOutputStream(decryptedFile);

            SecretKeySpec secretKey = generateSecretKey(encryptionKey, salt);
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(new byte[cipher.getBlockSize()]));
            CipherInputStream cipherInputStream = new CipherInputStream(inputStream, cipher);

            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = cipherInputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            outputStream.flush();
            outputStream.close();
            cipherInputStream.close();
            inputStream.close();

            Toast.makeText(this, "File successfully decrypted and saved.", Toast.LENGTH_SHORT).show();
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to decrypt file.", Toast.LENGTH_SHORT).show();
        }
    }

    private SecretKeySpec generateSecretKey(String password, String salt) throws GeneralSecurityException {
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt.getBytes(), 65536, 256);
        SecretKey secretKey = factory.generateSecret(spec);
        return new SecretKeySpec(secretKey.getEncoded(), "AES");
    }
}
