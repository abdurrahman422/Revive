package com.revive.medicinereminder;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.*;
import com.revive.medicinereminder.adapter.StockMedicineAdapter;
import com.revive.medicinereminder.model.Medicine;

import java.util.ArrayList;
import java.util.List;

public class StockTrackerActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private List<Medicine> medicineList;
    private StockMedicineAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_tracker);

        recyclerView = findViewById(R.id.recyclerViewStock);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        medicineList = new ArrayList<>();
        adapter = new StockMedicineAdapter(this, medicineList);
        recyclerView.setAdapter(adapter);

        loadMedicinesFromFirestore();
    }

    private void loadMedicinesFromFirestore() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore.getInstance()
                .collection("users")
                .document(uid)
                .collection("medicines")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    medicineList.clear();
                    for (DocumentSnapshot snapshot : queryDocumentSnapshots) {
                        Medicine medicine = snapshot.toObject(Medicine.class);
                        medicine.setId(snapshot.getId());
                        medicineList.add(medicine);
                    }
                    adapter.notifyDataSetChanged();
                });
    }
}
