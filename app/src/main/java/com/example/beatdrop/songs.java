package com.example.beatdrop;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class songs extends AppCompatActivity {

    LinearLayout layout;
    ImageView cloudBtn, cancel;
    ListView songList;
    SharedPreferences preferences;
    //Dialog Items
    Dialog cloudDialog;
    TextView backupText;
    Button restoreBtn, backupBtn;

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_songs);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        layout = findViewById(R.id.songsActivityLayout);
        cloudBtn = findViewById(R.id.cloudMusciImage);
        songList = findViewById(R.id.songs_list);

        cloudDialog = new Dialog(this);

        applyTheme();

        //Cloud Storage Menu
        cloudBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBackupDialog();
            }
        });

    }

    //Displays cloud dialog
    public void showBackupDialog() {
        //Connect elements
        cloudDialog.setContentView(R.layout.storage_dialog);
        backupText = cloudDialog.findViewById(R.id.backupText);
        cancel = cloudDialog.findViewById(R.id.storageCancel);
        restoreBtn = cloudDialog.findViewById(R.id.restoreMusicBtn);
        backupBtn = cloudDialog.findViewById(R.id.backupMusicBtn);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cloudDialog.dismiss();
            }
        });

        cloudDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        cloudDialog.show();
    }

    public void applyTheme() {
        preferences = getSharedPreferences("MySettings", Context.MODE_PRIVATE);
        boolean isDark = preferences.getBoolean("darkEnabled", false);
        //Add theme to UI elements
        if(isDark) {
            layout.setBackground(getApplicationContext().getDrawable(R.drawable.simple_dark_back));
        } else {
            layout.setBackground(getApplicationContext().getDrawable(R.drawable.simple_light_back));
        }
    }

    @Override
    protected void onRestart() {
        applyTheme();
        super.onRestart();
    }

    @Override
    public void finish() {
        super.finish();
        //When back button is pressed override animation (Slide to the left)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }
}
