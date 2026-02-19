package com.revive.medicinereminder;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;

import androidx.core.app.NotificationCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String medicineId = intent.getStringExtra("medicine_id");
        String medicineName = intent.getStringExtra("medicine_name");
        int stock = intent.getIntExtra("stock", 0);

        Intent takenIntent = new Intent(context, NotificationActionReceiver.class);
        takenIntent.setAction("ACTION_TAKEN");
        takenIntent.putExtra("medicine_id", medicineId);
        takenIntent.putExtra("stock", stock);

        PendingIntent takenPendingIntent = PendingIntent.getBroadcast(context, 0, takenIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        Intent dismissIntent = new Intent(context, NotificationActionReceiver.class);
        dismissIntent.setAction("ACTION_DISMISS");

        PendingIntent dismissPendingIntent = PendingIntent.getBroadcast(context, 1, dismissIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        String channelId = "medicine_reminder_channel";
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, "Medicine Reminder", NotificationManager.IMPORTANCE_HIGH);
            channel.enableLights(true);
            channel.setLightColor(Color.BLUE);
            channel.enableVibration(true);
            manager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.drugs) // 🛑 তোমার আইকন লাগবে
                .setContentTitle("Medicine Reminder")
                .setContentText("Time to take: " + medicineName)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .addAction(R.drawable.done, "Taken", takenPendingIntent)
                .addAction(R.drawable.close, "Dismiss", dismissPendingIntent);

        manager.notify(100, builder.build());
    }
}
