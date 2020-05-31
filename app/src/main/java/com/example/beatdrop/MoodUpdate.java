package com.example.beatdrop;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MoodUpdate extends AppCompatActivity {

    ImageView feelingChoice;
    CardView happyCard, chillCard, sadCard, angryCard, romanticCard, funnyCard;
    Button moodSave;
    TextView happyText, chillText, sadText, angryText, romanticText, funnyText;
    RelativeLayout layout;
    int confirmationChoice = 0; //0 = no confirmation, 1 = yes, 2 = no
    SharedPreferences moodPreferences;
    SharedPreferences settingsPreferences;
    String mood;
    String savedMood;

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mood_update);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        layout = findViewById(R.id.updatemoodActivityLayout);
        //Card views
        feelingChoice = findViewById(R.id.updatefeeling_choice);
        happyCard = findViewById(R.id.updatehappyCard);
        chillCard = findViewById(R.id.updatechillCard);
        sadCard = findViewById(R.id.updatesadCard);
        angryCard = findViewById(R.id.updateangryCard);
        romanticCard = findViewById(R.id.updateromanticCard);
        funnyCard = findViewById(R.id.updatefunnyCard);
        moodSave = findViewById(R.id.updatemood_save_btn);

        //Texts
        happyText = findViewById(R.id.updatehappyText);
        chillText = findViewById(R.id.updatechillText);
        sadText = findViewById(R.id.updatesadText);
        angryText= findViewById(R.id.updateangryText);
        romanticText= findViewById(R.id.updateromanticText);
        funnyText = findViewById(R.id.updatefunnyText);


        //Apply theme and mood
        applyTheme();

        //Card OnClickListeners
        happyCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                feelingChoice.setImageResource(R.drawable.happy);
                mood = "happy";
            }
        });
        chillCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                feelingChoice.setImageResource(R.drawable.chill);
                mood = "chill";
            }
        });
        sadCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                feelingChoice.setImageResource(R.drawable.sad);
                mood = "sad";
            }
        });
        angryCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                feelingChoice.setImageResource(R.drawable.angry);
                mood = "angry";
            }
        });
        romanticCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                feelingChoice.setImageResource(R.drawable.romantic);
                mood = "romantic";
            }
        });
        funnyCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                feelingChoice.setImageResource(R.drawable.funny);
                mood = "funny";
            }
        });

        moodSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    @Override
    protected void onRestart() {
        SharedPreferences.Editor editor = moodPreferences.edit();
        editor.putString("mood", mood);
        editor.putBoolean("justUpdate", true);
        editor.commit();
        finish();
        super.onRestart();
    }

    @Override
    protected void onPause() {
        finish();
        super.onPause();
    }

    @Override
    protected void onStop() {
        finish();
        super.onStop();
    }

    @Override
    public void finish() {
        //Save preference
        savedMood = mood;
        if(moodPreferences.getBoolean("justUpdate", false)) {
            super.finish();
        } else {
            SharedPreferences.Editor editor = moodPreferences.edit();
            editor.putString("mood", mood);
            editor.putBoolean("justUpdate", true);
            editor.commit();
            Toast.makeText(getApplicationContext(), "Mood Saved", Toast.LENGTH_SHORT/2).show();
            super.finish();
        }

    }

    public void applyTheme() {
        moodPreferences = getSharedPreferences("MyMood", Context.MODE_PRIVATE);
        savedMood = moodPreferences.getString("mood", "happy");
        mood = savedMood;
        settingsPreferences = getSharedPreferences("MySettings", Context.MODE_PRIVATE);
        boolean isDark = settingsPreferences.getBoolean("darkEnabled", false);


        if(savedMood.equals("happy")) {
            feelingChoice.setImageResource(R.drawable.happy);
        } else if(savedMood.equals("chill")) {
            feelingChoice.setImageResource(R.drawable.chill);
        } else if(savedMood.equals("sad")) {
            feelingChoice.setImageResource(R.drawable.sad);
        } else if(savedMood.equals("angry")) {
            feelingChoice.setImageResource(R.drawable.angry);
        } else if(savedMood.equals("romantic")) {
            feelingChoice.setImageResource(R.drawable.romantic);
        } else if(savedMood.equals("funny")) {
            feelingChoice.setImageResource(R.drawable.funny);
        }


        //Add theme to UI elements
        if(isDark) {
            layout.setBackground(getApplicationContext().getDrawable(R.drawable.simple_dark_back));
            happyCard.setCardBackgroundColor(0xFF636262);
            chillCard.setCardBackgroundColor(0xFF636262);
            sadCard.setCardBackgroundColor(0xFF636262);
            angryCard.setCardBackgroundColor(0xFF636262);
            romanticCard.setCardBackgroundColor(0xFF636262);
            funnyCard.setCardBackgroundColor(0xFF636262);
            happyText.setTextColor(Color.WHITE);
            chillText.setTextColor(Color.WHITE);
            sadText.setTextColor(Color.WHITE);
            angryText.setTextColor(Color.WHITE);
            romanticText.setTextColor(Color.WHITE);
            funnyText.setTextColor(Color.WHITE);
        } else {
            layout.setBackground(getApplicationContext().getDrawable(R.drawable.simple_light_back));
            happyCard.setCardBackgroundColor(Color.WHITE);
            chillCard.setCardBackgroundColor(Color.WHITE);
            sadCard.setCardBackgroundColor(Color.WHITE);
            angryCard.setCardBackgroundColor(Color.WHITE);
            romanticCard.setCardBackgroundColor(Color.WHITE);
            funnyCard.setCardBackgroundColor(Color.WHITE);
            happyText.setTextColor(Color.BLACK);
            chillText.setTextColor(Color.BLACK);
            sadText.setTextColor(Color.BLACK);
            angryText.setTextColor(Color.BLACK);
            romanticText.setTextColor(Color.BLACK);
            funnyText.setTextColor(Color.BLACK);
        }
    }

}
