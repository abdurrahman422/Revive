package com.revive.medicinereminder;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.Toast;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.revive.medicinereminder.model.Medicine;

import java.util.Locale;

public class EditMedicineActivity extends AppCompatActivity {

    private TextInputEditText edtName, edtDosage, edtStock;
    private TextView txtPickedTime;
    private MaterialButton btnUpdate, btnPickTime;

    private int hour = -1, minute = -1;
    private String documentId;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_medicine);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        // Bind views
        edtName = findViewById(R.id.editTextMedicineName);
        edtDosage = findViewById(R.id.editTextDosage);
        edtStock = findViewById(R.id.editTextStock);
        txtPickedTime = findViewById(R.id.textViewPickedTime);
        btnUpdate = findViewById(R.id.buttonUpdateMedicine);
        btnPickTime = findViewById(R.id.buttonPickTime);

        // Receive data from intent
        documentId = getIntent().getStringExtra("medicine_id");
        edtName.setText(getIntent().getStringExtra("name"));
        edtDosage.setText(getIntent().getStringExtra("dosage"));
        edtStock.setText(String.valueOf(getIntent().getIntExtra("stock", 0)));

        hour = getIntent().getIntExtra("hour", 0);
        minute = getIntent().getIntExtra("minute", 0);
        txtPickedTime.setText(String.format(Locale.getDefault(), "%02d:%02d", hour, minute));

        // Pick time again
        btnPickTime.setOnClickListener(v -> {
            TimePickerDialog dialog = new TimePickerDialog(this, (view, hourOfDay, minute1) -> {
                hour = hourOfDay;
                minute = minute1;
                txtPickedTime.setText(String.format(Locale.getDefault(), "%02d:%02d", hour, minute));
            }, hour, minute, true);
            dialog.show();
        });

        // Update button
        btnUpdate.setOnClickListener(v -> updateMedicine());
    }

    private void updateMedicine() {
        String name = edtName.getText().toString().trim();
        String dosage = edtDosage.getText().toString().trim();
        String stockStr = edtStock.getText().toString().trim();

        if (name.isEmpty() || dosage.isEmpty() || stockStr.isEmpty() || hour == -1) {
            Toast.makeText(this, "Please fill all fields and pick time", Toast.LENGTH_SHORT).show();
            return;
        }

        int stock = Integer.parseInt(stockStr);
        String uid = mAuth.getCurrentUser().getUid();

        db.collection("users")
                .document(uid)
                .collection("medicines")
                .document(documentId)
                .update(
                        "name", name,
                        "dosage", dosage,
                        "hour", hour,
                        "minute", minute,
                        "stock", stock
                )
                .addOnSuccessListener(aVoid ->
                        Toast.makeText(this, "Medicine updated successfully!", Toast.LENGTH_SHORT).show()
                )
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed to update medicine", Toast.LENGTH_SHORT).show()
                );
    }
}
