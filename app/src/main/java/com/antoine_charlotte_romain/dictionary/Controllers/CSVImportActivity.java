package com.antoine_charlotte_romain.dictionary.Controllers;

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

import com.antoine_charlotte_romain.dictionary.Business.Dictionary;
import com.antoine_charlotte_romain.dictionary.Business.Word;
import com.antoine_charlotte_romain.dictionary.DataModel.DictionaryDataModel;
import com.antoine_charlotte_romain.dictionary.DataModel.WordDataModel;
import com.antoine_charlotte_romain.dictionary.R;

import java.io.BufferedReader;
import java.io.File;
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

    final String EXTRA_NEW_DICO_NAME = "name dico";

    String dictionaryName;

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
            dictionaryName = intent.getStringExtra(EXTRA_NEW_DICO_NAME);
        }

        List<File> csvDispo = this.getAvailableCSV(Environment.getExternalStorageDirectory());

        // Display results
        vue = (ListView) findViewById(R.id.resultsList);
        List<HashMap<String, String>> liste = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> element;

        // if there is no result to display
        if(csvDispo.size()==0){
            element = new HashMap<String, String>();
            element.put("name","No CSV found");
            liste.add(element);
        }

        for(int i = 0 ; i < csvDispo.size() ; i++) {
            // we add each word of the results list in this new list
            element = new HashMap<String, String>();

            element.put("name", String.valueOf(csvDispo.get(i)));

            liste.add(element);
        }

        ListAdapter adapter = new SimpleAdapter(this,
                liste,
                android.R.layout.simple_list_item_1,
                new String[] {"name"},
                new int[] {android.R.id.text1});

        // Give ListView to the SimpleAdapter
        vue.setAdapter(adapter);

        vue.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                /*HashMap<String, String> item = (HashMap<String, String>) parent.getItemAtPosition(position);
                Long idword = Long.parseLong(item.get("id"));
                Long dicoid = Long.parseLong(item.get("dicoid"));
                String headword = item.get("headword");
                String translation = item.get("translation");
                String note = item.get("note");

                Intent intent = new Intent(AdvancedSearchResultActivity.this, WordActivity.class);
                intent.putExtra("selectedWord", new Word(idword,dicoid,headword,translation,note));

                DictionaryDataModel ddm = new DictionaryDataModel(getApplicationContext());
                intent.putExtra("selectedDictionary", ddm.select(dicoid));

                startActivity(intent);*/
                HashMap<String, String> item = (HashMap<String, String>) parent.getItemAtPosition(position);
                importCSV(item.get("name"));
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
    public List<File> getAvailableCSV(File f){
        List<File> csvDispo = new ArrayList<File>();

        if (f.isDirectory()){
            for (File childFile : f.listFiles()){
                csvDispo.addAll(getAvailableCSV(childFile));
            }
        } else if (f.isFile()){
            if (f.getName().endsWith(".csv") || f.getName().endsWith(".CSV")){
                csvDispo.add(f);
            }
        }
        return csvDispo;
    }

    /**
     * Open a CSV file and add its word to a dictionary
     *
     * @param csvFileName
     *          The CSV file name providing the words to add in the dictionary
     */
    private void importCSV(String csvFileName){
        // Get the dictionary in which the words have to be added
        DictionaryDataModel ddm = new DictionaryDataModel(this);
        Dictionary d = ddm.select(dictionaryName); // if the dico exists !!!
        // if it's a new one, when do we create it ? NOW
        if (d == null)
            d = new Dictionary(dictionaryName);

        WordDataModel wdm = new WordDataModel(this);

        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ",";
        try {
            InputStream is = getResources().getAssets().open(csvFileName); // TO CHANGE
            br = new BufferedReader(new InputStreamReader(is));
            String[] wordInfo;
            String note;
            String translation;
            while ((line = br.readLine()) != null) {
                // use comma as separator
                wordInfo = line.split(cvsSplitBy);
                note = "";
                translation = "";
                if (wordInfo.length >= 2)
                    translation = extractWord(wordInfo[1]);
                if (wordInfo.length == 3)
                    note = extractWord(wordInfo[2]);
                wdm.insert(new Word(d.getId(),extractWord(wordInfo[0]),translation,note));
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
        String word = s;
        String splitBy = "\'";
        String[] strings = s.split(splitBy);
        if (strings.length == 1){
            word = strings[0];
        }
        return word;
    }

}

