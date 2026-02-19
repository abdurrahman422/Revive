package com.revive.medicinereminder;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class NotificationActionReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if ("ACTION_TAKEN".equals(intent.getAction())) {
            String medicineId = intent.getStringExtra("medicine_id");
            int currentStock = intent.getIntExtra("stock", 0);
            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

            if (currentStock > 0) {
                int newStock = currentStock - 1;
                FirebaseFirestore.getInstance()
                        .collection("users")
                        .document(uid)
                        .collection("medicines")
                        .document(medicineId)
                        .update("stock", newStock)
                        .addOnSuccessListener(aVoid -> Toast.makeText(context, "Medicine Taken. Stock updated.", Toast.LENGTH_SHORT).show())
                        .addOnFailureListener(e -> Toast.makeText(context, "Failed to update stock.", Toast.LENGTH_SHORT).show());
            } else {
                Toast.makeText(context, "No stock left!", Toast.LENGTH_SHORT).show();
            }

        } else if ("ACTION_DISMISS".equals(intent.getAction())) {
            Toast.makeText(context, "Reminder dismissed.", Toast.LENGTH_SHORT).show();
        }
    }
}
