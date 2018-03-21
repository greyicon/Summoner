package com.sam.findsummoner;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class CacheDatabaseHelper extends SQLiteOpenHelper{
    private static final String DATABASE_NAME = "cache.db";
    private static final String TABLE_NAME = "cache_table";
    private static final String COL_1 = "id";
    private static final String COL_2 = "value";

    private static final int MAX_CACHE_SIZE = 200;

    public CacheDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "create table " + TABLE_NAME + " (" +
                COL_1 + " text primary key, " +
                COL_2 + " text " +
                ")";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        return;
    }

    public boolean addData(String id, String value) {
        if (getCacheSize() >= MAX_CACHE_SIZE) {return false;}
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_1, id);
        cv.put(COL_2, value);
        long result = db.insert(TABLE_NAME, null, cv);
        return true;
    }

    public int getCacheSize() {
        SQLiteDatabase db = this.getWritableDatabase();
        String qry = "select count(*) from " + TABLE_NAME;
        Cursor cur = db.rawQuery(qry, null);
        cur.moveToFirst();
        int count = cur.getInt(0);
        cur.close();
        return count;
    }

    public String getData(String id) {
        String str = "";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cur = db.rawQuery("select " + COL_2 + " from " + TABLE_NAME + " where " + COL_1 + " = " + id, null);
        cur.moveToFirst();
        str = cur.getString(0);
        cur.close();
        return str;
    }

    public void clearCache() {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "delete from " + TABLE_NAME;
        db.execSQL(query);
    }

    public boolean containsId(String id) {
        boolean ret;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cur = db.rawQuery("select " + COL_2 + " from " + TABLE_NAME + " where " + COL_1 + " = " + id, null);
        cur.moveToFirst();
        ret = cur.getCount() != 0;
        cur.close();
        return ret;
    }
}
