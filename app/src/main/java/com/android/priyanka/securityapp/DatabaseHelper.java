package com.android.priyanka.securityapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.HashMap;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_NAME = "security_db";

    private static final String TABLE_NAME = "admin_table";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

       // db = this.getWritableDatabase();
        String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS admin_table (id INTEGER PRIMARY KEY AUTOINCREMENT,name TEXT,gender TEXT,phone TEXT,date TEXT,time TEXT,image BLOB)";
        db.execSQL(CREATE_TABLE);
        //db.close();

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
    public void insertStudent(String personname,String persongender,String phnum, String date_n,String cur_time)
    {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", personname);
        values.put("gender", persongender);
        values.put("phone", phnum);
        values.put("date", date_n);
        values.put("time", cur_time);
        database.insert("admin_table", null, values);
        database.close();
    }



}
