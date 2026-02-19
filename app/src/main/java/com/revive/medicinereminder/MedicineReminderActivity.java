package com.revive.medicinereminder;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.revive.medicinereminder.model.Medicine;

import java.util.Calendar;
import java.util.Locale;

public class MedicineReminderActivity extends AppCompatActivity {

    private TextInputEditText edtName, edtDosage, edtStock;
    private TextView txtPickedTime;
    private MaterialButton btnSave, btnPickTime;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private int selectedHour = -1, selectedMinute = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicine_reminder);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        edtName = findViewById(R.id.editTextMedicineName);
        edtDosage = findViewById(R.id.editTextDosage);
        edtStock = findViewById(R.id.editTextStock);
        btnPickTime = findViewById(R.id.buttonPickTime);
        txtPickedTime = findViewById(R.id.textViewPickedTime);
        btnSave = findViewById(R.id.buttonSaveMedicine);

        btnPickTime.setOnClickListener(v -> showTimePicker());
        btnSave.setOnClickListener(v -> saveMedicineData());
    }

    private void showTimePicker() {
        final Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                (view, hourOfDay, minute1) -> {
                    selectedHour = hourOfDay;
                    selectedMinute = minute1;
                    String timeFormatted = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute1);
                    txtPickedTime.setText(timeFormatted);
                },
                hour, minute, true
        );
        timePickerDialog.show();
    }

    private void saveMedicineData() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "User not authenticated. Please login again.", Toast.LENGTH_SHORT).show();
            return;
        }

        String name = edtName.getText().toString().trim();
        String dosage = edtDosage.getText().toString().trim();
        String stockStr = edtStock.getText().toString().trim();

        if (name.isEmpty()) {
            edtName.setError("Medicine name is required");
            return;
        }
        if (dosage.isEmpty()) {
            edtDosage.setError("Dosage is required");
            return;
        }
        if (stockStr.isEmpty()) {
            edtStock.setError("Stock quantity is required");
            return;
        }
        if (selectedHour == -1 || selectedMinute == -1) {
            Toast.makeText(this, "Please select a time", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            int stock = Integer.parseInt(stockStr);

            // Create temporary ID (we will replace it later)
            Medicine medicine = new Medicine(name, dosage, selectedHour, selectedMinute, stock, user.getUid());

            db.collection("users")
                    .document(user.getUid())
                    .collection("medicines")
                    .add(medicine)
                    .addOnSuccessListener(documentReference -> {
                        // Update document ID as medicine ID
                        documentReference.update("id", documentReference.getId());
                        medicine.setId(documentReference.getId());
                        setMedicineReminderAlarm(medicine);

                        Toast.makeText(this, "Medicine saved and alarm set!", Toast.LENGTH_SHORT).show();
                        clearFields();
                    })
                    .addOnFailureListener(e -> {
                        Log.e("FirestoreError", "Error adding document", e);
                        Toast.makeText(this, "Failed to save medicine", Toast.LENGTH_SHORT).show();
                    });

        } catch (NumberFormatException e) {
            edtStock.setError("Enter a valid stock number");
        }
    }

    private void setMedicineReminderAlarm(Medicine medicine) {
        // For Android 12+, check permission for exact alarms
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            if (alarmManager != null && !alarmManager.canScheduleExactAlarms()) {
                Toast.makeText(this, "Please enable exact alarms in settings.", Toast.LENGTH_LONG).show();
                return;
            }
        }

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, medicine.getHour());
        calendar.set(Calendar.MINUTE, medicine.getMinute());
        calendar.set(Calendar.SECOND, 0);

        // যদি টাইম পাস হয়ে যায় তাহলে পরের দিন সেট করো
        if (calendar.before(Calendar.getInstance())) {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        Intent intent = new Intent(MedicineReminderActivity.this, MedicineReminderReceiver.class);
        intent.putExtra("medicine_name", medicine.getName());

        int requestCode = medicine.getId().hashCode(); // Convert unique string ID to int

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                MedicineReminderActivity.this,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(),
                    pendingIntent
            );
        }
    }

    private void clearFields() {
        edtName.setText("");
        edtDosage.setText("");
        edtStock.setText("");
        txtPickedTime.setText("No time selected");
        selectedHour = -1;
        selectedMinute = -1;
    }
}
