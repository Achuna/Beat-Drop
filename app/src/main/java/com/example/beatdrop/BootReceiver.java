package com.example.beatdrop;


import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;

import java.util.Calendar;

import static android.content.Context.ALARM_SERVICE;
import static android.content.Context.MODE_PRIVATE;


public class BootReceiver extends BroadcastReceiver {

    SharedPreferences preferences;

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {

            Log.i("AService", "Started Alarm Service From Device Boot--------------------------------------------");

            preferences = context.getSharedPreferences("MySettings", MODE_PRIVATE);
            boolean isNotify = preferences.getBoolean("notifyEnabled", false);
            if (isNotify) {
                //Create notification channel
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    //main channel
                    CharSequence name = "Beat Drop Reminder";
                    String description = "Channel for Beat Drop";
                    int importance = NotificationManager.IMPORTANCE_HIGH;
                    NotificationChannel channel = new NotificationChannel("Beat Drop Reminder", name, importance);

                    //Mood channel
                    CharSequence moodname = "Beat Drop Mood";
                    String mooddescription = "Channel for Beat Drop Mood";
                    int moodimportance = NotificationManager.IMPORTANCE_DEFAULT;
                    NotificationChannel mchannel = new NotificationChannel("Beat Drop Mood", moodname, moodimportance);

                    channel.setDescription(description);
                    mchannel.setDescription(mooddescription);
                    NotificationManager notificationManager = context.getSystemService(NotificationManager.class);

                    notificationManager.createNotificationChannel(channel);
                    notificationManager.createNotificationChannel(mchannel);
                }

                Calendar now = Calendar.getInstance();
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(System.currentTimeMillis());

                calendar.set(Calendar.HOUR_OF_DAY, 9);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);

                Intent alarmIntent = new Intent(context, MoodReceiver.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                long diff = now.getTimeInMillis() - calendar.getTimeInMillis();
                long dailyInterval = 1000 * 60 * 60 * 24;
                AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);

                if(diff > 0) {
                    alarmManager.cancel(pendingIntent);
                } else {
                    alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), dailyInterval, pendingIntent);
                }


                //////////MAIN NOTIFICATION//////////////
                Calendar mainnow = Calendar.getInstance();
                Calendar maincalendar = Calendar.getInstance();
                maincalendar.setTimeInMillis(System.currentTimeMillis());

                maincalendar.set(Calendar.HOUR_OF_DAY, 10);
                maincalendar.set(Calendar.MINUTE, 0);
                maincalendar.set(Calendar.SECOND, 0);

                Intent mainalarmIntent = new Intent(context, NotificationReceiver.class);
                PendingIntent mainpendingIntent = PendingIntent.getBroadcast(context, 0, mainalarmIntent, 0);

                AlarmManager mainalarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);

                long mainDiff = mainnow.getTimeInMillis() - maincalendar.getTimeInMillis();
                if(mainDiff > 0) {
                    mainalarmManager.cancel(mainpendingIntent);
                } else {
                    mainalarmManager.setRepeating(AlarmManager.RTC_WAKEUP, maincalendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, mainpendingIntent);
                }

            }
            Log.i("AService", "Started Alarm For BEAT DROP From Device Boot--------------------------------------------");

            Intent start = new Intent(context, AlarmService.class);
            context.startForegroundService(start);
            Log.i("AService", "Started Alarm For BEAT DROP From Device Boot--------------------------------------------");


        }




    }
}
