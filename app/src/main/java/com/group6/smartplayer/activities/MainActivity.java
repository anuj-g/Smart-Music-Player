package com.group6.smartplayer.activities;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import com.group6.smartplayer.R;
import com.group6.smartplayer.adapters.Song;
import com.group6.smartplayer.services.MusicService;
import com.group6.smartplayer.services.SoundCloudService;
import com.group6.smartplayer.services.SoundCloudServiceBuilder;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity
{
    MusicService musicService;

    boolean musicBound;
    ImageButton playButton;
    Intent playIntent;
    private boolean paused=false, playbackPaused=false;
    final ArrayList<Song> mSoundCloudTracks = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        playButton= (ImageButton) findViewById(R.id.btn_play);
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                togglePlayPause();
            }
        });

       // if(playIntent==null){
            Log.d("onstart","playIntent Started");
            playIntent = new Intent(this, MusicService.class);
            getApplicationContext(). bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
            startService(playIntent);
       // }
        SoundCloudService soundCloudService = SoundCloudServiceBuilder.getService();

        Call<List<Song>> call = soundCloudService.getSoundCloudTracks("293");
        call.enqueue(new Callback<List<Song>>() {
            @Override
            public void onResponse(Call<List<Song>> call, Response<List<Song>> response) {
                mSoundCloudTracks.add(response.body().get(0));
                songPicked(0,true);
            }

            @Override
            public void onFailure(Call<List<Song>> call, Throwable t) {

            }
        });

        /*soundCloudService.getSoundCloudTracks("293", new Callback<List<Song>>() {
            @Override
            public void onResponse(Call<List<Song>> call, Response<List<Song>> response)
            {
                mSoundCloudTracks.add(response.body().get(0));
                songPicked(0,true);
            }

            @Override
            public void onFailure(Call<List<Song>> call, Throwable t) {

            }
        });
*/

    }

    public void songPicked(int index,boolean isInit){
        musicService.setSong(index);
        musicService.playSong(isInit);
        togglePlayPause();
        if(playbackPaused){
//            setController();

            playbackPaused=false;
        }
//        controller.show(0);
    }
    private void togglePlayPause() {
        if(musicService.isPrepared()) {
            if (musicService.isPng()) {
                //if(isFirstTime) mood = "null";
                musicService.pausePlayer();
                //mPlayerControl.setImageResource(R.drawable.ic_play);
            } else {
                //if(isFirstTime) mood = mSoundCloudTracks.get(musicSrv.getSongPosn()).getMood();
                musicService.go();
                //mPlayerControl.setImageResource(R.drawable.ic_pause);
            }
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("onstart","In onStart");
        if(playIntent==null){
            Log.d("onstart","playIntent Started");
            playIntent = new Intent(this, MusicService.class);
            bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
            startService(playIntent);
        }
    }

    private ServiceConnection musicConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

            MusicService.MusicBinder binder = (MusicService.MusicBinder) service;
            //get service
            musicService = binder.getService();
            //pass list
            musicService.setList(mSoundCloudTracks);
            musicBound = true;
        }
        public void onServiceDisconnected(ComponentName name) {
            musicBound = false;
        }

    };

}
