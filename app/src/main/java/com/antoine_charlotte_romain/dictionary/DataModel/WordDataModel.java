package com.antoine_charlotte_romain.dictionary.DataModel;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import com.antoine_charlotte_romain.dictionary.Business.Word;

import java.util.ArrayList;

/**
 * Created by summer1 on 22/06/2015.
 */
public class WordDataModel extends DAOBase{

    public static final String SQL_CREATE_WORD =
            "CREATE TABLE " + WordEntry.TABLE_NAME + " (" +
                    WordEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    WordEntry.COLUMN_NAME_DICTIONARY_ID + " INTEGER NOT NULL, " +
                    WordEntry.COLUMN_NAME_HEADWORD + " TEXT NOT NULL, " +
                    WordEntry.COLUMN_NAME_TRANSLATION + " TEXT NOT NULL, " +
                    WordEntry.COLUMN_NAME_NOTE + " TEXT, " +
                    "FOREIGN KEY (" + WordEntry.COLUMN_NAME_DICTIONARY_ID + ") REFERENCES " + DictionaryDataModel.DictionaryEntry.TABLE_NAME + " (" + DictionaryDataModel.DictionaryEntry._ID + ")" +
                    ");";

    public static final String SQL_DELETE_WORD = "DROP TABLE IF EXISTS " + WordEntry.TABLE_NAME;

    private static final String SQL_SELECT_WORD_FROM_ID = "SELECT * FROM " + WordEntry.TABLE_NAME + " WHERE " + WordEntry._ID + " = ?;";

    private static final String SQL_SELECT_WORD_FROM_HEADWORD = "SELECT * FROM " + WordEntry.TABLE_NAME + " WHERE " + WordEntry.COLUMN_NAME_HEADWORD + " LIKE ?;";

    private static final String SQL_SELECT_WORD_FROM_HEADWORD_AND_DICTIONARY = "SELECT * FROM " + WordEntry.TABLE_NAME +
                    " WHERE " + WordEntry.COLUMN_NAME_HEADWORD + " LIKE ?" +
                    " AND " + WordEntry.COLUMN_NAME_DICTIONARY_ID + " = ?;";

    private static final String SQL_SELECT_WORD_FROM_WHOLE_WORD = "SELECT * FROM " + WordEntry.TABLE_NAME +
                    " WHERE " + WordEntry.COLUMN_NAME_HEADWORD + " LIKE ?" +
                    " OR " + WordEntry.COLUMN_NAME_TRANSLATION + " LIKE ?" +
                    " OR " + WordEntry.COLUMN_NAME_NOTE + " LIKE ?;";

    private static final String SQL_SELECT_WORD_FROM_WHOLE_WORD_AND_DICTIONARY = "SELECT * FROM " + WordEntry.TABLE_NAME +
                    " WHERE " + WordEntry.COLUMN_NAME_HEADWORD + " LIKE ?" +
                    " OR " + WordEntry.COLUMN_NAME_TRANSLATION + " LIKE ?" +
                    " OR " + WordEntry.COLUMN_NAME_NOTE + " LIKE ?" +
                    " AND " + WordEntry.COLUMN_NAME_DICTIONARY_ID + " = ?;";

    public static abstract class WordEntry implements BaseColumns {
        public static final String TABLE_NAME = "word";
        public static final String COLUMN_NAME_DICTIONARY_ID = "dictionaryID";
        public static final String COLUMN_NAME_HEADWORD = "headword";
        public static final String COLUMN_NAME_TRANSLATION = "translation";
        public static final String COLUMN_NAME_NOTE = "note";
    }

    public WordDataModel(Context context){
        super(context);
    }

    /**
     * Insert a word in the database
     *
     * @param w The word to insert.
     *
     * @return 1 if the word already exists or 2 if we try to insert without a selected dictionary
     */
    public int insert(Word w){

        if(w.getDictionaryID() != Word.ALL_DICTIONARIES) {
            // Gets the data repository in write mode
            SQLiteDatabase db = open();

            ArrayList<Word> aw = selectFromHeadWord(w.getHeadword(), w.getDictionaryID());
            if(aw.size() == 0){
                // Create a new map of values, where column names are the keys
                ContentValues values = new ContentValues();
                values.put(WordEntry.COLUMN_NAME_DICTIONARY_ID, w.getDictionaryID());
                values.put(WordEntry.COLUMN_NAME_HEADWORD, w.getHeadword());
                values.put(WordEntry.COLUMN_NAME_TRANSLATION, w.getTranslation());
                values.put(WordEntry.COLUMN_NAME_NOTE, w.getNote());

                // Insert the new row, returning the primary key value of the new row
                long newWordID = db.insert(WordEntry.TABLE_NAME, WordEntry.COLUMN_NAME_NOTE, values);

                w.setId(newWordID);
        }
            return -1;
        }
        return -2;
    }

    /**
     * Find a word in the database with its ID
     * @param id the ID of the word to find
     * @return the word or null if the word was not found
     */
    public Word selectFromID(long id){
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
    public ArrayList<Word> selectFromHeadWord(String headWord, long dictionaryID){
        SQLiteDatabase db = open();

        Cursor c;
        if(dictionaryID == Word.ALL_DICTIONARIES) {
            c = db.rawQuery(SQL_SELECT_WORD_FROM_HEADWORD, new String[]{String.valueOf(headWord)});
        }
        else{
            c = db.rawQuery(SQL_SELECT_WORD_FROM_HEADWORD_AND_DICTIONARY, new String[]{String.valueOf(headWord), String.valueOf(dictionaryID)});
        }

        ArrayList<Word> listWord = new ArrayList<Word>();
        while (c.moveToNext()) {
            Word w = selectFromID(c.getLong(c.getColumnIndexOrThrow(WordEntry._ID)));
            listWord.add(w);
        }
        c.close();
        return listWord;
    }

    /**
     * Find a word in a dictionary with the wholeword (i.e its headword, its translation and its note)
     * @param word the headword, the translation or the note of the word we are wanted to find
     * @param dictionaryID the ID of the dictionary in we wish we are searching (set this param to Word.ALL_DICTIONARIES to look in all the dictionaries)
     * @return A list of word which have this headword, this translation or this note in the selected dictionary
     */
    public ArrayList<Word> selectFromWholeWord(String word, long dictionaryID){
        SQLiteDatabase db = open();

        Cursor c;
        if(dictionaryID == Word.ALL_DICTIONARIES) {
            c = db.rawQuery(SQL_SELECT_WORD_FROM_WHOLE_WORD, new String[]{String.valueOf(word), String.valueOf(word), String.valueOf(word)});
        }
        else{
            c = db.rawQuery(SQL_SELECT_WORD_FROM_WHOLE_WORD_AND_DICTIONARY, new String[]{String.valueOf(word), String.valueOf(word), String.valueOf(word), String.valueOf(dictionaryID)});
        }

        ArrayList<Word> listWord = new ArrayList<Word>();
        while (c.moveToNext()) {
            Word w = selectFromID(c.getLong(c.getColumnIndexOrThrow(WordEntry._ID)));
            listWord.add(w);
        }
        c.close();
        return listWord;
    }

    public ArrayList<Word> selectAllFromDictionary(long dictionaryID){
        SQLiteDatabase db = open();

        Cursor c = db.rawQuery(SQL_SELECT_WORD_FROM_WHOLE_WORD, new String[]{String.valueOf(word), String.valueOf(word), String.valueOf(word)});

        ArrayList<Word> listWord = new ArrayList<Word>();
        while (c.moveToNext()) {
            Word w = selectFromID(c.getLong(c.getColumnIndexOrThrow(WordEntry._ID)));
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
        // Gets the data repository in write mode
        SQLiteDatabase db = open();

        ContentValues values = new ContentValues();
        values.put(WordEntry.COLUMN_NAME_DICTIONARY_ID, w.getDictionaryID());
        values.put(WordEntry.COLUMN_NAME_HEADWORD, w.getHeadword());
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

        // Define 'where' part of query.
        String selection = WordEntry._ID + " LIKE ?";

        // Specify arguments in placeholder order.
        String[] selectionArgs = { String.valueOf(id) };

        // Issue SQL statement.
        db.delete(WordEntry.TABLE_NAME, selection, selectionArgs);
    }

}

