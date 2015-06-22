package com.antoine_charlotte_romain.dictionary.DataModel;

import android.provider.BaseColumns;

import com.antoine_charlotte_romain.dictionary.Business.Dictionary;

/**
 * Created by summer1 on 22/06/2015.
 */
public class DictionaryDataModel extends DAOBase{

    public static final String SQL_CREATE_DICTIONARY =
            "CREATE TABLE " + DictionaryEntry.TABLE_NAME + " (" +
                    DictionaryEntry._ID + " INTEGER PRIMARY KEY," +
                    DictionaryEntry.COLUMN_NAME_TITLE + " TEXT"
                    + ");";

    public static final String SQL_DELETE_DICTIONARY = "DROP TABLE IF EXISTS " + DictionaryEntry.TABLE_NAME;

    public static abstract class DictionaryEntry implements BaseColumns {
        public static final String TABLE_NAME = "dictionary";
        public static final String COLUMN_NAME_TITLE = "title";
    }


    public void insert(Dictionary d){

    }

    public void select(long id){

    }

    public void update(Dictionary d){

    }

    public void delete(long id){

    }
}
