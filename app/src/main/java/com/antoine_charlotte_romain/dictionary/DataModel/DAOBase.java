package com.antoine_charlotte_romain.dictionary.DataModel;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.antoine_charlotte_romain.dictionary.DataModel.DataBase;

public abstract class DAOBase {

    protected SQLiteDatabase myDb = null;
    protected DataBase.DataBaseHelper myDbHelper =  null;

    public DAOBase(Context context) {
        this.myDbHelper = new DataBase.DataBaseHelper(context);
    }

    public SQLiteDatabase open() {
        myDb = myDbHelper.getWritableDatabase();
        return myDb;
    }

    public void close() {
        myDb.close();
    }

    public SQLiteDatabase getDb() {
        return myDb;
    }
}