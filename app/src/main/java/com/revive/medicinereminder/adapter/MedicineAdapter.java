package com.revive.medicinereminder.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.revive.medicinereminder.EditMedicineActivity;
import com.revive.medicinereminder.R;
import com.revive.medicinereminder.model.Medicine;

import java.util.List;

public class MedicineAdapter extends RecyclerView.Adapter<MedicineAdapter.MedicineViewHolder> {

    private Context context;
    private List<Medicine> medicineList;

    public MedicineAdapter(Context context, List<Medicine> medicineList) {
        this.context = context;
        this.medicineList = medicineList;
    }

    @NonNull
    @Override
    public MedicineViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_medicine, parent, false);
        return new MedicineViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MedicineViewHolder holder, int position) {
        Medicine medicine = medicineList.get(position);

        holder.textName.setText("Name: " + medicine.getName());
        holder.textDosage.setText("Dosage: " + medicine.getDosage());
        holder.textTime.setText(String.format("Time: %02d:%02d", medicine.getHour(), medicine.getMinute()));
        holder.textStock.setText("Stock: " + medicine.getStock());

        // Edit Click
        holder.textEdit.setOnClickListener(v -> {
            Intent intent = new Intent(context, EditMedicineActivity.class);
            intent.putExtra("medicine_id", medicine.getId());
            intent.putExtra("name", medicine.getName());
            intent.putExtra("dosage", medicine.getDosage());
            intent.putExtra("hour", medicine.getHour());
            intent.putExtra("minute", medicine.getMinute());
            intent.putExtra("stock", medicine.getStock());
            context.startActivity(intent);
        });

        // Delete Click
        holder.textDelete.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Delete Medicine")
                    .setMessage("Are you sure you want to delete this medicine?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        FirebaseFirestore.getInstance()
                                .collection("users")
                                .document(uid)
                                .collection("medicines")
                                .document(medicine.getId())
                                .delete()
                                .addOnSuccessListener(aVoid -> {
                                    medicineList.remove(position);
                                    notifyItemRemoved(position);
                                    Toast.makeText(context, "Deleted successfully", Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(context, "Failed to delete", Toast.LENGTH_SHORT).show();
                                });
                    })
                    .setNegativeButton("No", null)
                    .show();
        });
    }

    @Override
    public int getItemCount() {
        return medicineList.size();
    }

    public static class MedicineViewHolder extends RecyclerView.ViewHolder {

        TextView textName, textDosage, textTime, textStock, textEdit, textDelete;

        public MedicineViewHolder(@NonNull View itemView) {
            super(itemView);
            textName = itemView.findViewById(R.id.textMedicineName);
            textDosage = itemView.findViewById(R.id.textMedicineDosage);
            textTime = itemView.findViewById(R.id.textMedicineTime);
            textStock = itemView.findViewById(R.id.textMedicineStock);
            textEdit = itemView.findViewById(R.id.textEditMedicine);
            textDelete = itemView.findViewById(R.id.textDeleteMedicine);
        }
    }
}
