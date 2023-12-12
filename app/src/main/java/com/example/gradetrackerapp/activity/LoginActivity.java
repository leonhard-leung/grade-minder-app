package com.example.gradetrackerapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gradetrackerapp.R;
import com.example.gradetrackerapp.authentication.BiometricHelper;

import java.io.File;

public class LoginActivity extends AppCompatActivity implements BiometricHelper.BiometricCallback {
    private boolean proceedToLogin = true;
    private int invalidAttemptCount = 0;
    private static final int maxInvalidAttempts = 5;
    private BiometricHelper biometricHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        biometricHelper = new BiometricHelper(this, this);

        Button loginButton = findViewById(R.id.loginButton);
        loginButton.setOnClickListener(v -> {
            if (proceedToLogin) {
                biometricHelper.initiateBiometricAuthentication();
            } else {
                Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(registerIntent);
            }
        });

        Button removeButton = findViewById(R.id.removeButton);
        removeButton.setOnClickListener(v -> {
            removeDataFile();
            proceedToLogin = false;
        });
    } // end of onCreate

    @Override
    public void onBiometricAuthenticationSucceeded() {
        showToast("Biometric authentication succeeded.");
        proceed();
    } // end of onBiometricAuthenticationSucceeded

    @Override
    public void onBiometricAuthenticationFailed(String message) {
        showToast(message);
        if (invalidAttemptCount >= maxInvalidAttempts) {
            showToast("Exceeded maximum invalid attempts. Access restricted.");
            invalidAttemptCount = 0;
        } else {
            invalidAttemptCount++;
        }
    } // end of onBiometricAuthenticationFailed

    private void proceed() {
        Intent proceedIntent = new Intent(LoginActivity.this, HomepageActivity.class);
        startActivity(proceedIntent);
    } // end of proceed

    private void removeDataFile() {
        File file = new File(getFilesDir(), "register_data/data.txt");

        if (file.delete()) {
            showToast("Data file removed successfully");
        } else {
            showToast("Failed to remove data file");
        }

        File dbFile = getDatabasePath("course-database");
        if (dbFile.delete()) {
            showToast("Database file removed successfully");
        } else {
            showToast("Failed to remove database file");
        }
    } // end of removeDateFile

    private void showToast(String message) {
        runOnUiThread(() -> Toast.makeText(this, message, Toast.LENGTH_SHORT).show());
    } // end of showToast
} // end of LoginActivity