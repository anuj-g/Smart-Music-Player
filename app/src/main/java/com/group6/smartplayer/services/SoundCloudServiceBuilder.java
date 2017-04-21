package com.group6.smartplayer.services;

import com.group6.smartplayer.utils.Config;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by GHANSHYAM on 15-Apr-17.
 */

public class SoundCloudServiceBuilder
{
    private static final Retrofit RETROFIT= new Retrofit
            .Builder()
            .baseUrl(Config.SC_API_URL)

            .addConverterFactory(GsonConverterFactory.create())
            .build();
    private static final SoundCloudService SERVICE = RETROFIT.create(SoundCloudService.class);
    public static SoundCloudService getService() {
        return SERVICE;
    }
}
