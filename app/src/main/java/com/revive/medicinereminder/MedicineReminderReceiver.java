package com.revive.medicinereminder;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.os.Build;

import androidx.core.app.NotificationCompat;

public class MedicineReminderReceiver extends BroadcastReceiver {

    private static final String CHANNEL_ID = "medicine_reminder_channel";

    @Override
    public void onReceive(Context context, Intent intent) {

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Step 1: Notification Channel (with Alarm sound)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Medicine Reminders",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Channel for medicine reminders");
            channel.enableLights(true);
            channel.setLightColor(Color.BLUE);
            channel.enableVibration(true);
            channel.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM), null);

            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }

        String medicineName = intent.getStringExtra("medicine_name");

        // Step 2: Taken & Dismiss action intents
        Intent takenIntent = new Intent(context, com.example.revive.TakenReceiver.class);
        takenIntent.putExtra("medicine_name", medicineName);
        PendingIntent takenPendingIntent = PendingIntent.getBroadcast(
                context, 0, takenIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        Intent dismissIntent = new Intent(context, com.example.revive.DismissReceiver.class);
        dismissIntent.putExtra("medicine_name", medicineName);
        PendingIntent dismissPendingIntent = PendingIntent.getBroadcast(
                context, 1, dismissIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // Notification tap opens DashboardActivity
        Intent notificationIntent = new Intent(context, DashboardActivity.class);
        PendingIntent contentPendingIntent = PendingIntent.getActivity(
                context, 2, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // Build the Notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.logo)  // নিশ্চিত হও যে এই আইকন তোমার drawable এ আছে
                .setContentTitle("Time to take your medicine 💊")
                .setContentText("Medicine: " + medicineName)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM))
                .setContentIntent(contentPendingIntent)
                .addAction(R.drawable.done, "Taken", takenPendingIntent)      // Taken action button
                .addAction(R.drawable.close, "Dismiss", dismissPendingIntent); // Dismiss action button

        if (notificationManager != null) {
            // ID ভিন্ন রেখে প্রতি নোটিফিকেশন আলাদা করে দেখাবে
            notificationManager.notify((int) System.currentTimeMillis(), builder.build());
        }
    }
}
