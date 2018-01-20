package com.sam.summoner;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class ChampionLocalDatabaseHelper extends SQLiteOpenHelper {
    private final String TAG = "ChampionDatabaseHelper";

    public static final String DATABASE_NAME = "champions.db";
    public static final String TABLE_NAME = "champion_table";
    public static final String COL_1 = "id";
    public static final String COL_2 = "name";
    public static final String COL_3 = "image ";

    public ChampionLocalDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "Creating champion database table...");
        String query = "create table " + TABLE_NAME + " (" +
                COL_1 + " integer primary key" +
                COL_2 + " text" +
                COL_3 + " text" +
                ")";
        db.execSQL(query);
        Log.d(TAG, "Champion database table created.");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String query = "drop table if exists " + TABLE_NAME;
        db.execSQL(query);
    }

    public void add(int id, String name, String img) {
        Log.d(TAG, "Adding " + name + " to database...");
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_1, id);
        cv.put(COL_2, name);
        cv.put(COL_3, img);
        long result = db.insert(TABLE_NAME, null, cv);
        Log.d(TAG, name + " added to database: " + result);
    }

    public String getNameFromId(int id) {
        Log.d(TAG, "Getting champion name from ID " + id + ".");
        String str = "";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cur = db.rawQuery("select " + COL_2 + " from " + TABLE_NAME + " where " + COL_1 + " = " + id, null);
        str = cur.getString(0);
        Log.d(TAG, "Got: " + str);
        return str;
    }

    public String getImgFromId(int id) {
        Log.d(TAG, "Getting champion image name from ID " + id + ".");
        String str = "";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cur = db.rawQuery("select " + COL_3 + " from " + TABLE_NAME + " where " + COL_1 + " = " + id, null);
        str = cur.getString(0);
        return str;
    }
}