package com.antoine_charlotte_romain.dictionary.DataModel;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import com.antoine_charlotte_romain.dictionary.Business.Word;

/**
 * Created by summer1 on 22/06/2015.
 */
public class WordDataModel extends DAOBase{

    public static final String SQL_CREATE_WORD =
            "CREATE TABLE " + WordEntry.TABLE_NAME + " (" +
                    WordEntry._ID + " INTEGER PRIMARY KEY," +
                    WordEntry.COLUMN_NAME_DICTIONARY_ID + " INTEGER," +
                    WordEntry.COLUMN_NAME_HEADWORD + " TEXT," +
                    WordEntry.COLUMN_NAME_TRANSLATION + " TEXT," +
                    WordEntry.COLUMN_NAME_NOTE + " TEXT," +
                    "FOREIGN KEY(" + WordEntry.COLUMN_NAME_DICTIONARY_ID + ") REFERENCES " + DictionaryDataModel.DictionaryEntry.TABLE_NAME + "(" + DictionaryDataModel.DictionaryEntry._ID + ")"
                    + ");";

    public static final String SQL_DELETE_WORD = "DROP TABLE IF EXISTS " + WordEntry.TABLE_NAME;

    public static abstract class WordEntry implements BaseColumns {
        public static final String TABLE_NAME = "word";
        public static final String COLUMN_NAME_DICTIONARY_ID = "dictionaryID";
        public static final String COLUMN_NAME_HEADWORD = "headword";
        public static final String COLUMN_NAME_TRANSLATION = "translation";
        public static final String COLUMN_NAME_NOTE = "note";
    }


    public long insert(Word w){
        // Gets the data repository in write mode
        SQLiteDatabase db = open();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(WordEntry.COLUMN_NAME_DICTIONARY_ID, w.getDictionaryID());
        values.put(WordEntry.COLUMN_NAME_HEADWORD, w.getHeadword());
        values.put(WordEntry.COLUMN_NAME_TRANSLATION, w.getTranslation());
        values.put(WordEntry.COLUMN_NAME_NOTE, w.getNote());

        // Insert the new row, returning the primary key value of the new row
        long newWordID;
        newWordID = db.insert(WordEntry.TABLE_NAME, WordEntry.COLUMN_NAME_NOTE, values);


        return newWordID;
    }

    public void select(long id){

    }

    public void update(Word w){

    }

    public void delete(long id){

    }

}

