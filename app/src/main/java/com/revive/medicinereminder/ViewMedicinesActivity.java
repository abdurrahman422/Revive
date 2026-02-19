package com.revive.medicinereminder;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.revive.medicinereminder.adapter.MedicineAdapter;
import com.revive.medicinereminder.model.Medicine;

import java.util.ArrayList;
import java.util.List;

public class ViewMedicinesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private MedicineAdapter medicineAdapter;
    private List<Medicine> medicineList;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_medicines);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Setup RecyclerView
        recyclerView = findViewById(R.id.recyclerViewMedicines);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        medicineList = new ArrayList<>();
        medicineAdapter = new MedicineAdapter(this, medicineList);
        recyclerView.setAdapter(medicineAdapter);

        // Load data from Firestore
        loadMedicines();
    }

    private void loadMedicines() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("users")
                .document(user.getUid())
                .collection("medicines")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    medicineList.clear(); // Clear old list
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Medicine medicine = doc.toObject(Medicine.class);
                        medicine.setId(doc.getId());  // Set the document ID
                        medicineList.add(medicine);
                    }
                    medicineAdapter.notifyDataSetChanged(); // Refresh adapter
                })
                .addOnFailureListener(e -> {
                    Log.e("FirestoreError", "Error loading medicines", e);
                    Toast.makeText(this, "Failed to load medicines", Toast.LENGTH_SHORT).show();
                });
    }
}
