package com.group6.smartplayer.services;

import com.google.gson.JsonElement;
import com.group6.smartplayer.adapters.Song;
import com.group6.smartplayer.utils.Config;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;

/**
 * Created by GHANSHYAM on 15-Apr-17.
 */

public interface SoundCloudService
{

    /*@GET("/tracks?client_id=" + Config.SC_CLIENT_ID)
    Call<Song> getSoundCloudTracks(@Query("q") String trackName);*/

    @GET("/tracks/{q}/?client_id="+Config.SC_CLIENT_ID)
    Call<Song> getSoundCloudTracks(@Path("q") String q);

}
