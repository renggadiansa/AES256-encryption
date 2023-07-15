package com.example.aes256encryption;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class InputFileActivity extends AppCompatActivity {

    private static final int READ_REQUEST_CODE = 42;
    private static final int STORAGE_PERMISSION_CODE = 1;

    private Button btnSelectFile;
    private TextView tvSelectedFile;

    private String encryptionKey = "your_encryption_key_here";
    private String initializationVector = "your_initialization_vector_here";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_file);

        btnSelectFile = findViewById(R.id.btnSelectFile);
        tvSelectedFile = findViewById(R.id.tvSelectedFile);

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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                performFileSearch();
            } else {
                Toast.makeText(this, "Izin akses penyimpanan ditolak", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                Uri uri = data.getData();
                if (uri != null) {
                    String fileName = uri.getLastPathSegment();
                    tvSelectedFile.setText("Selected File: " + fileName);

                    try {
                        encryptAndSaveFile(uri);
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Gagal menyimpan file terenkripsi", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }

    private void encryptAndSaveFile(Uri uri) throws IOException {
        InputStream inputStream = getContentResolver().openInputStream(uri);
        if (inputStream == null) {
            Toast.makeText(this, "Gagal membuka file", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(new Date());
            String fileName = "Encrypted_" + timeStamp + ".enc";

            File directory = new File(getFilesDir(), "EncryptedFiles");
            if (!directory.exists()) {
                directory.mkdirs();
            }

            File encryptedFile = new File(directory, fileName);
            FileOutputStream outputStream = new FileOutputStream(encryptedFile);

            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                byte[] encryptedBytes = encrypt(buffer);
                outputStream.write(encryptedBytes, 0, encryptedBytes.length);
            }

            outputStream.close();
            inputStream.close();
            Toast.makeText(this, "File berhasil dienkripsi dan disimpan di penyimpanan internal", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Gagal menyimpan file terenkripsi", Toast.LENGTH_SHORT).show();
        }
    }

    private byte[] encrypt(byte[] input) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        SecretKeySpec keySpec = new SecretKeySpec(encryptionKey.getBytes(StandardCharsets.UTF_8), "AES");
        IvParameterSpec ivSpec = new IvParameterSpec(initializationVector.getBytes(StandardCharsets.UTF_8));
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
        return cipher.doFinal(input);
    }

}
