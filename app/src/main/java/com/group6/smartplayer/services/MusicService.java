package com.group6.smartplayer.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;

import com.group6.smartplayer.R;
import com.group6.smartplayer.activities.MainActivity;
import com.group6.smartplayer.adapters.Song;
import com.group6.smartplayer.utils.Config;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Random;



public class MusicService extends Service implements
        MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener,
        MediaPlayer.OnCompletionListener{
    //media player
    private MediaPlayer player;
    private boolean isPrepared = false;
    private final IBinder musicBind = new MusicBinder();
    //song list
    private ArrayList<Song> songs;
    //current position
    private int songPosn;
    private String songTitle="";
    private static final int NOTIFY_ID=1;
    private boolean shuffle=false;
    private Random rand;
    private boolean isInit;
    Song currentSong;
    Context context;
    public MusicService() {

    }

    public void setContext(Context c)
    {
        context=c;
    }
    public void initMusicPlayer(){
        //set player properties
        player.setWakeMode(getApplicationContext(),
                PowerManager.PARTIAL_WAKE_LOCK);
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        player.setOnPreparedListener(this);
        player.setOnCompletionListener(this);
        player.setOnErrorListener(this);
    }
    public void setList(ArrayList<Song> theSongs){
        songs=theSongs;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        rand=new Random();
        Log.d("Music Service","playIntent Started");
//initialize position
        songPosn=0;
//create player
        player = new MediaPlayer();
        initMusicPlayer();
    }

    public void setShuffle(){
        if(shuffle) shuffle=false;
        else shuffle=true;
    }
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return musicBind;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        if(player.getCurrentPosition()>0){
            mp.reset();
            playNext();
        }
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        mp.reset();
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        if(!isInit)
        mp.start();
        Log.d("MusicService","onprepared");
        Log.d("MusicService","title"+songTitle);
        //MainActivity.progressBar.setVisibility(View.GONE);
        if(!isPrepared)
            go();
        isPrepared = true;
        Intent in = new Intent();
        in.putExtra("TYPE","prepared");
        in.setAction("NOW");
//sendBroadcast(in);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(in);

//        Intent notIntent = new Intent(this, MainActivity.class);
//        notIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        PendingIntent pendInt = PendingIntent.getActivity(this, 0,
//                notIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//
//        Notification.Builder builder = new Notification.Builder(this);
//
//        builder.setContentIntent(pendInt)
//                .setSmallIcon(R.drawable.ic_play)
//                .setTicker(songTitle)
//                .setOngoing(true)
//                .setContentTitle("Playing").setContentText(songTitle);
//        Notification not = builder.build();
//
//        startForeground(NOTIFY_ID, not);

    }
    @Override
    public boolean onUnbind(Intent intent){
        player.stop();
        player.release();
        return false;
    }

    public int getSongPosn() {
        return songPosn;
    }
    public Song getCurrentSong(){return  currentSong;}
    public void setSong(int songIndex){
        songPosn=songIndex;
    }
    public void playSong(boolean isInit){
        //play a song
        player.reset();
        this.isInit = isInit;
        isPrepared = false;
        Song playSong = songs.get(songPosn);
        currentSong = playSong;
        songTitle=playSong.getTitle();
//get id
        long currSong = playSong.getID();
//set uri
//        Uri trackUri = ContentUris.withAppendedId(
//                android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
//                currSong);
        try{
            player.setDataSource(playSong.getStreamURL() + "?client_id=" + Config.SC_CLIENT_ID);
        }
        catch(Exception e){
            Log.e("MUSIC SERVICE", "Error setting data source", e);
        }
        player.prepareAsync();
    }

    @Override
    public void onDestroy() {
        stopForeground(true);
    }
    public void playPrev(){
        songPosn--;
        if(songPosn<0) songPosn=songs.size()-1;
        playSong(false);
    }

    public void playNext(){
        if(shuffle){
            int newSong = songPosn;
            while(newSong==songPosn){
                newSong=rand.nextInt(songs.size());
            }
            songPosn=newSong;
        }
        else{
            songPosn++;
            if(songPosn>=songs.size()) songPosn=0;
        }
        playSong(false);
    }

    public int getPosn(){
        return player.getCurrentPosition();
    }

    public int getSongPosition() {return  songPosn;}
    public int getDur(){
        return player.getDuration();
    }

    public boolean isPng(){
        return player.isPlaying();
    }

    public void pausePlayer(){
        player.pause();
    }

    public void seek(int posn){
        player.seekTo(posn);
    }

    public void go(){
        player.start();
    }

    public boolean isPrepared() {
        return isPrepared;
    }

    public class MusicBinder extends Binder {
        public MusicService getService() {
            return MusicService.this;
        }
    }


}
