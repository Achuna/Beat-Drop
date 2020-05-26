package com.example.beatdrop;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    //Initialize UI components
    Button addMusic;
    ImageButton playButton;
    CardView songCard;
    CardView moodCard;
    CardView settingsCard;
    //Dialog Items
    Dialog musicDialog;
    EditText songInput;
    ImageView happyChoice, chillChoice, sadChoice, angryChoice, romanticChoice, funnyChoice, addCancel;
    Button musicSave;

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //Connect UI Elements
        addMusic = findViewById(R.id.addMusic);
        playButton = findViewById(R.id.playButton);
        songCard = findViewById(R.id.songCard);
        moodCard = findViewById(R.id.moodCard);
        settingsCard = findViewById(R.id.settingsCard);

        musicDialog = new Dialog(this);

        //Pop up dialog box to add songs
        addMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMusicDialog();
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
                Intent moodNavigate = new Intent(getApplicationContext(), mood.class);
                startActivity(moodNavigate);
            }
        });

        //Song card Action (Go to mood screen)
        songCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent songsNavigate = new Intent(getApplicationContext(), songs.class);
                startActivity(songsNavigate);
                //Override animation to next activity (Slide to the right)
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });

        ///////////////End of Main Activity Navigation Cards////////////////


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

