package com.example.beatdrop;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class songs extends AppCompatActivity {

    LinearLayout layout;
    ImageView cloudBtn, cancel;
    ListView songList;
    SharedPreferences preferences;
    //Dialog Items
    Dialog cloudDialog;
    TextView backupText;
    Button restoreBtn, backupBtn;
    //Update Dialog box
    Dialog updateDialog;
    TextView textField;
    ImageView playSong, updateCancel;
    Button shareSong, deleteSongBtn;
    //Prepare database
    SongDbHelper database;
    boolean isDark;
    ArrayList<Song> songs;
    String mood;

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
        updateDialog = new Dialog(this);
        database = new SongDbHelper(this);

        applyTheme();

        songs = loadLocalSongData();

        final SongListAdapter adapter = new SongListAdapter(getApplicationContext(), songs, isDark);
        songList.setAdapter(adapter);

        songList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                mood = songs.get(position).getMood();
                //Update Song Dialog
                updateDialog.setContentView(R.layout.update_item);
                textField = updateDialog.findViewById(R.id.updatesongTextField);
                updateCancel = updateDialog.findViewById(R.id.updateCancel);

                deleteSongBtn = updateDialog.findViewById(R.id.deleteMusicBtn);
                playSong = updateDialog.findViewById(R.id.playSelectedSong);
                shareSong = updateDialog.findViewById(R.id.shareMusic);

                textField.setText(songs.get(position).getLink());

                deleteSongBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String link = textField.getText().toString();
                        database.deleteMusic(link);
                        songs.remove(position); //temp arraylist updater when listview refreshes
                        adapter.notifyDataSetChanged();
                        Toast.makeText(getApplicationContext(), "Song Deleted", Toast.LENGTH_SHORT).show();
                        updateDialog.dismiss();
                    }
                });

                shareSong.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //This will allow the user to select options to share their music url
                        Intent sendIntent = new Intent();
                        sendIntent.setAction(Intent.ACTION_SEND);
                        sendIntent.putExtra(Intent.EXTRA_TEXT, textField.getText().toString());
                        sendIntent.setType("text/plain");
                        startActivity(Intent.createChooser(sendIntent, "Share"));
                    }
                });


                updateCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        updateDialog.dismiss();
                    }
                });

                updateDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                updateDialog.show();
            }


        });


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
        isDark = preferences.getBoolean("darkEnabled", false);
        //Add theme to UI elements
        if(isDark) {
            layout.setBackground(getApplicationContext().getDrawable(R.drawable.simple_dark_back));
        } else {
            layout.setBackground(getApplicationContext().getDrawable(R.drawable.simple_light_back));
        }
    }

    ////////////////////DATA METHODS//////////////////////


    //Load songs from local SQLite database on device
    public ArrayList<Song> loadLocalSongData() {
        ArrayList<Song> localSongs = new ArrayList<>();
        Cursor data = database.getData();
        while (data.moveToNext()) {
            String link = data.getString(1); //get link
            String mood = data.getString(2); //get mood
            Song songItem = new Song(link, mood);
            localSongs.add(songItem);
        }
        return localSongs;
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
