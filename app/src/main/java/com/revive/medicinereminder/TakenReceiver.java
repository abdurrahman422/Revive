package com.revive.medicinereminder;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class TakenReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String medicineName = intent.getStringExtra("medicine_name");

        if (medicineName == null || medicineName.isEmpty()) {
            Toast.makeText(context, "Medicine name missing!", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("medicines")
                .whereEqualTo("name", medicineName)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                            Long currentStock = doc.getLong("stock");
                            if (currentStock != null && currentStock > 0) {
                                doc.getReference().update("stock", currentStock - 1);
                                Toast.makeText(context, medicineName + " taken! Dose reduced by 1.", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(context, "No stock left for " + medicineName, Toast.LENGTH_SHORT).show();
                            }
                        }
                    } else {
                        Toast.makeText(context, "Medicine not found in database.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Failed to update stock: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
