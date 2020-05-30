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
import android.icu.util.Calendar;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;

public class songs extends AppCompatActivity {

    LinearLayout layout;
    ImageView cloudBtn, cancel;
    ListView songList;
    SharedPreferences preferences;
    SharedPreferences backup;
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
    ArrayList<Song> songsInList;
    String mood;
    SongListAdapter adapter;
    //Local Settings UI Elements
    TextView remoteTextDes;
    EditText ipTextField;
    Button remoteBtn;
    ImageView localCancel;
    Dialog remoteDialog;

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
        remoteDialog = new Dialog(this);
        database = new SongDbHelper(this);

        applyTheme();

        songsInList = loadLocalSongData();

        adapter = new SongListAdapter(getApplicationContext(), songsInList, isDark);
        songList.setAdapter(adapter);

        songList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                mood = songsInList.get(position).getMood();
                //Update Song Dialog
                updateDialog.setContentView(R.layout.update_item);
                textField = updateDialog.findViewById(R.id.updatesongTextField);
                updateCancel = updateDialog.findViewById(R.id.updateCancel);

                deleteSongBtn = updateDialog.findViewById(R.id.deleteMusicBtn);
                playSong = updateDialog.findViewById(R.id.playSelectedSong);
                shareSong = updateDialog.findViewById(R.id.shareMusic);

                textField.setText(songsInList.get(position).getLink());

                playSong.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String songLink = textField.getText().toString();
                        try {
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(songLink));
                            startActivity(browserIntent);
                        } catch (Exception e) {
                            Toast.makeText(getApplicationContext(), "Error Playing Song", Toast.LENGTH_SHORT).show();
                        }

                    }
                });

                deleteSongBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        backup = getSharedPreferences("backup", Context.MODE_PRIVATE);
                        String ip = backup.getString("ip", "");
                        ArrayList<Song> songs = new ArrayList<>(); //expected only one value
                        final Song deleteSong = songsInList.get(position);
                        songs.add(deleteSong);
                        Toast.makeText(getApplicationContext(), "Deleting...", Toast.LENGTH_SHORT).show();
                        new Backup(getApplicationContext(), ip, 1, 0, new Backup.AsyncResponse() {
                            @Override
                            public void processFinished(boolean result) {
                                if(result) {
                                    database.deleteMusic(deleteSong.getLink()); //delete music to internal database
                                    songsInList.remove(position); //temp arraylist updater when listview refreshes
                                    Toast.makeText(getApplicationContext(), "Song Deleted", Toast.LENGTH_SHORT).show();
                                    setBackupTime();
                                    updateDialog.dismiss();
                                    adapter.notifyDataSetChanged();
                                } else {
                                    Toast.makeText(getApplicationContext(), "COULD NOT DELETE SONG", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }).execute(songs);
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

        backup = getSharedPreferences("backup", MODE_PRIVATE);
        String backText = "Last Backup: " + backup.getString("backupTime", "");
        backupText.setText(backText);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cloudDialog.dismiss();
            }
        });


        restoreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<Song> cloudSongs = new ArrayList<>();
                SharedPreferences backupPreference = getSharedPreferences("backup", Context.MODE_PRIVATE);
                String ip = backupPreference.getString("ip", "");
                Toast.makeText(getApplicationContext(), "Restoring...", Toast.LENGTH_SHORT).show();
                new Restore(getApplicationContext(), ip, 1, new Restore.AsyncResponse() {
                    @Override
                    public void processFinished(ArrayList<Song> cloudSongs) {
                        if (cloudSongs.size() > 0) {
                            database.clearMusic();
                            songsInList.clear();
                            for (int i = 0; i < cloudSongs.size(); i++) {
                                database.addMusic(cloudSongs.get(i));
                                songsInList.add(cloudSongs.get(i));
                            }
                            Collections.reverse(songsInList); //newest first
                            adapter.notifyDataSetChanged();
                            Toast.makeText(getApplicationContext(), "Songs Restored", Toast.LENGTH_SHORT).show();
                            cloudDialog.dismiss();
                        } else {
                            Toast.makeText(getApplicationContext(), "Error Restoring", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).execute();
            }
        });


        backupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<Song> localSongs = new ArrayList<>();
                Cursor data = database.getData();
                while (data.moveToNext()) {
                    localSongs.add(new Song(data.getString(1), data.getString(2)));
                }
                if(localSongs.size() > 0) {
                    Toast.makeText(getApplicationContext(), "Backing up...", Toast.LENGTH_SHORT).show();
                    new Backup(getApplicationContext(), "", 1, 1, new Backup.AsyncResponse() {
                        @Override
                        public void processFinished(boolean result) {
                            if(result) {
                                Toast.makeText(getApplicationContext(), "Backup Complete", Toast.LENGTH_SHORT).show();
                                cloudDialog.dismiss();
                                setBackupTime();
                            } else {
                                Toast.makeText(getApplicationContext(), "Backup Failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }).execute(localSongs);
                } else {
                    Toast.makeText(getApplicationContext(), "No Songs to Backup", Toast.LENGTH_SHORT).show();
                }

            }
        });

        ///////////////////////LONG PRESS FOR LOCAL BACKUP///////////////////////////

        backupBtn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                remoteDialog.setContentView(R.layout.local_storage_dialog);
                remoteTextDes = remoteDialog.findViewById(R.id.remoteDes);
                remoteBtn = remoteDialog.findViewById(R.id.remoteBackupDtn);
                ipTextField = remoteDialog.findViewById(R.id.ipTextField);
                localCancel = remoteDialog.findViewById(R.id.localCancel);

                remoteTextDes.setText("Back up songs to remote location");
                backup = getSharedPreferences("backup", MODE_PRIVATE);
                final String ip = backup.getString("ip", "");
                ipTextField.setText(ip);

                localCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        remoteDialog.dismiss();
                    }
                });

                remoteBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        ArrayList<Song> localSongs = new ArrayList<>();
                        Cursor data = database.getData();
                        while (data.moveToNext()) {
                            localSongs.add(new Song(data.getString(1), data.getString(2)));
                        }
                        if(localSongs.size() > 0) {
                            final String currentInputIp = ipTextField.getText().toString();
                            Toast.makeText(getApplicationContext(), "Backing up...", Toast.LENGTH_SHORT).show();
                            new Backup(getApplicationContext(), currentInputIp, 0, 1, new Backup.AsyncResponse() {
                                @Override
                                public void processFinished(boolean result) {
                                    if(result) {
                                        Toast.makeText(getApplicationContext(), "Backup Complete", Toast.LENGTH_SHORT).show();
                                        SharedPreferences.Editor editor = backup.edit();
                                        editor.putString("ip", currentInputIp);
                                        editor.commit();
                                        remoteDialog.dismiss();
                                        cloudDialog.dismiss();
                                        setBackupTime();
                                    } else {
                                        Toast.makeText(getApplicationContext(), "Backup Failed", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }).execute(localSongs);
                        } else {
                            Toast.makeText(getApplicationContext(), "No Songs to Backup", Toast.LENGTH_SHORT).show();
                        }


                    }
                });

                remoteDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                remoteDialog.show();
                return true;
            }
        });



        restoreBtn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                remoteDialog.setContentView(R.layout.local_storage_dialog);
                remoteTextDes = remoteDialog.findViewById(R.id.remoteDes);
                remoteBtn = remoteDialog.findViewById(R.id.remoteBackupDtn);
                ipTextField = remoteDialog.findViewById(R.id.ipTextField);
                localCancel = remoteDialog.findViewById(R.id.localCancel);

                remoteTextDes.setText("Restore songs from remote location");
                remoteBtn.setText("Restore");
                remoteBtn.setBackground(getDrawable(R.drawable.warning_btn));

                backup = getSharedPreferences("backup", MODE_PRIVATE);
                final String ip = backup.getString("ip", "");
                ipTextField.setText(ip);

                localCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        remoteDialog.dismiss();
                    }
                });

                remoteBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        final String currentInputIp = ipTextField.getText().toString();
                        Toast.makeText(getApplicationContext(), "Restoring...", Toast.LENGTH_SHORT).show();
                        new Restore(getApplicationContext(), currentInputIp, 0, new Restore.AsyncResponse() {
                            @Override
                            public void processFinished(ArrayList<Song> cloudSongs) {
                                Log.e("Achuna", cloudSongs.toString());

                                if (cloudSongs.size() > 0) {
                                    database.clearMusic();
                                    songsInList.clear();
                                    for (int i = 0; i < cloudSongs.size(); i++) {
                                        database.addMusic(cloudSongs.get(i));
                                        songsInList.add(cloudSongs.get(i));
                                    }
                                    Collections.reverse(songsInList); //newest first
                                    adapter.notifyDataSetChanged();
                                    Toast.makeText(getApplicationContext(), "Songs Restored", Toast.LENGTH_SHORT).show();
                                    SharedPreferences.Editor editor = backup.edit();
                                    editor.putString("ip", currentInputIp);
                                    editor.commit();
                                    remoteDialog.dismiss();
                                    cloudDialog.dismiss();
                                } else {
                                    Toast.makeText(getApplicationContext(), "Error Restoring", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }).execute();



                    }
                });

                remoteDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                remoteDialog.show();
                return true;
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
        Collections.reverse(localSongs); //newest first
        return localSongs;
    }

    @Override
    protected void onRestart() {
        applyTheme();
        super.onRestart();
    }

    /**
     * Sets the data and time that the most recent backup was made only if the
     * backup was successful
     */
    public String setBackupTime() {
        SharedPreferences backup = getSharedPreferences("backup", MODE_PRIVATE);
        SharedPreferences.Editor editor = backup.edit();

        Calendar calendar = Calendar.getInstance();
        String currentDate = DateFormat.getDateInstance().format(calendar.getTime());

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm a");
        String time = simpleDateFormat.format(calendar.getTime());

        String date = currentDate + " at " + time;

        editor.putString("backupTime", date);
        editor.commit();

        return date;
    }

    @Override
    public void finish() {
        super.finish();
        //When back button is pressed override animation (Slide to the left)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }
}
