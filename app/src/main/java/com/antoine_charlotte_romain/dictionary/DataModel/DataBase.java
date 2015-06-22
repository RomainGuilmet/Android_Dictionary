package com.antoine_charlotte_romain.dictionary.DataModel;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

/**
 * Created by summer1 on 22/06/2015.
 */
public class DataBase {

    private static final String SQL_CREATE_DICTIONARIES =
            "CREATE TABLE " + Dictionary.TABLE_NAME + " (" +
                    Dictionary._ID + " INTEGER PRIMARY KEY," +
                    Dictionary.COLUMN_NAME_TITLE + " TEXT"
                + ");";

    private static final String SQL_CREATE_WORDS =
            "CREATE TABLE " + Word.TABLE_NAME + " (" +
                    Word._ID + " INTEGER PRIMARY KEY," +
                    Word.COLUMN_NAME_DICTIONARY_ID + " INTEGER," +
                    Word.COLUMN_NAME_HEADWORD + " TEXT," +
                    Word.COLUMN_NAME_TRANSLATION + " TEXT," +
                    Word.COLUMN_NAME_NOTE + " TEXT," +
                    "FOREIGN KEY(" + Word.COLUMN_NAME_DICTIONARY_ID + ") REFERENCES " + Dictionary.TABLE_NAME + "(" + Dictionary._ID + ")"
                    + ");";

    private static final String SQL_DELETE_DICTIONARIES = "DROP TABLE IF EXISTS " + Dictionary.TABLE_NAME;
    private static final String SQL_DELETE_WORDS = "DROP TABLE IF EXISTS " + Word.TABLE_NAME;

    public DataBase(){}

    public static abstract class Dictionary implements BaseColumns {
        public static final String TABLE_NAME = "dictionary";
        public static final String COLUMN_NAME_TITLE = "title";
    }

    public static abstract class Word implements BaseColumns {
        public static final String TABLE_NAME = "word";
        public static final String COLUMN_NAME_DICTIONARY_ID = "dictionaryID";
        public static final String COLUMN_NAME_HEADWORD = "headword";
        public static final String COLUMN_NAME_TRANSLATION = "translation";
        public static final String COLUMN_NAME_NOTE = "note";
    }

    public static class DataBaseHelper extends SQLiteOpenHelper {
        // If you change the database schema, you must increment the database version.
        public static final int DATABASE_VERSION = 1;
        public static final String DATABASE_NAME = "dictionary_database.db";

        public DataBaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(SQL_CREATE_DICTIONARIES);
            db.execSQL(SQL_CREATE_WORDS);
        }
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // This database is only a cache for online data, so its upgrade policy is
            // to simply to discard the data and start over
            db.execSQL(SQL_DELETE_DICTIONARIES);
            db.execSQL(SQL_DELETE_WORDS);
            onCreate(db);
        }
        public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            onUpgrade(db, oldVersion, newVersion);
        }
    }
}
