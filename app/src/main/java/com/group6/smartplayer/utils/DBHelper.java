package com.group6.smartplayer.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by GHANSHYAM on 04-Mar-17.
 */

public class DBHelper extends SQLiteOpenHelper
{

    public static final String COLUMN_TIMESTAMP = "timestamp";
    public static final String ID = "ID";
    public static  final  String MOOD = "MOOD";
    public static final String SONGNAME = "SONGNAME";
    public static final String ARTISTNAME = "ARTISTNAME";

     static final String DATABASE_NAME = "group6.db";
     static final int DATABASE_VERSION = 1;

    // Database creation sql statement


    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //db.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public  void createTable(String tableName)
    {
         final String TABLE_CREATE = "create table if not exists "
                + tableName + "( " + COLUMN_TIMESTAMP
                + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " + ID
                + " int not null, "+MOOD+" int not null,"
                + SONGNAME + " text," + ARTISTNAME +" text);";

        Log.d("table","table created"+TABLE_CREATE);

        getWritableDatabase().execSQL(TABLE_CREATE);

    }
}
