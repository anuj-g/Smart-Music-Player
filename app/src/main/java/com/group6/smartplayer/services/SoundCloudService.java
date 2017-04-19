package com.group6.smartplayer.services;

import com.google.gson.JsonElement;
import com.group6.smartplayer.adapters.Song;
import com.group6.smartplayer.utils.Config;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by GHANSHYAM on 15-Apr-17.
 */

public interface SoundCloudService
{
    @GET("/tracks?client_id=" + Config.SC_CLIENT_ID)
    Call<List<Song>> getSoundCloudTracks(@Query("q") String trackName);


}
