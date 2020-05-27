package com.example.beatdrop;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    //Initialize UI components
    LinearLayout layout;
    Button addMusic;
    ImageButton playButton;
    CardView songCard;
    CardView moodCard;
    CardView settingsCard;
    ImageView globalMood, songsIcon;
    TextView actionDes, songLabel, moodLabel, settingsLabel, devLabel;
    //Dialog Items
    Dialog musicDialog;
    EditText songInput;
    ImageView happyChoice, chillChoice, sadChoice, angryChoice, romanticChoice, funnyChoice, addCancel;
    Button musicSave;
    //Prepare Preferences
    SharedPreferences preferences;
    SharedPreferences moodPreferences;

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //Connect UI Elements
        layout = findViewById(R.id.mainActivityLayout);
        addMusic = findViewById(R.id.addMusic);
        playButton = findViewById(R.id.playButton);
        songCard = findViewById(R.id.songCard);
        moodCard = findViewById(R.id.moodCard);
        settingsCard = findViewById(R.id.settingsCard);
        globalMood = findViewById(R.id.globalMood);
        songsIcon = findViewById(R.id.main_songs_icon);

        //Text Elements
        actionDes = findViewById(R.id.actionDescription);
        songLabel = findViewById(R.id.songs_label);
        moodLabel = findViewById(R.id.mood_label);
        settingsLabel = findViewById(R.id.settings_label);
        devLabel = findViewById(R.id.dev_label);

        applyTheme();

        musicDialog = new Dialog(this);

        //Pop up dialog box to add songs
        addMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMusicDialog();
            }
        });
        playButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showMusicDialog();
                return true;
            }
        });

        ///////////////Main Activity Navigation Cards////////////////

        //Settings Action (Go to settings screen)
        settingsCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent settingsNavigate = new Intent(getApplicationContext(), settings.class);
                startActivity(settingsNavigate);
                //Override animation to next activity (Slide to the right)
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        //Mood Action (Go to mood screen)
        moodCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Check if offline before proceeding to change mood
                preferences = getSharedPreferences("MySettings", Context.MODE_PRIVATE);
                boolean isOffline = preferences.getBoolean("offlineEnabled", false);
                if(isOffline) {
                    Toast offlineMsg = Toast.makeText(getApplicationContext(), "Offline Music Enabled", Toast.LENGTH_SHORT);
                    offlineMsg.show();
                } else if(!isConnected()) {
                    Toast offlineMsg = Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT);
                    offlineMsg.show();
                } else {
                    Intent moodNavigate = new Intent(getApplicationContext(), mood.class);
                    startActivity(moodNavigate);
                }
            }
        });

        //Song card Action (Go to mood screen)
        songCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Check if offline before proceeding to view songs
                preferences = getSharedPreferences("MySettings", Context.MODE_PRIVATE);
                boolean isOffline = preferences.getBoolean("offlineEnabled", false);
                if(isOffline) {
                    Toast offlineMsg = Toast.makeText(getApplicationContext(), "Offline Music Enabled", Toast.LENGTH_SHORT);
                    offlineMsg.show();
                } else if(!isConnected()) {
                    Toast offlineMsg = Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT);
                    offlineMsg.show();
                } else {
                    Intent songsNavigate = new Intent(getApplicationContext(), songs.class);
                    startActivity(songsNavigate);
                    //Override animation to next activity (Slide to the right)
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                }
            }
        });

        ///////////////End of Main Activity Navigation Cards////////////////


    }

    @Override
    protected void onRestart() {
        applyTheme();
        super.onRestart();
    }

    public void applyTheme() {
        preferences = getSharedPreferences("MySettings", Context.MODE_PRIVATE);
        boolean isDark = preferences.getBoolean("darkEnabled", false);
        boolean isNotify = preferences.getBoolean("notifyEnabled", false);
        boolean isOffline = preferences.getBoolean("offlineEnabled", false);

        moodPreferences = getSharedPreferences("MyMood", Context.MODE_PRIVATE);
        String mood = moodPreferences.getString("mood", "happy");
        if(mood.equals("happy")) {
            globalMood.setImageResource(R.drawable.happy);
        } else if(mood.equals("chill")) {
            globalMood.setImageResource(R.drawable.chill);
        } else if(mood.equals("sad")) {
            globalMood.setImageResource(R.drawable.sad);
        } else if(mood.equals("angry")) {
            globalMood.setImageResource(R.drawable.angry);
        } else if(mood.equals("romantic")) {
            globalMood.setImageResource(R.drawable.romantic);
        } else if(mood.equals("funny")) {
            globalMood.setImageResource(R.drawable.funny);
        }

        //Test Network State
        if (isOffline) {
            globalMood.setImageResource(R.drawable.offline);
            songsIcon.setImageResource(R.drawable.offline);
        } else if(!isConnected()) {
            globalMood.setImageResource(R.drawable.offline);
            songsIcon.setImageResource(R.drawable.offline);
        } else {
            songsIcon.setImageResource(R.drawable.songs_icon);
        }

        //Add theme to UI elements
        if(isDark) {
            layout.setBackground(getApplicationContext().getDrawable(R.drawable.simple_dark_back));
            songCard.setCardBackgroundColor(0xFF636262);
            moodCard.setCardBackgroundColor(0xFF636262);
            settingsCard.setCardBackgroundColor(0xFF636262);
            actionDes.setTextColor(Color.WHITE);
            songLabel.setTextColor(Color.WHITE);
            moodLabel.setTextColor(Color.WHITE);
            settingsLabel.setTextColor(Color.WHITE);
            devLabel.setTextColor(Color.WHITE);
        } else {
            layout.setBackground(getApplicationContext().getDrawable(R.drawable.simple_light_back));
            songCard.setCardBackgroundColor(Color.WHITE);
            moodCard.setCardBackgroundColor(Color.WHITE);
            settingsCard.setCardBackgroundColor(Color.WHITE);
            actionDes.setTextColor(Color.BLACK);
            songLabel.setTextColor(Color.BLACK);
            moodLabel.setTextColor(Color.BLACK);
            settingsLabel.setTextColor(Color.BLACK);
            devLabel.setTextColor(Color.BLACK);
        }
    }

    public boolean isConnected() {
        ConnectivityManager cm =
                (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        Network activeNetwork = cm.getActiveNetwork();
        boolean isConnected = activeNetwork != null;
        return isConnected;
    }


    //Displays music dialog
    public void showMusicDialog() {
        //Connect elements
        musicDialog.setContentView(R.layout.add_item);
        songInput = musicDialog.findViewById(R.id.songTextField);
        addCancel = musicDialog.findViewById(R.id.addCancel);
        happyChoice = musicDialog.findViewById(R.id.happyChoice);
        chillChoice = musicDialog.findViewById(R.id.chillChoice);
        sadChoice = musicDialog.findViewById(R.id.sadChoice);
        angryChoice = musicDialog.findViewById(R.id.angryChoice);
        romanticChoice = musicDialog.findViewById(R.id.romanticChoice);
        funnyChoice = musicDialog.findViewById(R.id.funnyChoice);
        musicSave = musicDialog.findViewById(R.id.addMusicBtn);

        addCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                musicDialog.dismiss();
            }
        });

        musicDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        musicDialog.show();
    }
}

