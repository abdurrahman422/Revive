package com.revive.medicinereminder.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.revive.medicinereminder.R;
import com.revive.medicinereminder.model.Medicine;

import java.util.List;

public class StockMedicineAdapter extends RecyclerView.Adapter<StockMedicineAdapter.ViewHolder> {

    private Context context;
    private List<Medicine> medicineList;

    public StockMedicineAdapter(Context context, List<Medicine> medicineList) {
        this.context = context;
        this.medicineList = medicineList;
    }

    @NonNull
    @Override
    public StockMedicineAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_medicine_stock, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StockMedicineAdapter.ViewHolder holder, int position) {
        Medicine medicine = medicineList.get(position);
        holder.textName.setText(medicine.getName());
        holder.textStock.setText("Stock: " + medicine.getStock());
    }

    @Override
    public int getItemCount() {
        return medicineList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textName, textStock;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textName = itemView.findViewById(R.id.textMedicineName);
            textStock = itemView.findViewById(R.id.textMedicineStock);
        }
    }
}
