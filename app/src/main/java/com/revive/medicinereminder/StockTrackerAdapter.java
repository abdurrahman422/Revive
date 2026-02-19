package com.revive.medicinereminder;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import com.revive.medicinereminder.model.Medicine;

import java.util.List;

public class StockTrackerAdapter extends RecyclerView.Adapter<StockTrackerAdapter.ViewHolder> {

    private List<Medicine> medicineList;

    public StockTrackerAdapter(List<Medicine> medicineList) {
        this.medicineList = medicineList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.medicine_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Medicine medicine = medicineList.get(position);
        holder.name.setText(medicine.getName());
        holder.stock.setText("Stock: " + medicine.getStock());
    }

    @Override
    public int getItemCount() {
        return medicineList.size();
    }

    // Add this method to update the list
    public void updateList(List<Medicine> newList) {
        medicineList.clear();
        medicineList.addAll(newList);
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, stock;

        public ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.textMedicineName);
            stock = itemView.findViewById(R.id.textMedicineDosage); // Temporarily using dosage TextView to show stock
        }
    }
}
