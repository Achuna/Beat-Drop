package com.example.beatdrop;

public class Song {
    String link;
    String mood;

    public Song(String link, String mood) {
        this.link = link;
        this.mood = mood;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getMood() {
        return mood;
    }

    public void setMood(String mood) {
        this.mood = mood;
    }

}
