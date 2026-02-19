package com.revive.medicinereminder;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.revive.medicinereminder.model.Medicine;

import java.util.Calendar;
import java.util.Locale;

public class AddMedicineActivity extends AppCompatActivity {

    private EditText editTextMedicineName, editTextDosage, editTextStock;
    private TextView textViewPickedTime;
    private Button buttonPickTime, buttonSaveMedicine;

    private int pickedHour = -1, pickedMinute = -1;
    private FirebaseAuth auth;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_medicine);

        // Firebase initialization
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        // View bindings
        editTextMedicineName = findViewById(R.id.editTextMedicineName);
        editTextDosage = findViewById(R.id.editTextDosage);
        editTextStock = findViewById(R.id.editTextStock);
        textViewPickedTime = findViewById(R.id.textViewPickedTime);
        buttonPickTime = findViewById(R.id.buttonPickTime);
        buttonSaveMedicine = findViewById(R.id.buttonSaveMedicine);

        // Pick Time Button
        buttonPickTime.setOnClickListener(v -> showTimePicker());

        // Save Medicine Button
        buttonSaveMedicine.setOnClickListener(v -> saveMedicine());
    }

    private void showTimePicker() {
        final Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(
                AddMedicineActivity.this,
                (view, hourOfDay, minute1) -> {
                    pickedHour = hourOfDay;
                    pickedMinute = minute1;
                    String timeFormatted = String.format(Locale.getDefault(), "%02d:%02d", pickedHour, pickedMinute);
                    textViewPickedTime.setText(timeFormatted);
                },
                hour, minute, true
        );
        timePickerDialog.show();
    }

    private void saveMedicine() {
        String name = editTextMedicineName.getText().toString().trim();
        String dosage = editTextDosage.getText().toString().trim();
        String stockStr = editTextStock.getText().toString().trim();

        if (name.isEmpty() || dosage.isEmpty() || stockStr.isEmpty() || pickedHour == -1) {
            Toast.makeText(this, "Please fill all fields and pick a time", Toast.LENGTH_SHORT).show();
            return;
        }

        int stock = Integer.parseInt(stockStr);
        String userId = auth.getCurrentUser().getUid();

        Medicine medicine = new Medicine(null, name, dosage, pickedHour, pickedMinute, stock, userId);

        firestore.collection("users")
                .document(userId)
                .collection("medicines")
                .add(medicine)
                .addOnSuccessListener(documentReference -> {
                    String generatedId = documentReference.getId();
                    medicine.setId(generatedId);

                    firestore.collection("users")
                            .document(userId)
                            .collection("medicines")
                            .document(generatedId)
                            .set(medicine);

                    // ✅ Set Alarm for reminder
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(Calendar.HOUR_OF_DAY, pickedHour);
                    calendar.set(Calendar.MINUTE, pickedMinute);
                    calendar.set(Calendar.SECOND, 0);

                    Intent intent = new Intent(this, MedicineReminderReceiver.class);
                    intent.putExtra("medicine_name", name);

                    PendingIntent pendingIntent = PendingIntent.getBroadcast(
                            this,
                            (int) System.currentTimeMillis(),
                            intent,
                            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
                    );

                    AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                    if (alarmManager != null) {
                        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                    }

                    // ✅ Show success dialog
                    new AlertDialog.Builder(this)
                            .setTitle("Success 🎉")
                            .setMessage("Medicine saved and alarm set!\n\nName: " + name +
                                    "\nDosage: " + dosage +
                                    "\nTime: " + String.format(Locale.getDefault(), "%02d:%02d", pickedHour, pickedMinute) +
                                    "\nStock: " + stock)
                            .setPositiveButton("OK", (dialog, which) -> {
                                editTextMedicineName.setText("");
                                editTextDosage.setText("");
                                editTextStock.setText("");
                                textViewPickedTime.setText("No time selected");
                                pickedHour = -1;
                                pickedMinute = -1;
                            })
                            .show();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to save medicine", Toast.LENGTH_SHORT).show());
    }
}
