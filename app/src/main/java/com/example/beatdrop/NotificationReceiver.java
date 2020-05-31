package com.example.beatdrop;

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
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.ArrayList;
import java.util.Random;

////Receive intent to display notification

public class NotificationReceiver extends BroadcastReceiver {
    NotificationCompat.Builder builder;
    SharedPreferences settings;

    @Override
    public void onReceive(final Context context, Intent intent) {



        builder = new NotificationCompat.Builder(context, "Beat Drop Reminder")
                .setSmallIcon(R.drawable.notification_icon)
                .setContentTitle("Beat Drop")
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.notimg))
                .setContentText("New Beat Drop Available! Tap to Drop")
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_SOUND)
                .setPriority(NotificationCompat.PRIORITY_HIGH);



        final Intent listen = new Intent(Intent.ACTION_VIEW);

        new Restore(context, "", 1, new Restore.AsyncResponse() {
            @Override
            public void processFinished(ArrayList<Song> cloudSongs) {
                SharedPreferences moodPref = context.getSharedPreferences("MyMood", Context.MODE_PRIVATE);
                String mood = moodPref.getString("mood", "happy");

                if(cloudSongs.size() > 0) {
                    ArrayList<String> personalizedSongs = new ArrayList<>();
                    for (int i = 0; i < cloudSongs.size(); i++) {
                        if(cloudSongs.get(i).getMood().equals(mood)) //Add song if it matches the mood
                            personalizedSongs.add(cloudSongs.get(i).getLink());
                    }
                    //Pick random song from personalized array
                    Random rand = new Random();
                    String selectedSongLink = personalizedSongs.get(rand.nextInt(personalizedSongs.size()));
                    listen.setData(Uri.parse(selectedSongLink));
                    PendingIntent startListening = PendingIntent.getActivity(context, 300, listen, PendingIntent.FLAG_UPDATE_CURRENT);
                    builder.setContentIntent(startListening);
                    Log.e("Notification", "NOTIFIED");
                    NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);


                    settings = context.getSharedPreferences("MySettings", Context.MODE_PRIVATE);
                    if(settings.getBoolean("notifyEnabled", false))
                        notificationManager.notify(200, builder.build());
                }



            }
        }).execute();


    }
}
