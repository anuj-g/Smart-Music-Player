package com.group6.smartplayer.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import com.group6.smartplayer.adapters.SongInfo;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by GHANSHYAM on 04-Mar-17.
 */

public class DataSource
{
    static  SQLiteDatabase db;
    DBHelper dbHelper;
    Context context;
    static String DB_ABSOLUTE_PATH;
    private String[] allColumns = { DBHelper.COLUMN_TIMESTAMP,DBHelper.ID,DBHelper.MOOD};

    public DataSource(Context context)
    {
        this.context=context;
        dbHelper=new DBHelper(context);
    }

    public void open() throws SQLException {
        db = dbHelper.getWritableDatabase();
        DB_ABSOLUTE_PATH=db.getPath();
    }

    public void close() {
        dbHelper.close();
    }

    public static void insert(String tableName, int id, String songName, String artistName,String mood) {
        if (tableName != null) {
            db = SQLiteDatabase.openDatabase(DataSource.DB_ABSOLUTE_PATH, null, SQLiteDatabase.CREATE_IF_NECESSARY);
            ContentValues values = new ContentValues();
            values.put(DBHelper.ID, id);
            values.put(DBHelper.MOOD, mood);
            values.put(DBHelper.SONGNAME, songName);
            values.put(DBHelper.ARTISTNAME, artistName);

            if (db != null)
                db.insert(tableName, null, values);
            Log.d("db path insert", db.getPath());
            Log.d("table name insert", tableName);
        }
    }

    public HashMap<Integer, SongInfo> getSongs(String tableName,String artistName) {
        HashMap<Integer, SongInfo> playlist = new HashMap<Integer, SongInfo>();
        int i = 0;
        if (tableName!=null ) {
            try {
                Cursor cursor = db.query(tableName, new String[]{DBHelper.ID,DBHelper.SONGNAME,DBHelper.ARTISTNAME,DBHelper.MOOD}, DBHelper.ARTISTNAME+"=?", new String[]{artistName}, null, null, DBHelper.COLUMN_TIMESTAMP + " DESC", "10");
                //Log.d("cursor",cursor.moveToFirst()+"");


                while (cursor.moveToNext()) {
                    //Log.d("data",sensorData.getX()+"");
                    Log.d("Cursor", cursor.getInt(0)+"");
                    SongInfo songInfo = new SongInfo(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getString(3));
                    playlist.put(cursor.getInt(0), songInfo);
                    //cursor.moveToNext();
                }

                cursor.close();

                return playlist;
            }
            catch (Exception e)
            {
                e.printStackTrace();
                Toast.makeText(context, "Table not found", Toast.LENGTH_SHORT).show();
            }
        }

        return playlist;
    }

    public int[] getPlaylist(String tableName,String mood){
        int[] playlist = new int[10];
        int i = 0;
        if (tableName!=null ) {
            try {
                Cursor cursor = db.query(tableName, new String[]{DBHelper.ID}, DBHelper.MOOD+" = '"+ mood+ "'", null, null, null, DBHelper.COLUMN_TIMESTAMP + " DESC", "10");
                //Log.d("cursor",cursor.moveToFirst()+"");


                while (cursor.moveToNext()) {
                    //Log.d("data",sensorData.getX()+"");
                    Log.d("Cursor", cursor.getInt(0)+"");
                    playlist[i++] = cursor.getInt(0);
                    //cursor.moveToNext();
                }

                cursor.close();

                return playlist;
            }
            catch (Exception e)
            {
                e.printStackTrace();
                Toast.makeText(context, "Table not found", Toast.LENGTH_SHORT).show();
            }
        }

        return playlist;
    }
    public String getDatabasePath()
    {
        return context.getDatabasePath(DBHelper.DATABASE_NAME).getAbsolutePath();
    }

    public String getDataBaseName()
    {
        return DBHelper.DATABASE_NAME;
    }
    public void createtable(String tableName)
    {
        if(tableName!=null)
            dbHelper.createTable(tableName);
    }
}
