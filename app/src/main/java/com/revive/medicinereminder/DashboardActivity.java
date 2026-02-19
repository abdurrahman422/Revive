package com.revive.medicinereminder;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class DashboardActivity extends AppCompatActivity {

    private ImageView imageViewLogo;
    private TextView textViewWelcome;
    private GridLayout gridLayout;
    private Button buttonLogout;
    private Button buttonSettings;

    private CardView cardAddMedicine, cardViewMedicines, cardPrescriptionScanner, cardStockTracker;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Initialize views
        imageViewLogo = findViewById(R.id.imageViewLogo);
        textViewWelcome = findViewById(R.id.textViewWelcome);
        gridLayout = findViewById(R.id.gridLayout);
        buttonLogout = findViewById(R.id.buttonLogout);
        buttonSettings = findViewById(R.id.buttonSettings);

        cardAddMedicine = findViewById(R.id.cardAddMedicine);
        cardViewMedicines = findViewById(R.id.cardViewMedicines);
        cardPrescriptionScanner = findViewById(R.id.cardPrescriptionScanner);
        cardStockTracker = findViewById(R.id.cardStockTracker);

        // Apply animations
        Animation topAnimation = AnimationUtils.loadAnimation(this, R.anim.top_animation);
        imageViewLogo.setAnimation(topAnimation);

        Animation fadeAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_animation);
        textViewWelcome.setAnimation(fadeAnimation);

        Animation bottomAnimation = AnimationUtils.loadAnimation(this, R.anim.bottom_animation);
        gridLayout.setAnimation(bottomAnimation);
        buttonLogout.setAnimation(bottomAnimation);
        buttonSettings.setAnimation(topAnimation);

        // Fetch username from Firestore
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String uid = currentUser.getUid();
            db.collection("users").document(uid).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String username = documentSnapshot.getString("username");
                            textViewWelcome.setText("Welcome to " + username);
                        } else {
                            textViewWelcome.setText("Welcome to Revive!");
                        }
                    })
                    .addOnFailureListener(e -> {
                        textViewWelcome.setText("Welcome to Revive!");
                        Log.e("Dashboard", "Failed to fetch username", e);
                    });
        }

        // Card click listeners
        cardAddMedicine.setOnClickListener(v -> {
            startActivity(new Intent(DashboardActivity.this, AddMedicineActivity.class));
        });

        cardViewMedicines.setOnClickListener(v -> {
            startActivity(new Intent(DashboardActivity.this, ViewMedicinesActivity.class));
        });

        cardPrescriptionScanner.setOnClickListener(v -> {
            startActivity(new Intent(DashboardActivity.this, ScannerActivity.class));
        });

        cardStockTracker.setOnClickListener(v -> {
            startActivity(new Intent(DashboardActivity.this, StockTrackerActivity.class));
        });

        // Logout button
        buttonLogout.setOnClickListener(v -> {
            mAuth.signOut();
            Intent intent = new Intent(DashboardActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });

        // Settings button
        buttonSettings.setOnClickListener(v -> {
            startActivity(new Intent(DashboardActivity.this, SettingsActivity.class));
        });
    }
}
