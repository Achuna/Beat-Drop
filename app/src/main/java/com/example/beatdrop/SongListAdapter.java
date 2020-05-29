package com.example.beatdrop;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import java.util.ArrayList;

public class SongListAdapter extends ArrayAdapter<String> {

    Context context;
    ArrayList<Song> songs;
    boolean isDark;
    SongDbHelper database;

    public SongListAdapter(@NonNull Context context, ArrayList<Song> songs, boolean isDark) {
        super(context, R.layout.list_item);
        this.context = context;
        this.songs = songs;
        this.isDark = isDark;
        database = new SongDbHelper(context);
    }

    @Override
    public int getCount() {
        return songs.size();
    }

    static class ViewHolder {
        ImageView moodImage;
        TextView songLink;
        CardView listCard;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final ViewHolder viewHolder = new ViewHolder();
        //Inflate layout
        LayoutInflater inflater = LayoutInflater.from(getContext());
        convertView = inflater.inflate(R.layout.list_item, parent, false);

        //Connect UI elements
        viewHolder.moodImage = convertView.findViewById(R.id.moodImage);
        viewHolder.songLink = convertView.findViewById(R.id.listItemText);
        viewHolder.listCard = convertView.findViewById(R.id.cardItem);

        //Apply Global Theme and Uniqueness
        if (isDark) {
            viewHolder.songLink.setTextColor(Color.WHITE);
            viewHolder.songLink.setText(songs.get(position).getLink());
            viewHolder.listCard.setCardBackgroundColor(0xFF636262);
            String mood = songs.get(position).getMood();
            switch (mood) {
                case "happy": viewHolder.moodImage.setImageResource(R.drawable.happy); break;
                case "chill": viewHolder.moodImage.setImageResource(R.drawable.chill); break;
                case "sad": viewHolder.moodImage.setImageResource(R.drawable.sad); break;
                case "angry": viewHolder.moodImage.setImageResource(R.drawable.angry); break;
                case "romantic": viewHolder.moodImage.setImageResource(R.drawable.romantic); break;
                case "funny": viewHolder.moodImage.setImageResource(R.drawable.funny); break;
                default: viewHolder.moodImage.setImageResource(R.drawable.happy);
            }
        } else {
            viewHolder.songLink.setTextColor(Color.BLACK);
            viewHolder.songLink.setText(songs.get(position).getLink());
            viewHolder.listCard.setCardBackgroundColor(Color.WHITE);
            String mood = songs.get(position).getMood();
            switch (mood) {
                case "happy": viewHolder.moodImage.setImageResource(R.drawable.happy); break;
                case "chill": viewHolder.moodImage.setImageResource(R.drawable.chill); break;
                case "sad": viewHolder.moodImage.setImageResource(R.drawable.sad); break;
                case "angry": viewHolder.moodImage.setImageResource(R.drawable.angry); break;
                case "romantic": viewHolder.moodImage.setImageResource(R.drawable.romantic); break;
                case "funny": viewHolder.moodImage.setImageResource(R.drawable.funny); break;
                default: viewHolder.moodImage.setImageResource(R.drawable.happy);
            }
        }

        return convertView;

    }
}
