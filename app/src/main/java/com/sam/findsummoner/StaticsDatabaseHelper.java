package com.sam.findsummoner;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

public class StaticsDatabaseHelper extends SQLiteOpenHelper {
    private final String TAG = "StaticsDatabaseHelper";

    public static final String DATABASE_NAME = Constants.STATIC_DB_NAME;
    public static final String CHAMP_TABLE_NAME = Constants.CHAMP_TABLE_NAME;
    public static final String CHAMP_COL1 = "id";
    public static final String CHAMP_COL2 = "name";
    public static final String CHAMP_COL3 = "image";
    public static final String ITEM_TABLE_NAME = Constants.ITEM_TABLE_NAME;
    public static final String ITEM_COL1 = "id";
    public static final String ITEM_COL2 = "name";
    public static final String ITEM_COL3 = "image";
    public static final String SS_TABLE_NAME = Constants.SS_TABLE_NAME;
    public static final String SS_COL1 = "id";
    public static final String SS_COL2 = "name";
    public static final String SS_COL3 = "image";
    public static final String FL_TABLE_NAME  = Constants.FRIENDS_TABLE_NAME;
    public static final String FL_COL1 = "name";
    public static final String DD_TABLE_NAME  = Constants.DD_TABLE_NAME;
    public static final String DD_COL1 = "version";

    public StaticsDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "Creating champion database table...");
        String query = "create table " + CHAMP_TABLE_NAME + " (" +
                CHAMP_COL1 + " integer primary key, " +
                CHAMP_COL2 + " text, " +
                CHAMP_COL3 + " text" +
                ")";
        db.execSQL(query);
        Log.d(TAG, "Champion database table created.");

        Log.d(TAG, "Creating item database table...");
        query = "create table " + ITEM_TABLE_NAME + " (" +
                ITEM_COL1 + " integer primary key, " +
                ITEM_COL2 + " text, " +
                ITEM_COL3 + " text" +
                ")";
        db.execSQL(query);
        Log.d(TAG, "Item database table created.");

        Log.d(TAG, "Creating summoner spell database table...");
        query = "create table " + SS_TABLE_NAME + " (" +
                SS_COL1 + " text primary key, " +
                SS_COL2 + " text, " +
                SS_COL3 + " text" +
                ")";
        db.execSQL(query);
        Log.d(TAG, "Summoner spell database table created.");

        Log.d(TAG, "Creating friend database table...");
        query = "create table " + FL_TABLE_NAME + " (" +
                "id integer primary key autoincrement, " +
                FL_COL1 + " text" +
                ")";
        db.execSQL(query);
        Log.d(TAG, "Friend database table created.");

        Log.d(TAG, "Creating dd database table...");
        query = "create table " + DD_TABLE_NAME + " (" +
                "id integer primary key autoincrement, " +
                DD_COL1 + " text" +
                ")";
        db.execSQL(query);
        Log.d(TAG, "dd database table created.");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        return;
    }

    public void addChampion(int id, String name, String img) {
        Log.d(TAG, "Adding " + name + " to champion table...");
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(CHAMP_COL1, id);
        cv.put(CHAMP_COL2, name);
        cv.put(CHAMP_COL3, img);
        long result = db.insert(CHAMP_TABLE_NAME, null, cv);
        Log.d(TAG, name + " added to champion table: " + result);
    }

    public void addItem(int id, String name, String img) {
        Log.d(TAG, "Adding " + name + " to item table...");
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(ITEM_COL1, id);
        cv.put(ITEM_COL2, name);
        cv.put(ITEM_COL3, img);
        long result = db.insert(ITEM_TABLE_NAME, null, cv);
        Log.d(TAG, name + " added to item table: " + result);
    }

    public void addSpell(int id, String name, String img) {
        Log.d(TAG, "Adding " + name + " to summoner spell table...");
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(SS_COL1, id);
        cv.put(SS_COL2, name);
        cv.put(SS_COL3, img);
        long result = db.insert(SS_TABLE_NAME, null, cv);
        Log.d(TAG, name + " added to summoner spell table: " + result);
    }

    public String getChampionNameFromId(int id) {
        Log.d(TAG, "Getting champion name from ID " + id + ".");
        String str = "";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cur = db.rawQuery("select " + CHAMP_COL2 + " from " + CHAMP_TABLE_NAME + " where " + CHAMP_COL1 + " = " + id, null);
        cur.moveToFirst();
        str = cur.getString(0);
        cur.close();
        return str;
    }

    public String getItemNameFromId(int id) {
        Log.d(TAG, "Getting item name from ID " + id + ".");
        String str = "";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cur = db.rawQuery("select " + ITEM_COL2 + " from " + ITEM_TABLE_NAME + " where " + ITEM_COL1 + " = " + id, null);
        cur.moveToFirst();
        str = cur.getString(0);
        cur.close();
        return str;
    }

    public String getSpellNameFromId(int id) {
        Log.d(TAG, "Getting summoner spell name from ID " + id + ".");
        String str = "";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cur = db.rawQuery("select " + SS_COL2 + " from " + SS_TABLE_NAME + " where " + SS_COL1 + " = " + id, null);
        cur.moveToFirst();
        str = cur.getString(0);
        cur.close();
        return str;
    }

    public String getChampionImgFromId(int id) {
        Log.d(TAG, "Getting champion image name from ID " + id + ".");
        String str = "";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cur = db.rawQuery("select " + CHAMP_COL3 + " from " + CHAMP_TABLE_NAME + " where " + CHAMP_COL1 + " = " + id, null);
        cur.moveToFirst();
        if (cur.getCount() == 0) {return Constants.UNKNOWN_IMAGE;}
        str = cur.getString(0);
        cur.close();
        return str;
    }

    public String getItemImgFromId(int id) {
        Log.d(TAG, "Getting item image name from ID " + id + ".");
        String str = "";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cur = db.rawQuery("select " + ITEM_COL3 + " from " + ITEM_TABLE_NAME + " where " + ITEM_COL1 + " = " + id, null);
        cur.moveToFirst();
        if (cur.getCount() == 0) {return Constants.UNKNOWN_IMAGE;}
        str = cur.getString(0);
        cur.close();
        return str;
    }

    public String getSpellImgFromId(int id) {
        Log.d(TAG, "Getting summoner spell image name from ID " + id + ".");
        String str = "";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cur = db.rawQuery("select " + SS_COL3 + " from " + SS_TABLE_NAME + " where " + SS_COL1 + " = " + id, null);
        cur.moveToFirst();
        if (cur.getCount() == 0) {return Constants.UNKNOWN_IMAGE;}
        str = cur.getString(0);
        cur.close();
        return str;
    }

    public int getNumChampTableEntries() {
        Log.d(TAG, "Checking number of champion table entries...");
        SQLiteDatabase db = this.getWritableDatabase();
        String qry = "select count(*) from " + CHAMP_TABLE_NAME;
        Cursor cur = db.rawQuery(qry, null);
        cur.moveToFirst();
        int count = cur.getInt(0);
        cur.close();
        return count;
    }

    public int getNumItemTableEntries() {
        Log.d(TAG, "Checking number of item table entries...");
        SQLiteDatabase db = this.getWritableDatabase();
        String qry = "select count(*) from " + ITEM_TABLE_NAME;
        Cursor cur = db.rawQuery(qry, null);
        cur.moveToFirst();
        int count = cur.getInt(0);
        cur.close();
        return count;
    }

    public int getNumSpellTableEntries() {
        Log.d(TAG, "Checking number of summoner spell table entries...");
        SQLiteDatabase db = this.getWritableDatabase();
        String qry = "select count(*) from " + SS_TABLE_NAME;
        Cursor cur = db.rawQuery(qry, null);
        cur.moveToFirst();
        int count = cur.getInt(0);
        cur.close();
        return count;
    }

    public void clearChampTable() {
        Log.d(TAG, "Clearing " + CHAMP_TABLE_NAME);
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "delete from " + CHAMP_TABLE_NAME;
        db.execSQL(query);
    }

    public void clearItemTable() {
        Log.d(TAG, "Clearing " + ITEM_TABLE_NAME);
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "delete from " + ITEM_TABLE_NAME;
        db.execSQL(query);
    }

    public void clearSpellTable() {
        Log.d(TAG, "Clearing " + SS_TABLE_NAME);
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "delete from " + SS_TABLE_NAME;
        db.execSQL(query);
    }

    public void addFriend(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(FL_COL1, name);
        db.insert(FL_TABLE_NAME, null, cv);
    }

    public void removeFriend(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "delete from " + FL_TABLE_NAME + " where " + FL_COL1 + " = '" + name + "'";
        db.execSQL(query);
    }

    public ArrayList<String> getFriends() {
        ArrayList<String> friends = new ArrayList<String>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cur = db.rawQuery("select " + FL_COL1 + " from " + FL_TABLE_NAME, null);
        cur.moveToFirst();
        while (!cur.isAfterLast()) {
            friends.add(cur.getString(0));
            cur.moveToNext();
        }
        cur.close();
        return friends;
    }

    public int getNumDDTableEntries() {
        Log.d(TAG, "Checking number of dd table entries...");
        SQLiteDatabase db = this.getWritableDatabase();
        String qry = "select count(*) from " + DD_TABLE_NAME;
        Cursor cur = db.rawQuery(qry, null);
        cur.moveToFirst();
        int count = cur.getInt(0);
        cur.close();
        return count;
    }

    public void addDDVersion(String version) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(DD_COL1, version);
        db.insert(DD_TABLE_NAME, null, cv);
    }

    public void clearDDVersion() {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "delete from " + DD_TABLE_NAME;
        db.execSQL(query);
    }

    public String getDDVersion() {
        String ret = "";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cur = db.rawQuery("select " + DD_COL1 + " from " + DD_TABLE_NAME, null);
        cur.moveToFirst();
        ret = cur.getString(0);
        cur.close();
        return ret;
    }


}
