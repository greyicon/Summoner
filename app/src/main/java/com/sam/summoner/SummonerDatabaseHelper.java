package com.sam.summoner;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class SummonerDatabaseHelper extends SQLiteOpenHelper{
    public static final String DATABASE_NAME = Constants.SUMMONER_DB_NAME;
    public static final String FRIENDS_TABLE_NAME = Constants.FRIENDS_TABLE_NAME;
    public static final String FRIENDS_COL1 = "name";

    public SummonerDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "create table " + FRIENDS_TABLE_NAME + " (" +
                FRIENDS_COL1 + " integer primary key, " +
                ")";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        return;
    }

    public void addFriend(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(FRIENDS_COL1, name);
        db.insert(FRIENDS_TABLE_NAME, null, cv);
    }

    public ArrayList<String> getFriends() {
        ArrayList<String> friends = new ArrayList<String>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cur = db.rawQuery("select " + FRIENDS_COL1 + " from " + FRIENDS_TABLE_NAME, null);
        cur.moveToFirst();
        for (int i = 0; i < cur.getCount(); i++) {
            friends.add(cur.getString(i));
        }
        return friends;
    }
}
