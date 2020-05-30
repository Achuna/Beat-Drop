package com.example.beatdrop;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

public class SongDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "Songs";
    private static final String TABLE_NAME = "songs_table";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_LINK = "link";
    private static final String COLUMN_MOOD = "mood";

    public SongDbHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //This create the database for the first time
        String query = "CREATE TABLE " + TABLE_NAME + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_LINK + " VARCHAR(255) NOT NULL," +
                COLUMN_MOOD + " VARCHAR(255) NOT NULL);";

        db.execSQL(query);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //Delete table and start from scratch
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + TABLE_NAME;
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }


    //////////DATABASE METHODS//////////////

    public void addMusic(Song song) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_LINK, song.getLink());
        values.put(COLUMN_MOOD, song.getMood());

        db.insert(TABLE_NAME, null, values);
        db.close();

    }

    public void deleteMusic(String link) {
        SQLiteDatabase db = getWritableDatabase();
        String query = "DELETE FROM " + TABLE_NAME + " WHERE " + COLUMN_LINK + "='" + link + "';";
        db.execSQL(query);
        Log.e("Achuna", "Song deleted");
    }

    //Only used on restore
    public void clearMusic() {
        SQLiteDatabase db = getWritableDatabase();
        String query = "DELETE FROM " + TABLE_NAME;
        db.execSQL(query);
        Log.e("Achuna", "Songs cleared");
    }


    public Cursor getData() {
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME;
        Cursor data = db.rawQuery(query, null);
        return data;
    }


}
