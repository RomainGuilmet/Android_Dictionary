package com.antoine_charlotte_romain.dictionary.DataModel;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import com.antoine_charlotte_romain.dictionary.Business.SearchDate;
import com.antoine_charlotte_romain.dictionary.Business.Word;

import java.util.ArrayList;

/**
 * Created by summer1 on 08/07/2015.
 */
public class SearchDateDataModel extends DAOBase{

    public static final String SQL_CREATE_SEARCH_DATE =
            "CREATE TABLE " + SearchDateEntry.TABLE_NAME + " (" +
                    SearchDateEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    SearchDateEntry.COLUMN_NAME_WORD_ID + " INTEGER NOT NULL, " +
                    SearchDateEntry.COLUMN_NAME_SEARCH_DATE + " DEFAULT CURRENT_TIMESTAMP, "+
                    "FOREIGN KEY (" + SearchDateEntry.COLUMN_NAME_WORD_ID + ") REFERENCES " + WordDataModel.WordEntry.TABLE_NAME + " (" + WordDataModel.WordEntry._ID + ")" +
                    ");";

    public static final String SQL_DELETE_SEARCH_DATE= "DROP TABLE IF EXISTS " + SearchDateEntry.TABLE_NAME + ";";

    public static abstract class SearchDateEntry implements BaseColumns {
        public static final String TABLE_NAME = "searchDate";
        public static final String COLUMN_NAME_WORD_ID = "wordID";
        public static final String COLUMN_NAME_SEARCH_DATE= "dateOfSearch";
    }

    private static final String SQL_SELECT_SEARCH_DATE_FROM_ID = "SELECT * FROM " + SearchDateEntry.TABLE_NAME + " WHERE " + SearchDateEntry._ID + " = ?;";

    private static final String SQL_SELECT_SEARCH_DATE_FROM_WORD_AND_DATE = "SELECT * FROM " + SearchDateEntry.TABLE_NAME +
            " sd INNER JOIN " + WordDataModel.WordEntry.TABLE_NAME + " w ON sd." + SearchDateEntry.COLUMN_NAME_WORD_ID + "=w." + WordDataModel.WordEntry._ID +
            " WHERE w." + WordDataModel.WordEntry.COLUMN_NAME_HEADWORD + " LIKE ?" +
            " OR w." + WordDataModel.WordEntry.COLUMN_NAME_TRANSLATION + " LIKE ?" +
            " OR sd." + SearchDateEntry.COLUMN_NAME_SEARCH_DATE + " LIKE ? ORDER BY " + SearchDateEntry.COLUMN_NAME_SEARCH_DATE + " DESC;";

    private static final String SQL_SELECT_ALL_SEARCH_DATE = "SELECT * FROM " + SearchDateEntry.TABLE_NAME + " ORDER BY " + SearchDateEntry.COLUMN_NAME_SEARCH_DATE + " DESC" +
           " LIMIT ? OFFSET ?;";

    public SearchDateDataModel(Context context){
        super(context);
    }

    /**
     * Insert a searchDate of a word in the database
     * @param sd the searchDate to insert
     * @return 0 if it's a success, 1 if there were a previous search in this word less than an hour ago and 2 if the word searched doesn't exists
     */
    public int insert(SearchDate sd){
        // Gets the data repository in write mode
        SQLiteDatabase db = open();

        WordDataModel wdm = new WordDataModel(context);
        Word w = wdm.select(sd.getWord().getId());
        if(w != null) {
            ArrayList<SearchDate> asd = select(sd.getWord(), sd.getDate());
            if (asd.size() == 0) {
                // Create a new map of values, where column names are the keys
                ContentValues values = new ContentValues();
                values.put(SearchDateEntry.COLUMN_NAME_WORD_ID, sd.getWord().getId());

                // Insert the new row, returning the primary key value of the new row
                long newSearchDateID = db.insert(SearchDateEntry.TABLE_NAME, null, values);

                sd.setId(newSearchDateID);
                return 0;
            }
            return 1;
        }
        return 2;
    }

    public SearchDate select(long id){
        SQLiteDatabase db = open();

        Cursor c = db.rawQuery(SQL_SELECT_SEARCH_DATE_FROM_ID, new String[]{String.valueOf(id)});

        SearchDate sd;
        if(c.getCount() == 1) {
            c.moveToFirst();
            long wordID = c.getLong(c.getColumnIndexOrThrow(SearchDateEntry.COLUMN_NAME_WORD_ID));
            String date = c.getString(c.getColumnIndexOrThrow(SearchDateEntry.COLUMN_NAME_SEARCH_DATE));

            WordDataModel wdm = new WordDataModel(context);
            Word w = wdm.select(wordID);

            sd = new SearchDate(w, date);
        }
        else {
            sd = null;
        }

        c.close();
        return sd;
    }

    public ArrayList<SearchDate> select(String search){
        SQLiteDatabase db = open();

        Cursor c = db.rawQuery(SQL_SELECT_SEARCH_DATE_FROM_WORD_AND_DATE, new String[]{String.valueOf(search)+"%", String.valueOf(search)+"%", "%"+String.valueOf(search)+"%"});

        ArrayList<SearchDate> listDate = new ArrayList<>();
        while (c.moveToNext()) {
            SearchDate sd = select(c.getLong(c.getColumnIndexOrThrow(SearchDateEntry._ID)));
            listDate.add(sd);
        }
        c.close();

        return listDate;
    }

    public ArrayList<SearchDate> select(Word w, String date){
        ArrayList<SearchDate> listDate = new ArrayList<>();
        return listDate;
    }

    public ArrayList<SearchDate> selectAll(int limit, int offset){
        SQLiteDatabase db = open();

        Cursor c = db.rawQuery(SQL_SELECT_ALL_SEARCH_DATE, new String[]{String.valueOf(limit), String.valueOf(offset)});

        ArrayList<SearchDate> listDate = new ArrayList<>();
        while(c.moveToNext()) {
            SearchDate sd = select(c.getLong(c.getColumnIndexOrThrow(SearchDateEntry._ID)));
            listDate.add(sd);
        }

        return listDate;
    }

    public void delete(long id){
        // Gets the data repository in write mode
        SQLiteDatabase db = open();

        // Define 'where' part of query.
        String selection = SearchDateEntry._ID + " LIKE ?";

        // Specify arguments in placeholder order.
        String[] selectionArgs = { String.valueOf(id) };

        // Issue SQL statement.
        db.delete(SearchDateEntry.TABLE_NAME, selection, selectionArgs);
    }

    public void deleteAll(long id){
        // Gets the data repository in write mode
        SQLiteDatabase db = open();

        // Define 'where' part of query.
        String selection = SearchDateEntry.COLUMN_NAME_WORD_ID + " LIKE ?";

        // Specify arguments in placeholder order.
        String[] selectionArgs = {String.valueOf(id)};

        // Issue SQL statement.
        db.delete(SearchDateEntry.TABLE_NAME, selection, selectionArgs);
    }
}
