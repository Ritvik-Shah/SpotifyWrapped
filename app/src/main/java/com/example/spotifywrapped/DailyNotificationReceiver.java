package com.example.spotifywrapped;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class DailyNotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // Create and show the daily notification
        createNotification(context, "Daily Notification", "This is your daily notification message.");
    }

    private void createNotification(Context context, String title, String message) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "channel_id")
                .setSmallIcon(R.drawable.ic_notifications_black_24dp)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        //int notificationId = (int) System.currentTimeMillis();
        if (NotificationManagerCompat.from(context).areNotificationsEnabled()) {
            try {
                notificationManager.notify(123, builder.build());
                Toast.makeText(context, "Notification should pop up", Toast.LENGTH_SHORT).show();
            } catch (SecurityException e) {
                e.printStackTrace();
                // Handle exception here
                Toast.makeText(context, "Notification permission denied", Toast.LENGTH_SHORT).show();
            }
        } else {
            // Handle case when notifications are disabled
            Toast.makeText(context, "Notifications are disabled", Toast.LENGTH_SHORT).show();
        }
    }
}
