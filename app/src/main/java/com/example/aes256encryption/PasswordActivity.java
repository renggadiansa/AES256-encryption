package com.example.aes256encryption;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.hanks.passcodeview.PasscodeView;

public class PasswordActivity extends AppCompatActivity {
    PasscodeView passcodeView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);

        passcodeView = findViewById(R.id.passcodeView);

        passcodeView.setPasscodeLength(5)
                .setLocalPasscode("12369")

                .setListener(new PasscodeView.PasscodeViewListener() {
                    @Override
                    public void onFail() {
                        Toast.makeText(PasswordActivity.this, "Password is wrong!", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSuccess(String number) {
                        Intent intent_passcode = new Intent(PasswordActivity.this, MainActivity.class);
                        startActivity(intent_passcode);
                    }
                });
    }
}