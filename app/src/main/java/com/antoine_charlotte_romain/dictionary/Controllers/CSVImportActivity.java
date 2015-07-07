package com.antoine_charlotte_romain.dictionary.Controllers;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.antoine_charlotte_romain.dictionary.Business.Dictionary;
import com.antoine_charlotte_romain.dictionary.Business.Word;
import com.antoine_charlotte_romain.dictionary.DataModel.DictionaryDataModel;
import com.antoine_charlotte_romain.dictionary.DataModel.WordDataModel;
import com.antoine_charlotte_romain.dictionary.R;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class CSVImportActivity extends AppCompatActivity {

    Toolbar toolbar;
    ListView vue;

    String dictionaryName;
    ArrayList<String> updatedWords;
    int addedWords;

    final String EXTRA_UPDATED_LIST = "listOfUpdatedWords";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_csvimport);

        // Creating The Toolbar and setting it as the Toolbar for the activity
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        // Get data associated to the advanced search
        Intent intent = getIntent();
        if (intent != null){
            dictionaryName = intent.getStringExtra(MainActivity.EXTRA_NEW_DICO_NAME);

            if (intent.getStringArrayListExtra(EXTRA_UPDATED_LIST) != null) { // Utile ? ou EXTRA UPDATED LIST != null
                updatedWords = intent.getStringArrayListExtra(EXTRA_UPDATED_LIST);
                // Display words list
                displayUpdatedWordslist();

            } else {
                // Initialize attributes
                updatedWords = new ArrayList<String>();
                addedWords = 0;

                // Display CSV files list
                displayCSVlist();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_csvimport, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Find all CSV files available on the mobile
     *
     * @return
     *          A list of CSV file names
     */
    private List<File> getAvailableCSV(File f){
        List<File> csvDispo = new ArrayList<File>();

        // if the specified file is a directory
        if (f.isDirectory()){
            // if it contains other files
            if (f.listFiles() != null) {
                // for each file it contains, get all CSV files available in it
                for (File childFile : f.listFiles()) {
                    csvDispo.addAll(getAvailableCSV(childFile));
                }
            }
        } else if (f.isFile()){
            // Check if the file is a CSV file or not
            if (f.getName().endsWith(".csv") || f.getName().endsWith(".CSV")){
                csvDispo.add(f);
            }
        }
        return csvDispo;
    }

    /**
     * Open a CSV file and add its word to a dictionary
     *
     * @param fileToRead
     *          The CSV file providing the words to add in the dictionary
     * @return A list of updated words after the import
     */
    private void importCSV(File fileToRead){
        // Get the dictionary in which the words have to be added
        DictionaryDataModel ddm = new DictionaryDataModel(this);
        Dictionary d = ddm.select(dictionaryName);
        // if a dictionary with this name dos not exist, we create it
        if (d == null){
            d = new Dictionary(dictionaryName);
            ddm.insert(d);
        }
        Long dicoID = d.getId();

        WordDataModel wdm = new WordDataModel(this);

        BufferedReader br = null;
        String line = "";
        // Each line of the CSV file will by split by the comma character
        String cvsSplitBy = ",";
        try {
            InputStream is = new BufferedInputStream(new FileInputStream(fileToRead));
            // ISO-8859-1 interprets accents correctly
            br = new BufferedReader(new InputStreamReader(is,"ISO-8859-1"));
            String[] wordInfo;
            String note;
            String translation;
            Word w = null;
            List<Word> databaseWord = null;
            String meanings = "";
            while ((line = br.readLine()) != null) {
                // Split the line with comma as a separator
                wordInfo = line.split(cvsSplitBy);
                // TODO warning : elements must not have comma in their string
                note = "";
                translation = "";
                // if a translation exists
                if (wordInfo.length >= 2)
                    translation = extractWord(wordInfo[1]);
                // if a note exists
                if (wordInfo.length >= 3)
                    note = extractWord(wordInfo[2]);
                // Add the word in the database
                w = new Word(dicoID,extractWord(wordInfo[0]),translation,note);
                int result = wdm.insert(w);

                // if the headword of the word we try to insert already exists in this dictionary
                if (result == 1){
                    // Get the already existing word
                    databaseWord = wdm.selectFromHeadWord(w.getHeadword(),dicoID);
                    if (databaseWord.size() == 1){
                        // Get its translation
                        meanings = databaseWord.get(0).getTranslation();
                        // if the CSV word translation does not appear in the already existing word translation
                        if (!meanings.contains(translation)){
                            // TODO CHOOSE HOW TO REPRESENT ANOTHER MEANING
                            meanings = meanings + " - " + translation;
                            w.setTranslation(meanings);
                            wdm.update(w);
                            updatedWords.add(w.getHeadword());
                        }
                    }
                } else if (result == 0){
                    // if the word was successfully added in the database
                    addedWords = addedWords + 1;
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Suppress the simple quotes that circle q word in the CSV file
     * @param s
     *          The word to "clean"
     * @return
     *          The word without the simple quotes
     */
    private String extractWord(String s){
        // TODO or just concatenate all strings after split() function ?
        String word = s;
        String splitBy = "'"; // Use the simple quote as a separator
        String[] strings = s.split(splitBy);
        // if the array has more than 1 element, a simple quote was found
        if (strings.length >= 2){
            // The second element of the array is recorded
            word = strings[1];

            for (int i = 2; i<strings.length-1; i++){
                // Being in that case means that there is a simple quote in the word itself
                // So we concatenate all the elements of the array, avoiding the first and the last element
                // and we had the suppressed simple quote
                word = word + "'" + strings[i];
            }
        }
        return word;
    }

    private void displayCSVlist(){
        // Get all available CSV files in the mobile
        final List<File> csvDispo = this.getAvailableCSV(Environment.getExternalStorageDirectory());

        // Display results
        vue = (ListView) findViewById(R.id.resultsList);
        List<HashMap<String, String>> liste = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> element;

        // if there is no result to display
        if(csvDispo.size()==0){
            element = new HashMap<String, String>();
            element.put("name", "No CSV found");
            liste.add(element);
        }

        // Fill the list to display with the name of the CSV files found
        for(int i = 0 ; i < csvDispo.size() ; i++) {
            // we add each word of the results list in this new list
            element = new HashMap<String, String>();
            element.put("name", String.valueOf(csvDispo.get(i).getName()));
            liste.add(element);
        }

        ListAdapter adapter = new SimpleAdapter(this,
                liste,
                android.R.layout.simple_list_item_1,
                new String[] {"name"},
                new int[] {android.R.id.text1});

        // Give ListView to the SimpleAdapter
        vue.setAdapter(adapter);

        // Listener on the list
        vue.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            // When an item of the list is clicked
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Import the chosen CSV file in the previously specified dictionary
                updatedWords = new ArrayList<String>();
                addedWords = 0;
                importCSV(csvDispo.get(position));

                // Display a pop up window
                new AlertDialog.Builder(CSVImportActivity.this)
                        .setTitle(R.string.csvimport_popuptitle)
                        .setMessage("Added words : " + addedWords + "\nUpdated words : "
                                + updatedWords.size())
                        .setPositiveButton(R.string.csvimport_popuppositive, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // pop up closes and the list of the words in this dictionary is displayed
                                DictionaryDataModel ddm = new DictionaryDataModel(CSVImportActivity.this);
                                Dictionary d = ddm.select(dictionaryName);
                                Intent intent = new Intent(CSVImportActivity.this,ListWordsActivity.class);
                                intent.putExtra(MainActivity.EXTRA_DICTIONARY, d);
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton(R.string.csvimport_popupnegative, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // pop up closes and the list of the updated words is displayed
                                Intent intent = new Intent(CSVImportActivity.this,CSVImportActivity.class);

                                intent.putStringArrayListExtra(EXTRA_UPDATED_LIST, updatedWords);
                                intent.putExtra(MainActivity.EXTRA_NEW_DICO_NAME, dictionaryName);

                                startActivity(intent);
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        });
    }

    private void displayUpdatedWordslist(){
        // Change name of the list
        TextView tv = (TextView) findViewById(R.id.listName);
        tv.setText(R.string.csvimport_wordsList);

        // Display results
        vue = (ListView) findViewById(R.id.resultsList);
        List<HashMap<String, String>> liste = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> element;

        // if there is no result to display
        if(updatedWords.size()==0){
            element = new HashMap<String, String>();
            element.put("headword", "No updated word");
            liste.add(element);
        }

        DictionaryDataModel ddm = new DictionaryDataModel(CSVImportActivity.this);
        final Dictionary d = ddm.select(dictionaryName);
        long dicoid = d.getId();
        WordDataModel wdm = new WordDataModel(CSVImportActivity.this);
        Word w;

        // Fill the list to display with the updated words
        for(int i = 0 ; i < updatedWords.size() ; i++) {
            // we add each word of the results list in this new list
            element = new HashMap<String, String>();
            ArrayList<Word> aw = wdm.selectFromHeadWord(updatedWords.get(i),dicoid);
            if (aw.size() == 1){
                w = aw.get(0);
                element.put("id", String.valueOf(w.getId()));
                element.put("diconame", dictionaryName);
                element.put("dicoid", String.valueOf(dicoid));
                element.put("headword", w.getHeadword());
                element.put("translation", w.getTranslation());
                element.put("note", w.getNote());
                liste.add(element);
            }
        }

        ListAdapter adapter = new SimpleAdapter(this,
                liste,
                android.R.layout.simple_list_item_2,
                new String[] {"headword", "diconame"},
                new int[] {android.R.id.text1, android.R.id.text2 });

        // Give ListView to the SimpleAdapter
        vue.setAdapter(adapter);

        // Listener on the list
        vue.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            // When an item of the list is clicked
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                HashMap<String, String> item = (HashMap<String, String>) parent.getItemAtPosition(position);
                Long idword = Long.parseLong(item.get("id"));
                Long dicoid = Long.parseLong(item.get("dicoid"));
                String headword = item.get("headword");
                String translation = item.get("translation");
                String note = item.get("note");

                Intent intent = new Intent(CSVImportActivity.this, WordActivity.class);
                intent.putExtra(MainActivity.EXTRA_WORD, new Word(idword, dicoid, headword, translation, note));

                intent.putExtra(MainActivity.EXTRA_DICTIONARY, d);

                startActivity(intent);
            }
        });
    }

}