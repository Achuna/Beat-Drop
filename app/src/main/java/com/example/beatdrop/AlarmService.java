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
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
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

                Calendar now = Calendar.getInstance();
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(System.currentTimeMillis());

                calendar.set(Calendar.HOUR_OF_DAY, 9);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);

                Intent alarmIntent = new Intent(getApplicationContext(), MoodReceiver.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                long diff = now.getTimeInMillis() - calendar.getTimeInMillis();
                long dailyInterval = 1000 * 60 * 60 * 24;
                AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

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

                Intent mainalarmIntent = new Intent(getApplicationContext(), NotificationReceiver.class);
                PendingIntent mainpendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, mainalarmIntent, 0);

                AlarmManager mainalarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

                long mainDiff = mainnow.getTimeInMillis() - maincalendar.getTimeInMillis();
                if(mainDiff > 0) {
                    mainalarmManager.cancel(mainpendingIntent);
                } else {
                    mainalarmManager.setRepeating(AlarmManager.RTC_WAKEUP, maincalendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, mainpendingIntent);
                }

            }


            Log.i("AService", "Alarm Service Ran ----------------------------------------------");

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

