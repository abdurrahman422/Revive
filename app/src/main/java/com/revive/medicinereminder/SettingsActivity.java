package com.revive.medicinereminder;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    private Switch switchDarkMode;
    private Button buttonLogout, buttonHelp, buttonAppInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        switchDarkMode = findViewById(R.id.switchDarkMode);
        buttonLogout = findViewById(R.id.buttonLogout);
        buttonHelp = findViewById(R.id.buttonHelp);
        buttonAppInfo = findViewById(R.id.buttonAppInfo);

        // Dark Mode switch
        switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                Toast.makeText(this, "Dark Mode On (Implement theme)", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Dark Mode Off", Toast.LENGTH_SHORT).show();
            }
        });

        // Logout
        buttonLogout.setOnClickListener(v -> {
            Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });

        // Help & Support
        buttonHelp.setOnClickListener(v -> {
            Toast.makeText(this, "Contact us at: support@revive.com", Toast.LENGTH_LONG).show();
        });

        // App Info
        buttonAppInfo.setOnClickListener(v -> {
            Toast.makeText(this, "Revive App v1.0\nDeveloped by Your Team", Toast.LENGTH_LONG).show();
        });
    }
}
