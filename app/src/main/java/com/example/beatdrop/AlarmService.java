package com.example.beatdrop;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import java.util.Calendar;

/**
 * Created by Achuna on 3/10/2018.
 *
 * Repeatedly Sets the alarm for each show
 */

public class AlarmService extends Service {
    private boolean isRunning;
    private Context context;
    private Thread backgroundThread;
    SharedPreferences preferences;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        this.context = this;
        this.isRunning = false;
        this.preferences = context.getSharedPreferences("MySettings", MODE_PRIVATE);
        this.backgroundThread = new Thread(myTask);
    }

    //This tasks will run in the background and set the alarms every 30 minutes
    private Runnable myTask = new Runnable() {
        public void run() {
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
                    NotificationManager notificationManager = getSystemService(NotificationManager.class);

                    notificationManager.createNotificationChannel(channel);
                    notificationManager.createNotificationChannel(mchannel);
                }

                //hour and minute to receive consecutive notifications
                int[] times = {9, 10, 0, 0};

                Calendar now = Calendar.getInstance();
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(System.currentTimeMillis());

                calendar.set(Calendar.HOUR_OF_DAY, times[0]);
                calendar.set(Calendar.MINUTE, times[2]);
                calendar.set(Calendar.SECOND, 0);

                Intent alarmIntent = new Intent(getApplicationContext(), MoodReceiver.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                long diff = now.getTimeInMillis() - calendar.getTimeInMillis();
                long timeInterval = 1000 * 60 * 60 * 4;
                AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

                if(diff > 0) {
                    //alarmManager.cancel(pendingIntent);
                } else {
                    alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), timeInterval, pendingIntent);
                }


                //////////MAIN NOTIFICATION//////////////
                Calendar mainnow = Calendar.getInstance();
                Calendar maincalendar = Calendar.getInstance();
                maincalendar.setTimeInMillis(System.currentTimeMillis());

                maincalendar.set(Calendar.HOUR_OF_DAY, times[1]);
                maincalendar.set(Calendar.MINUTE, times[3]);
                maincalendar.set(Calendar.SECOND, 0);

                Intent mainalarmIntent = new Intent(getApplicationContext(), NotificationReceiver.class);
                PendingIntent mainpendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, mainalarmIntent, 0);

                AlarmManager mainalarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

                long mainDiff = mainnow.getTimeInMillis() - maincalendar.getTimeInMillis();
                if(mainDiff > 0) {
                    //mainalarmManager.cancel(mainpendingIntent);
                } else {
                    mainalarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, maincalendar.getTimeInMillis(), timeInterval, mainpendingIntent);
                }

                Intent serviceIntent = new Intent(getApplicationContext(), AlarmService.class);
                PendingIntent pendingService = PendingIntent.getService(getApplicationContext(), 0, serviceIntent, 0);

                AlarmManager serviceManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                serviceManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, now.getTimeInMillis(), 60000*30, pendingService);

                Log.i("AService", "Alarm Service Ran ----------------------------------------------");
            }

            stopSelf();
        }
    };

    @Override
    public void onDestroy() {
        this.isRunning = false;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(!this.isRunning) {
            this.isRunning = true;
            this.backgroundThread.start();
        }
        return START_STICKY;
    }

}

