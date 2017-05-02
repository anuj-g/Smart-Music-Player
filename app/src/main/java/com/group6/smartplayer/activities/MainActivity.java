package com.group6.smartplayer.activities;

import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.graphics.Color;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.provider.BaseColumns;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;

import android.support.v4.widget.CursorAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.group6.smartplayer.R;
import com.group6.smartplayer.adapters.Song;
import com.group6.smartplayer.adapters.SongInfo;
import com.group6.smartplayer.services.MusicService;
import com.group6.smartplayer.services.SoundCloudService;
import com.group6.smartplayer.services.SoundCloudServiceBuilder;
import com.group6.smartplayer.utils.DataSource;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.*;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.view.Gravity.CENTER;
import com.squareup.seismic.ShakeDetector;

import static android.view.Gravity.CENTER;
import static android.widget.ListPopupWindow.MATCH_PARENT;

import static com.group6.smartplayer.utils.SongIDs.ANGER_SONGS_ID;
import static com.group6.smartplayer.utils.SongIDs.HAPPY_SONGS_ID;
import static com.group6.smartplayer.utils.SongIDs.SADNESS_SONGS_ID;

public class MainActivity extends AppCompatActivity implements ShakeDetector.Listener
{

    HashMap<Integer,String> SADNESS_SONGS_ID=new HashMap<Integer,String>();
    HashMap<Integer,String> HAPPY_SONGS_ID=new HashMap<Integer,String>();
    HashMap<Integer,String> ANGER_SONGS_ID=new HashMap<Integer,String>();

    HashMap<Integer,String> SADNESS_ARTIST_ID=new HashMap<Integer,String>();
    HashMap<Integer,String> HAPPY_ARTISTS_ID=new HashMap<Integer,String>();
    HashMap<Integer,String> ANGER_ARTISTS_ID=new HashMap<Integer,String>();

    private BroadcastReceiver statusReceiver;
    private IntentFilter mIntent;

    static final String TABLE_NAME= "Songs";
    SearchView searchView;
    ArrayList<String> songsList;
    ArrayAdapter<String> arrayAdapter;
    public static ProgressBar progressBar;
    ListView listView;
    int[] songIds;
    MusicService musicService;
    String mood;
    boolean musicBound;
    ImageButton playButton,nextButton,prevButton;
    ImageView artWorkImageView;
    TextView trackTitleTextView;
    Intent playIntent;
    private boolean paused=false, playbackPaused=false;
    final ArrayList<Song> mSoundCloudTracks = new ArrayList<>();

    DataSource dataSource;
    private boolean isLikeButtonPressed=false;
    private ImageButton buttonLike;
    private ImageButton buttonDislike;
    private boolean isDisLikePressed=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //progressBar= (ProgressBar) findViewById(R.id.progressBar2);
        buttonLike= (ImageButton) findViewById(R.id.btn_like);
        buttonLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleLikeButtons(true);
            }
        });
        buttonDislike= (ImageButton) findViewById(R.id.btn_dislike);
        buttonDislike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleLikeButtons(false);
            }
        });
        dataSource= new DataSource(getApplicationContext());
        dataSource.createtable("Songs");
        dataSource.open();



        /*TextView tv = new TextView(this);
        tv.setGravity(CENTER);
        tv.setText("Shake me, bro!");
        setContentView(tv, new ActionBar.LayoutParams(MATCH_PARENT, MATCH_PARENT));*/


        ANGER_SONGS_ID.put(50623988,"What I've done");
        ANGER_ARTISTS_ID.put(50623988,"Linkin Park");
        ANGER_SONGS_ID.put(68994095,"One step closer");
        ANGER_ARTISTS_ID.put(68994095,"Linkin Park");
        ANGER_SONGS_ID.put(184048237,"Numb");
        ANGER_ARTISTS_ID.put(184048237,"Linkin Park");

        HAPPY_SONGS_ID.put(96441813,"Radioactive");
        HAPPY_ARTISTS_ID.put(96441813,"Imagine Dragons");
        HAPPY_SONGS_ID.put(167375184,"It's time");
        HAPPY_ARTISTS_ID.put(167375184,"Imagine Dragons");
        HAPPY_SONGS_ID.put(185788656,"I bet my life");
        HAPPY_ARTISTS_ID.put(185788656,"Imagine Dragons");

        HAPPY_SONGS_ID.put(181778404,"Waka waka");
        HAPPY_ARTISTS_ID.put(181778404,"Shakira");
        HAPPY_SONGS_ID.put(262869782,"Try Everything");
        HAPPY_ARTISTS_ID.put(262869782,"Shakira");
        HAPPY_SONGS_ID.put(276738580,"La Bicicleta");
        HAPPY_ARTISTS_ID.put(276738580,"Shakira");

        SADNESS_SONGS_ID.put(146437548,"Somebody's me");
        SADNESS_ARTIST_ID.put(146437548,"Enrique");
        SADNESS_SONGS_ID.put(238349082,"Same Mistake");
        SADNESS_ARTIST_ID.put(238349082,"James Blunt");
        SADNESS_SONGS_ID.put(99450460,"With or without me");
        SADNESS_ARTIST_ID.put(99450460,"U2");

        Set<Integer> sadnessSongsKeySet = SADNESS_SONGS_ID.keySet();

        for (Integer key : sadnessSongsKeySet) {
            dataSource.insert("Songs", key, SADNESS_SONGS_ID.get(key), SADNESS_ARTIST_ID.get(key),"sad");
        }

        Set<Integer> happySongsKeySet = HAPPY_SONGS_ID.keySet();
        for (Integer key : happySongsKeySet) {
            dataSource.insert("Songs", key, HAPPY_SONGS_ID.get(key), HAPPY_ARTISTS_ID.get(key),"happy");
        }


        Set<Integer> angrySongsKeySet = ANGER_SONGS_ID.keySet();
        for (Integer key : angrySongsKeySet) {
            dataSource.insert("Songs", key, ANGER_SONGS_ID.get(key), ANGER_ARTISTS_ID.get(key),"angry");
        }
        mood=getIntent().getStringExtra("mood");
        Log.d("MainActivity","mood "+mood);
        songIds = dataSource.getPlaylist(TABLE_NAME,mood);
        for(Integer i:songIds) {
            getSongsFromSoundCloud(String.valueOf(i), false);

        }

        artWorkImageView= (ImageView) findViewById(R.id.imageViewArtWork);
        trackTitleTextView= (TextView) findViewById(R.id.textViewTrackTitle);
        playButton= (ImageButton) findViewById(R.id.btn_play);
        nextButton= (ImageButton) findViewById(R.id.btn_next);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                musicService.playNext();
                int position=musicService.getSongPosition();
                Picasso.with(getApplicationContext())
                        .load(mSoundCloudTracks.get(position).getArtworkURL())
                        .placeholder(R.drawable.ic_notification_default_black)
                        .error(R.drawable.ic_notification_default_black)
                        .into(artWorkImageView);
                trackTitleTextView.setText(mSoundCloudTracks.get(position).getTitle());
            }
        });
        prevButton = (ImageButton) findViewById(R.id.btn_prev);
        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                musicService.playPrev();
                int position=musicService.getSongPosition();
                Picasso.with(getApplicationContext())
                        .load(mSoundCloudTracks.get(position).getArtworkURL())
                        .placeholder(R.drawable.ic_notification_default_black)
                        .error(R.drawable.ic_notification_default_black)
                        .into(artWorkImageView);
                trackTitleTextView.setText(mSoundCloudTracks.get(position).getTitle());
            }
        });

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

        SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        ShakeDetector sd = new ShakeDetector(this);
        sd.start(sensorManager);

    }

    public void songPicked(int index,boolean isInit){
        musicService.setSong(index);
        musicService.playSong(isInit);
       // togglePlayPause();
        if(playbackPaused){
//            setController();

            playbackPaused=false;
        }
//        controller.show(0);
        if(musicService.isPrepared()) {

        }


    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(MainActivity.this).registerReceiver(broadcastReceiver, new IntentFilter("NOW"));
    }
    @Override
    protected void onPause() {
        if(mIntent != null) {
            unregisterReceiver(statusReceiver);
            mIntent = null;
        }
        super.onPause();
    }
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String type = intent.getStringExtra("message");  //get the type of message from MyGcmListenerService 1 - lock or 0 -Unlock
            //sm = (SensorManager) getSystemService(SENSOR_SERVICE);
            //accelerometer = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            //Log.d(TAG, "Status: " + type);
            Log.d("broadcast","received");
           // if(type!=null)
            {
                Picasso.with(getApplicationContext())
                        .load(musicService.getCurrentSong().getArtworkURL())
                        .placeholder(R.drawable.ic_notification_default_black)
                        .error(R.drawable.ic_notification_default_black)
                        .into(artWorkImageView);
                trackTitleTextView.setText(musicService.getCurrentSong().getTitle());
                Log.d("Mainactivity", "title" + musicService.getCurrentSong().getTitle());
                if (musicService.isPng()) {
                    //if(isFirstTime) mood = "null";

                   // musicService.pausePlayer();
                    //playbackPaused = true;
                    playButton.setBackgroundResource(R.drawable.ic_pause_black);
                    //mPlayerControl.setImageResource(R.drawable.ic_play);
                } else {
                    //if(isFirstTime) mood = mSoundCloudTracks.get(musicSrv.getSongPosn()).getMood();
                    //musicService.go();
                    playButton.setBackgroundResource(R.drawable.ic_play_black);
                   // playbackPaused=false;
                    //mPlayerControl.setImageResource(R.drawable.ic_pause);
                }
            }
        }
    };

    private void toggleLikeButtons(boolean button) {
        if (button) {
            if (isLikeButtonPressed) {
                buttonLike.setImageResource(R.drawable.ic_action_like_white);
                isLikeButtonPressed = false;
            } else {
                buttonLike.setImageResource(R.drawable.ic_action_like_black);
                buttonDislike.setImageResource(R.drawable.ic_action_dontlike_white);
                isDisLikePressed = false;
                isLikeButtonPressed = true;
            }


        } else {

            if (isDisLikePressed) {

                buttonDislike.setImageResource(R.drawable.ic_action_dontlike_white);
                isDisLikePressed = false;
            } else {
                buttonDislike.setImageResource(R.drawable.ic_action_dontlike_black);

                buttonLike.setImageResource(R.drawable.ic_action_like_white);
                isDisLikePressed = true;
                isLikeButtonPressed = false;
            }
        }
    }
    private void togglePlayPause() {
        //if(musicService.isPrepared())
        {
            if (musicService.isPng()) {
                //if(isFirstTime) mood = "null";

                musicService.pausePlayer();
                playbackPaused = true;
                playButton.setBackgroundResource(R.drawable.ic_play_black);
                //mPlayerControl.setImageResource(R.drawable.ic_play);
            } else {
                //if(isFirstTime) mood = mSoundCloudTracks.get(musicSrv.getSongPosn()).getMood();
                musicService.go();
                playButton.setBackgroundResource(R.drawable.ic_pause_black);
                playbackPaused=false;
                //mPlayerControl.setImageResource(R.drawable.ic_pause);
            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        String[] artistNames=getIntent().getStringArrayExtra(CameraActivity.ARTIST_NAMES);

        Log.d("artistNames",artistNames.length+"");
        final ArrayList<SongInfo> suggestions=new ArrayList<>();
        for(String str:artistNames)
        {
            HashMap<Integer,SongInfo> map=dataSource.getSongs(TABLE_NAME,str);
            Log.d("MainActivity","map size "+map.size());
            for(Integer i:map.keySet())
                suggestions.add(map.get(i));
        }
        Log.d("MainActivity","list size "+suggestions.size());
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_bar_menu, menu);

        SearchManager searchManager = (SearchManager)
                getSystemService(Context.SEARCH_SERVICE);
        MenuItem searchMenuItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) searchMenuItem.getActionView();

        searchView.setSearchableInfo(searchManager.
                getSearchableInfo(getComponentName()));
        searchView.setSubmitButtonEnabled(true);
        //searchView.setOnQueryTextListener(this);
        /*final List<String> suggestions = new ArrayList<>();
        suggestions.add("song1");
        suggestions.add("song2");
        suggestions.add("song3");*/

        android.support.v4.widget.SimpleCursorAdapter.ViewBinder binder = new android.support.v4.widget.SimpleCursorAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
                if(view.getId()==android.R.id.text1) {
                    view.setBackgroundColor(Color.WHITE);
                    return false;
                }
                return false;
            }
        };
        final android.support.v4.widget.SimpleCursorAdapter suggestionAdapter =new android.support.v4.widget.SimpleCursorAdapter(this,android.R.layout.simple_list_item_1,null,new String[]{SearchManager.SUGGEST_COLUMN_TEXT_1},
                new int[]{android.R.id.text1},
                0);
        suggestionAdapter.setViewBinder(binder);
        searchView.setSuggestionsAdapter(suggestionAdapter);
        searchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
            @Override
            public boolean onSuggestionSelect(int position) {
                return false;
            }

            @Override
            public boolean onSuggestionClick(int position) {
                searchView.setQuery(suggestions.get(position).getSongName(), true);
                getSongsFromSoundCloud(String.valueOf(suggestions.get(position).getId()),true);
                //searchView.clearFocus();
                return true;

            }
        });
       /* searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });*/

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false; //false if you want implicit call to searchable activity
                // or true if you want to handle submit yourself
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Hit the network and take all the suggestions and store them in List 'suggestions'

                String[] columns = { BaseColumns._ID,
                        SearchManager.SUGGEST_COLUMN_TEXT_1,
                        SearchManager.SUGGEST_COLUMN_INTENT_DATA,
                };
                MatrixCursor cursor = new MatrixCursor(columns);
                for (int i = 0; i < suggestions.size(); i++) {
                    String[] tmp = {Integer.toString(i),suggestions.get(i).getSongName()+"\n"+suggestions.get(i).getArtistName(),suggestions.get(i).getSongName()+"\n"+suggestions.get(i).getArtistName()};
                    cursor.addRow(tmp);
                }
                suggestionAdapter.swapCursor(cursor);
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
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
            musicService.setContext(getApplicationContext());
            musicBound = true;
        }
        public void onServiceDisconnected(ComponentName name) {
            musicBound = false;
        }

    };




/*
    @Override
    public void onPrepared(MediaPlayer mediaPlayer)
    {
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
    }*/

    private void  getSongsFromSoundCloud(final String songID, final boolean playCurrent)
    {
        SoundCloudService soundCloudService = SoundCloudServiceBuilder.getService();

        Call<Song> call = soundCloudService.getSoundCloudTracks(songID);
        Log.d("MainActvity","songid "+songID);

        call.enqueue(new Callback<Song>() {
            @Override
            public void onResponse(Call<Song> call, Response<Song> response) {
                //Log.d("Mainacitivity",response.body().getStreamURL());
                //mSoundCloudTracks.clear();
                if(playCurrent) {
                    mSoundCloudTracks.add(0, response.body());
                    /*Picasso.with(getApplicationContext())
                            .load(mSoundCloudTracks.get(0).getArtworkURL())
                            .placeholder(R.drawable.ic_notification_default_black)
                            .error(R.drawable.ic_notification_default_black)
                            .into(artWorkImageView);
                    trackTitleTextView.setText(mSoundCloudTracks.get(0).getTitle());
                    Log.d("Mainactivity", "title" + mSoundCloudTracks.get(0).getTitle());*/
                    songPicked(0, true);
                }
                else
                    mSoundCloudTracks.add(response.body());
                if(musicService.isPrepared()) {
                    Picasso.with(getApplicationContext())
                            .load(musicService.getCurrentSong().getArtworkURL())
                            .placeholder(R.drawable.ic_notification_default_black)
                            .error(R.drawable.ic_notification_default_black)
                            .into(artWorkImageView);
                    trackTitleTextView.setText(musicService.getCurrentSong().getTitle());
                    Log.d("Mainactivity", "title" + musicService.getCurrentSong().getTitle());
                }
                if(mSoundCloudTracks.size() == songIds.length) {
                    /*Picasso.with(getApplicationContext())
                            .load(mSoundCloudTracks.get(0).getArtworkURL())
                            .placeholder(R.drawable.ic_notification_default_black)
                            .error(R.drawable.ic_notification_default_black)
                            .into(artWorkImageView);
                    trackTitleTextView.setText(mSoundCloudTracks.get(0).getTitle());
                    Log.d("Mainactivity", "title" + mSoundCloudTracks.get(0).getTitle());*/
                    songPicked(0, true);
                }
            }

            @Override
            public void onFailure(Call<Song> call, Throwable t) {

            }
        });
    }

    @Override
    public void hearShake()
    {
        musicService.playNext();
        int position=musicService.getSongPosition();
        Picasso.with(getApplicationContext())
                .load(mSoundCloudTracks.get(position).getArtworkURL())
                .placeholder(R.drawable.ic_notification_default_black)
                .error(R.drawable.ic_notification_default_black)
                .into(artWorkImageView);
        trackTitleTextView.setText(mSoundCloudTracks.get(position).getTitle());
    }


}
