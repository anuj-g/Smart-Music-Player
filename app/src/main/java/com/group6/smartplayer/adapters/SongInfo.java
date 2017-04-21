package com.group6.smartplayer.adapters;

/**
 * Created by Anuj Gandhi on 4/21/2017.
 */

public class SongInfo {

    public int id;
    public String songName;
    public String artistName;
    public String mood;

    public SongInfo(int id, String songName, String artistName, String mood) {
        this.id = id;
        this.songName = songName;
        this.artistName = artistName;
        this.mood = mood;
    }

    public int getId() {
        return id;
    }

    public String getSongName() {
        return songName;
    }

    public String getArtistName() {
        return artistName;
    }

    public String getMood() {
        return mood;
    }
}
