package com.revive.medicinereminder;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class DismissReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String name = intent.getStringExtra("medicine_name");

        Toast.makeText(context, name + " dismissed!", Toast.LENGTH_SHORT).show();
    }
}
