package com.antoine_charlotte_romain.dictionary.DataModel;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import com.antoine_charlotte_romain.dictionary.Business.Dictionary;

/**
 * Created by summer1 on 22/06/2015.
 * Updated by summer3 on 22/06/2015.
 */
public class DictionaryDataModel extends DAOBase{

    public static final String SQL_CREATE_DICTIONARY =
            "CREATE TABLE " + DictionaryEntry.TABLE_NAME + " (" +
                    DictionaryEntry._ID + " INTEGER PRIMARY KEY, " +
                    DictionaryEntry.COLUMN_NAME_TITLE + " TEXT NOT NULL"
                    + ");";

    public static final String SQL_DELETE_DICTIONARY = "DROP TABLE IF EXISTS " + DictionaryEntry.TABLE_NAME;

    public static abstract class DictionaryEntry implements BaseColumns {
        public static final String TABLE_NAME = "dictionary";
        public static final String COLUMN_NAME_TITLE = "title";
    }

    /**
     * Insert a dictionary in the DataBase
     *
     * @param d
     *          The dictionary to insert
     * @return The id of the added dictionary or -1 if the dictionary already exists
     */
    public long insert(Dictionary d){
        // Look if this dictionary (with this name) already exists
        if(select(d.getTitle()) == null){
            // Gets the data repository in write mode
            SQLiteDatabase db = open();

            // Create a new map of values, where column names are the keys
            ContentValues values = new ContentValues();
            values.put(DictionaryEntry.COLUMN_NAME_TITLE, d.getTitle());

            // Insert the new row, returning the primary key value of the new row
            long newdictionaryID;
            newdictionaryID = db.insert(DictionaryEntry.TABLE_NAME, null, values);

            // Close DB
            db.close();

            return newdictionaryID;
        }
        return -1;

    }

    /**
     * Find a dictionary in the DataBase with its id
     *
     * @param id
     *          The id of the dictionary to find
     * @return The dictionary, or null if it was not found
     */
    public Dictionary select(long id){
        // Gets the data repository in write mode
        SQLiteDatabase db = open();

        // Query
        Cursor c = db.rawQuery("select * from " + DictionaryEntry.TABLE_NAME + "where " + DictionaryEntry._ID + " = ?", new String[]{String.valueOf(id)});

        // Close DB
        db.close();

        // Creating object found
        if (c.getCount() == 1) {
           c.moveToNext();
            // Obtained row is made of :
            // / id / title /
            return new Dictionary(c.getLong(0), c.getString(1));
        }
        // if Cursor object has not only one element, something wrong happened
        return null;
    }

    /**
     * Find a dictionary in the DataBase with its name/title
     *
     * @param title
     *          The title of the dictionary to find
     * @return The dictionary, or null if it was not found
     */
    public Dictionary select(String title){
        // Gets the data repository in write mode
        SQLiteDatabase db = open();

        Cursor c = db.rawQuery("select * from " + DictionaryEntry.TABLE_NAME + "where " + DictionaryEntry.COLUMN_NAME_TITLE + " = ?", new String[]{title});

        // Close DB
        db.close();

        if (c.getCount() == 1) {
            c.moveToNext();
            // Obtained row is made of :
            // / id / title /
            return new Dictionary(c.getLong(0), c.getString(1));
        }
        return null;
    }

    /**
     * Update the specified dictionary
     *
     * @param d
     *          The dictionary to update
     */
    public void update(Dictionary d){
        // Gets the data repository in write mode
        SQLiteDatabase db = open();

        ContentValues value = new ContentValues();
        value.put(DictionaryEntry.COLUMN_NAME_TITLE,d.getTitle());

        db.update(DictionaryEntry.TABLE_NAME, value, DictionaryEntry._ID + " = ?", new String[]{String.valueOf(d.getId())});

        // Close DB
        db.close();
    }

    /**
     * Delete a dictionary with its id
     *
     * @param id
     *          The id of the dictionary to delete
     */
    public void delete(long id){
        // Gets the data repository in write mode
        SQLiteDatabase db = open();

        // delete words with dictionqryID in word table ? Or automatic with foreign key
        db.delete(DictionaryEntry.TABLE_NAME, DictionaryEntry._ID + " = ?", new String[]{String.valueOf(id)});

        // Close DB
        db.close();
    }

    /**
     * Delete a dictionary with its name
     *
     * @param dictionaryName
     *          The name of the dictionary to delete
     */
    public void delete(String dictionaryName){
        // Gets the data repository in write mode
        SQLiteDatabase db = open();

        // delete words with dictionqryID in word table ? Or automatic with foreign key
        db.delete(DictionaryEntry.TABLE_NAME, DictionaryEntry.COLUMN_NAME_TITLE + " = ?", new String[]{dictionaryName});

        // Close DB
        db.close();
    }
}
