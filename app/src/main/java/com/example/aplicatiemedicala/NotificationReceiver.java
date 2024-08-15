package com.example.aplicatiemedicala;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

public class NotificationReceiver extends BroadcastReceiver {
    private static final String TAG = "NotificationReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {
        String title = intent.getStringExtra("title");
        String message  =intent.getStringExtra("message");
        String doctor =intent.getStringExtra("doctor");

        Log.d("NotificationReceiver", "Received notification: " + title + " - " + message +" - "+doctor);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        String channelId = "reminder_channel";
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel(channelId, "Reminder Notifications", NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);

        }

        NotificationCompat.Builder builder =new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.baseline_add_alarm_24)
                .setContentTitle(title)
                .setContentText(message)
                .setContentText(doctor)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        notificationManager.notify(0, builder.build());
        Log.d(TAG, "Notification displayed");
    }
}
