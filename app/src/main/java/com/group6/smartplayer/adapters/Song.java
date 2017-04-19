package com.group6.smartplayer.adapters;

import com.google.gson.annotations.SerializedName;

/**
 * Created by GHANSHYAM on 15-Apr-17.
 */

public class Song
{
    @SerializedName("title")
    private String mTitle;

    @SerializedName("id")
    private int mID;

    @SerializedName("stream_url")
    private String mStreamURL;

    @SerializedName("artwork_url")
    private String mArtworkURL;


    private String mood;

    public String getTitle() {
        return mTitle;
    }

    public int getID() {
        return mID;
    }

    public String getStreamURL() {
        return mStreamURL;
    }

    public String getArtworkURL() {
        return mArtworkURL;
    }

    public void setMood(String mood) {
        this.mood = mood;
    }

    public String getMood() {
        return mood;
    }

    public void setmTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public void setmID(int mID) {
        this.mID = mID;
    }

    public void setmStreamURL(String mStreamURL) {
        this.mStreamURL = mStreamURL;
    }

    public void setmArtworkURL(String mArtworkURL) {
        this.mArtworkURL = mArtworkURL;
    }
}
