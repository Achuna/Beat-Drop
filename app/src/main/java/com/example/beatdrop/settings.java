package com.example.beatdrop;

import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

public class settings extends AppCompatActivity {

    //UI Variables
    LinearLayout layout;
    Switch darkThemeSwitch, notificationSwitch, offlineSwitch;
    Button saveSettings;
    TextView darkTitle, notifTitle, offlineTitle;
    SharedPreferences sharedpreferences;
    boolean isDark, isNotify, isOffline; //from saved preferences
    boolean darkTheme, notifications, offline; //from context values
    int confirmationChoice = 0; //0 = no confirmation, 1 = yes, 2 = no
    //Dialog variables
    Dialog confirmationDialog;
    TextView title, description;
    Button decline, accept;
    ImageView exit;

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        confirmationDialog = new Dialog(this);
        //Prepare preferences
        sharedpreferences = getSharedPreferences("MySettings", Context.MODE_PRIVATE);
        isDark = sharedpreferences.getBoolean("darkEnabled", false);
        isNotify = sharedpreferences.getBoolean("notifyEnabled", false);
        isOffline = sharedpreferences.getBoolean("offlineEnabled", false);

        //Connect Buttons
        darkThemeSwitch = findViewById(R.id.dark_theme_switch);
        notificationSwitch = findViewById(R.id.notificationSwitch);
        offlineSwitch = findViewById(R.id.offlineSwitch);
        saveSettings = findViewById(R.id.save_settings);
        layout = findViewById(R.id.settingsActivityLayout);

        //UI Texts
        darkTitle = findViewById(R.id.darkThemeLabel);
        notifTitle = findViewById(R.id.notificationLabel);
        offlineTitle = findViewById(R.id.OfflineMusic);

        //Initialize Theme
        if(isDark) {
            layout.setBackground(getApplicationContext().getDrawable(R.drawable.simple_dark_back));
            darkTitle.setTextColor(Color.WHITE);
            notifTitle.setTextColor(Color.WHITE);
            offlineTitle.setTextColor(Color.WHITE);
        } else {
            layout.setBackground(getApplicationContext().getDrawable(R.drawable.simple_light_back));
            darkTitle.setTextColor(Color.BLACK);
            notifTitle.setTextColor(Color.BLACK);
            offlineTitle.setTextColor(Color.BLACK);
        }

        //Set switches based on shared prefences values
        darkThemeSwitch.setChecked(isDark);
        notificationSwitch.setChecked(isNotify);
        offlineSwitch.setChecked(isOffline);


        //Dark theme switch update view
        darkThemeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    layout.setBackground(getApplicationContext().getDrawable(R.drawable.simple_dark_back));
                    darkTitle.setTextColor(Color.WHITE);
                    notifTitle.setTextColor(Color.WHITE);
                    offlineTitle.setTextColor(Color.WHITE);
                } else {
                    layout.setBackground(getApplicationContext().getDrawable(R.drawable.simple_light_back));
                    darkTitle.setTextColor(Color.BLACK);
                    notifTitle.setTextColor(Color.BLACK);
                    offlineTitle.setTextColor(Color.BLACK);
                }
            }
        });


        saveSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = sharedpreferences.edit();
                darkTheme = darkThemeSwitch.isChecked();
                notifications = notificationSwitch.isChecked();
                offline = offlineSwitch.isChecked();
                isDark = darkTheme;
                isNotify = notifications;
                isOffline = offline;
                editor.putBoolean("darkEnabled", darkTheme);
                editor.putBoolean("notifyEnabled", notifications);
                editor.putBoolean("offlineEnabled", offline);
                editor.commit();

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

                    Intent start = new Intent(getApplicationContext(), AlarmService.class);
                    startService(start);

                }

                finish();
            }
        });

    }

    @Override
    public void finish() {
        //Check for any changes before going back
        darkTheme = darkThemeSwitch.isChecked();
        notifications = notificationSwitch.isChecked();
        offline = offlineSwitch.isChecked();

        //If changes are found, show dialog
        boolean changesFound = (isDark != darkTheme) || (isNotify != notifications) || (isOffline != offline);
        if(changesFound && confirmationChoice == 0) {

            //Connect elements
            confirmationDialog.setContentView(R.layout.confirmation_dialog);
            title = confirmationDialog.findViewById(R.id.confirmationTitle);
            description = confirmationDialog.findViewById(R.id.confirmationDes);
            accept = confirmationDialog.findViewById(R.id.accept);
            decline = confirmationDialog.findViewById(R.id.decline);
            exit = confirmationDialog.findViewById(R.id.confirmExit);

            title.setText("Changes Found");
            description.setText("Exit without saving?");

            exit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    confirmationDialog.dismiss();
                    confirmationChoice = 2;
                    finish();
                }
            });
            accept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    confirmationChoice = 1;
                    confirmationDialog.dismiss();
                    finish();
                }
            });

            decline.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    confirmationChoice = 2;
                    confirmationDialog.dismiss();
                    finish();
                }
            });

            confirmationDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            confirmationDialog.show();

            //No Changes in settings
        } else if(confirmationChoice == 1) {
            super.finish();
            //When back button is pressed override animation (Slide to the left)
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        } else if(confirmationChoice == 2){
            confirmationChoice = 0;
        }
        else {
            super.finish();
            //When back button is pressed override animation (Slide to the left)
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        }
    }


}
