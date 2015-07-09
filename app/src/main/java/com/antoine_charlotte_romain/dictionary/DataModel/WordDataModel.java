package com.antoine_charlotte_romain.dictionary.DataModel;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import com.antoine_charlotte_romain.dictionary.Business.Dictionary;
import com.antoine_charlotte_romain.dictionary.Business.Word;

import java.util.ArrayList;

/**
 * Created by summer1 on 22/06/2015.
 */
public class WordDataModel extends DAOBase{

    public static final String SQL_CREATE_WORD =
            "CREATE TABLE " + WordEntry.TABLE_NAME + " (" +
                    WordEntry._ID + " INTEGER PRIMARY KEY, " +
                    WordEntry.COLUMN_NAME_DICTIONARY_ID + " INTEGER NOT NULL, " +
                    WordEntry.COLUMN_NAME_HEADWORD + " TEXT NOT NULL, " +
                    WordEntry.COLUMN_NAME_TRANSLATION + " TEXT NOT NULL, " +
                    WordEntry.COLUMN_NAME_NOTE + " TEXT, " +
                    "FOREIGN KEY (" + WordEntry.COLUMN_NAME_DICTIONARY_ID + ") REFERENCES " + DictionaryDataModel.DictionaryEntry.TABLE_NAME + " (" + DictionaryDataModel.DictionaryEntry._ID + ")" +
                    ");";

    public static final String SQL_DELETE_WORD = "DROP TABLE IF EXISTS " + WordEntry.TABLE_NAME;

    public static abstract class WordEntry implements BaseColumns {
        public static final String TABLE_NAME = "word";
        public static final String COLUMN_NAME_DICTIONARY_ID = "dictionaryID";
        public static final String COLUMN_NAME_HEADWORD = "headword";
        public static final String COLUMN_NAME_TRANSLATION = "translation";
        public static final String COLUMN_NAME_NOTE = "note";
    }

    private static final String SQL_SELECT_WORD_FROM_ID = "SELECT * FROM " + WordEntry.TABLE_NAME + " WHERE " + WordEntry._ID + " = ?;";

    private static final String SQL_SELECT_WORD_FROM_HEADWORD = "SELECT * FROM " + WordEntry.TABLE_NAME + " WHERE UPPER(" + WordEntry.COLUMN_NAME_HEADWORD + ") LIKE ?"
            + " ORDER BY UPPER(" + WordEntry.COLUMN_NAME_HEADWORD + ") ASC;";

    private static final String SQL_SELECT_WORD_FROM_HEADWORD_AND_DICTIONARY = "SELECT * FROM " + WordEntry.TABLE_NAME +
                    " WHERE UPPER(" + WordEntry.COLUMN_NAME_HEADWORD + ") LIKE ?" +
                    " AND " + WordEntry.COLUMN_NAME_DICTIONARY_ID + " = ?" +
                     " ORDER BY UPPER(" + WordEntry.COLUMN_NAME_HEADWORD + ") ASC;";

    private static final String SQL_SELECT_WORD_WITH_BEGIN_MIDDLE_END_HEADWORD = "SELECT * FROM " + WordEntry.TABLE_NAME +
            " WHERE " + WordEntry.COLUMN_NAME_HEADWORD + " LIKE ?;";

    private static final String SQL_SELECT_WORD_WITH_BEGIN_MIDDLE_END_HEADWORD_AND_DICTIONARY = "SELECT * FROM " + WordEntry.TABLE_NAME +
            " WHERE " + WordEntry.COLUMN_NAME_DICTIONARY_ID + " = ?" +
            " AND " + WordEntry.COLUMN_NAME_HEADWORD + " LIKE ?;";

    private static final String SQL_SELECT_WORD_WITH_BEGIN_MIDDLE_END_WHOLEWORD = "SELECT * FROM " + WordEntry.TABLE_NAME +
            " WHERE " + WordEntry.COLUMN_NAME_HEADWORD + " LIKE ?" +
            " OR " + WordEntry.COLUMN_NAME_TRANSLATION + " LIKE ?" +
            " OR " + WordEntry.COLUMN_NAME_NOTE + " LIKE ?;";

    private static final String SQL_SELECT_WORD_WITH_BEGIN_MIDDLE_END_WHOLEWORD_AND_DICTIONARY = "SELECT * FROM " + WordEntry.TABLE_NAME +
            " WHERE (" + WordEntry.COLUMN_NAME_HEADWORD + " LIKE ?" +
            " OR " + WordEntry.COLUMN_NAME_TRANSLATION + " LIKE ?" +
            " OR " + WordEntry.COLUMN_NAME_NOTE + " LIKE ? )" +
            " AND " + WordEntry.COLUMN_NAME_DICTIONARY_ID + " = ?;";

    private static final String SQL_SELECT_ALL_FROM_DICTIONARY = "SELECT * FROM " + WordEntry.TABLE_NAME
            + " WHERE " + WordEntry.COLUMN_NAME_DICTIONARY_ID + " = ? "
            + " ORDER BY UPPER(" + WordEntry.COLUMN_NAME_HEADWORD + ") ASC;";

    private static final String SQL_SELECT_ALL = "SELECT * FROM " + WordEntry.TABLE_NAME + " ORDER BY UPPER(" + WordEntry.COLUMN_NAME_HEADWORD + ") ASC;";

    private static final String SQL_SELECT_ALL_FROM_DICTIONARY_LIMIT = "SELECT * FROM " + WordEntry.TABLE_NAME
            + " WHERE " + WordEntry.COLUMN_NAME_DICTIONARY_ID + " = ? "
            + " ORDER BY UPPER(" + WordEntry.COLUMN_NAME_HEADWORD + ") ASC LIMIT ? OFFSET ?;";

    private static final String SQL_SELECT_ALL_LIMIT = "SELECT * FROM " + WordEntry.TABLE_NAME + " ORDER BY UPPER(" + WordEntry.COLUMN_NAME_HEADWORD + ") ASC LIMIT ? OFFSET ?;";

    public WordDataModel(Context context){
        super(context);
    }

    /**
     * Insert a word in the database
     * @param w The word to insert.
     * @return 0 if it's a success, 1 if the word already exists, 2 if the dictionary doesn't already exists or 3 if we try to insert without a selected dictionary
     */
    public int insert(Word w){

        if(w.getDictionaryID() != Word.ALL_DICTIONARIES) {
            // Gets the data repository in write mode
            SQLiteDatabase db = open();

            DictionaryDataModel ddm = new DictionaryDataModel(context);
            Dictionary d = ddm.select(w.getDictionaryID());
            if(d != null) {
                ArrayList<Word> aw = select(w.getHeadword(), w.getDictionaryID());
                if (aw.size() == 0) {
                    // Create a new map of values, where column names are the keys
                    ContentValues values = new ContentValues();
                    values.put(WordEntry.COLUMN_NAME_DICTIONARY_ID, w.getDictionaryID());
                    values.put(WordEntry.COLUMN_NAME_HEADWORD, w.getHeadword());
                    values.put(WordEntry.COLUMN_NAME_TRANSLATION, w.getTranslation());
                    values.put(WordEntry.COLUMN_NAME_NOTE, w.getNote());

                    // Insert the new row, returning the primary key value of the new row
                    long newWordID = db.insert(WordEntry.TABLE_NAME, WordEntry.COLUMN_NAME_NOTE, values);

                    w.setId(newWordID);
                    return 0;
                }
                return 1;
            }
            return 2;
        }
        return 3;
    }

    /**
     * Find a word in the database with its ID
     * @param id the ID of the word to find
     * @return the word or null if the word was not found
     */
    public Word select(long id){
        SQLiteDatabase db = open();

        Cursor c = db.rawQuery(SQL_SELECT_WORD_FROM_ID, new String[]{String.valueOf(id)});

        Word w;
        if(c.getCount() == 1) {
            c.moveToFirst();
            long dictionaryID = c.getLong(c.getColumnIndexOrThrow(WordEntry.COLUMN_NAME_DICTIONARY_ID));
            String headword = c.getString(c.getColumnIndexOrThrow(WordEntry.COLUMN_NAME_HEADWORD));
            String translation = c.getString(c.getColumnIndexOrThrow(WordEntry.COLUMN_NAME_TRANSLATION));
            String note = c.getString(c.getColumnIndexOrThrow(WordEntry.COLUMN_NAME_NOTE));

            w = new Word(id, dictionaryID, headword, translation, note);
        }
        else {
            w = null;
        }

        c.close();
        return w;
    }

    /**
     * Find a word in a dictionary with its headword
     * @param headWord the headword of the word we are wanted to find
     * @param dictionaryID the ID of the dictionary in we wish we are searching (set this param to Word.ALL_DICTIONARIES to look in all the dictionaries)
     * @return A list of word which have this headword in the selected dictionary
     */
    public ArrayList<Word> select(String headWord, long dictionaryID){
        SQLiteDatabase db = open();

        Cursor c;
        if(dictionaryID == Word.ALL_DICTIONARIES) {
            c = db.rawQuery(SQL_SELECT_WORD_FROM_HEADWORD, new String[]{String.valueOf(headWord)+"%"});
        }
        else{
            c = db.rawQuery(SQL_SELECT_WORD_FROM_HEADWORD_AND_DICTIONARY, new String[]{String.valueOf(headWord)+"%", String.valueOf(dictionaryID)});
        }

        ArrayList<Word> listWord = new ArrayList<Word>();
        while (c.moveToNext()) {
            Word w = select(c.getLong(c.getColumnIndexOrThrow(WordEntry._ID)));
            listWord.add(w);
        }
        c.close();
        return listWord;
    }

    /**
     * Find a word in a dictionary with the beginning, the middle and the end of its headword
     * @param begin the start of the headword
     * @param middle the middle of the headword
     * @param end the end of the headword
     * @param dictionaryID the ID of the dictionary in we wish we are searching (set this param to Word.ALL_DICTIONARIES to look in all the dictionaries)
     * @return A list of word which have this begin, this middle and this end in the headword
     */
    public ArrayList<Word> selectHeadword(String begin, String middle, String end, long dictionaryID){
        SQLiteDatabase db = open();

        String search = begin+"%"+middle+"%"+end;
        Cursor c;
        if(dictionaryID == Word.ALL_DICTIONARIES) {
            c = db.rawQuery(SQL_SELECT_WORD_WITH_BEGIN_MIDDLE_END_HEADWORD, new String[]{search});
        }
        else{
            c = db.rawQuery(SQL_SELECT_WORD_WITH_BEGIN_MIDDLE_END_HEADWORD_AND_DICTIONARY, new String[]{String.valueOf(dictionaryID), search});
        }

        ArrayList<Word> listWord = new ArrayList<Word>();
        Word w;
        while (c.moveToNext()) {
            w = select(c.getLong(c.getColumnIndexOrThrow(WordEntry._ID)));
            listWord.add(w);
        }
        c.close();
        return listWord;
    }

    /**
     * Find a word in a dictionary with the beginning, the middle and the end of its headword, translation or note
     * @param begin the start of the headword, translation or note
     * @param middle the middle of the headword, translation or note
     * @param end the end of the headword, translation or note
     * @param dictionaryID the ID of the dictionary in we wish we are searching (set this param to Word.ALL_DICTIONARIES to look in all the dictionaries)
     * @return A list of word which have this begin, this middle and this end in the headword, translation or note
     */
    public ArrayList<Word> selectWholeWord(String begin, String middle, String end, long dictionaryID){
        SQLiteDatabase db = open();

        String search = begin+"%"+middle+"%"+end;
        Cursor c;
        if(dictionaryID == Word.ALL_DICTIONARIES) {
            c = db.rawQuery(SQL_SELECT_WORD_WITH_BEGIN_MIDDLE_END_WHOLEWORD, new String[]{search, search, search});
        }
        else{
            c = db.rawQuery(SQL_SELECT_WORD_WITH_BEGIN_MIDDLE_END_WHOLEWORD_AND_DICTIONARY, new String[]{search, search, search, String.valueOf(dictionaryID)});
        }

        ArrayList<Word> listWord = new ArrayList<Word>();
        while (c.moveToNext()) {
            Word w = select(c.getLong(c.getColumnIndexOrThrow(WordEntry._ID)));
            listWord.add(w);
        }
        c.close();
        return listWord;
    }

    /**
     * Find all the words in the database present in a dictionary
     * @param dictionaryID the ID of the dictionary in which we want to find all the words
     * @return A list of all the words present in the selected dictionary
     */
    public ArrayList<Word> selectAll(long dictionaryID){
        SQLiteDatabase db = open();

        Cursor c;

        if(dictionaryID == Word.ALL_DICTIONARIES) {
            c = db.rawQuery(SQL_SELECT_ALL, null);
        }
        else {
            c = db.rawQuery(SQL_SELECT_ALL_FROM_DICTIONARY, new String[]{String.valueOf(dictionaryID)});
        }

        ArrayList<Word> listWord = new ArrayList<Word>();
        while (c.moveToNext()) {
            Word w = select(c.getLong(c.getColumnIndexOrThrow(WordEntry._ID)));
            listWord.add(w);
        }
        c.close();
        return listWord;
    }

    /**
     * Find all the words in the database present in a dictionary
     * @param dictionaryID the ID of the dictionary in which we want to find all the words
     * @param limit the limit of rows to return if set to it will return all the rows
     * @param offset the number of first rows to not return
     * @return A list of all the words present in the selected dictionary
     */
    public ArrayList<Word> selectAll(long dictionaryID, int limit, int offset){
        SQLiteDatabase db = open();

        Cursor c;

        if(dictionaryID == Word.ALL_DICTIONARIES) {
            c = db.rawQuery(SQL_SELECT_ALL_LIMIT, new String[]{String.valueOf(limit), String.valueOf(offset)});
        }
        else {
            c = db.rawQuery(SQL_SELECT_ALL_FROM_DICTIONARY_LIMIT, new String[]{String.valueOf(dictionaryID), String.valueOf(limit), String.valueOf(offset)});
        }

        ArrayList<Word> listWord = new ArrayList<Word>();
        while (c.moveToNext()) {
            Word w = select(c.getLong(c.getColumnIndexOrThrow(WordEntry._ID)));
            listWord.add(w);
        }
        c.close();
        return listWord;
    }

    /**
     * Update a word in the database
     * @param w The word to update
     */
    public void update(Word w){
        SQLiteDatabase db = open();

        ContentValues values = new ContentValues();

        values.put(WordEntry.COLUMN_NAME_TRANSLATION, w.getTranslation());
        values.put(WordEntry.COLUMN_NAME_NOTE, w.getNote());

        db.update(WordEntry.TABLE_NAME, values, WordEntry._ID + " = ?", new String[]{String.valueOf(w.getId())});
    }

    /**
     * Delete a word in the database with its ID
     * @param id The ID of the word to delete
     */
    public void delete(long id){
        // Gets the data repository in write mode
        SQLiteDatabase db = open();

        // suppress its search
        SearchDateDataModel sddm = new SearchDateDataModel(context);
        sddm.deleteAll(id);

        // Define 'where' part of query.
        String selection = WordEntry._ID + " LIKE ?";

        // Specify arguments in placeholder order.
        String[] selectionArgs = { String.valueOf(id) };

        // Issue SQL statement.
        db.delete(WordEntry.TABLE_NAME, selection, selectionArgs);
    }

    /**
     * Delete all the word in the database of this dictionary
     * @param dictionaryId the ID of the dictionary in which we want to delete all the words
     */
    public void deleteAll(long dictionaryId){
        if(dictionaryId != Word.ALL_DICTIONARIES) {
            // Gets the data repository in write mode
            SQLiteDatabase db = open();

            ArrayList<Word> aw = selectAll(dictionaryId);
            for(int i=0; i<aw.size(); i++){
                delete(aw.get(i).getId());
            }
        }
    }

}

