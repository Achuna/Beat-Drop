package com.example.beatdrop;

import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.ArrayList;
import java.util.Random;

////Receive intent to display notification

public class MoodReceiver extends BroadcastReceiver {
    NotificationCompat.Builder builder;
    SharedPreferences settings;

    @Override
    public void onReceive(final Context context, Intent intent) {

        builder = new NotificationCompat.Builder(context, "Beat Drop Mood")
                .setSmallIcon(R.drawable.notification_icon)
                .setContentTitle("Beat Drop")
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.notimg))
                .setContentText("How are you feeling?")
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_SOUND)
                .setPriority(NotificationCompat.PRIORITY_HIGH);


        final Intent updateIntent = new Intent(context, MoodUpdate.class);
        PendingIntent waitForUpdate = PendingIntent.getActivity(context, 350, updateIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(waitForUpdate);

        Log.e("Notification", "NOTIFIED");
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        settings = context.getSharedPreferences("MySettings", Context.MODE_PRIVATE);
        if(settings.getBoolean("notifyEnabled", false))
            notificationManager.notify(250, builder.build());

    }
}
