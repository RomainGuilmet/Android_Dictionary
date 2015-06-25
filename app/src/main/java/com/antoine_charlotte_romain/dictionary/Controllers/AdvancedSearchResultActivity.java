package com.antoine_charlotte_romain.dictionary.Controllers;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.antoine_charlotte_romain.dictionary.Business.Word;
import com.antoine_charlotte_romain.dictionary.DataModel.DictionaryDataModel;
import com.antoine_charlotte_romain.dictionary.DataModel.WordDataModel;
import com.antoine_charlotte_romain.dictionary.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class AdvancedSearchResultActivity extends AppCompatActivity {

    Toolbar toolbar;
    ListView vue;

    final String ALL_DICO = "All";

    final String EXTRA_BEGIN_STRING = "begin";
    final String EXTRA_MIDDLE_STRING = "middle";
    final String EXTRA_END_STRING = "end";
    final String EXTRA_HEAD_OR_WHOLE = "headOrWhole";
    final String EXTRA_TARGET_DICO = "target";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advanced_search_result);

        // Creating The Toolbar and setting it as the Toolbar for the activity
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        ArrayList<Word> results = new ArrayList<Word>();

        // Get data associated to the advanced search
        Intent intent = getIntent();
        if (intent != null){
            String begin = intent.getStringExtra(EXTRA_BEGIN_STRING);
            String middle = intent.getStringExtra(EXTRA_MIDDLE_STRING);
            String end = intent.getStringExtra(EXTRA_END_STRING);
            String headWhole = intent.getStringExtra(EXTRA_HEAD_OR_WHOLE);
            String dico = intent.getStringExtra(EXTRA_TARGET_DICO);

            System.out.println("b = "+begin+".");
            System.out.println("m = "+middle+".");
            System.out.println("e = "+end+".");
            System.out.println(headWhole);

            // find id of the dictionary
            long id;
            DictionaryDataModel ddm = new DictionaryDataModel(this);
/*            ddm.insert(new Dictionary("Dico1"));
            ddm.insert(new Dictionary("Dico2"));
            ddm.insert(new Dictionary("Dico3"));
            ddm.insert(new Dictionary("Dico4"));*/

            System.out.println(dico);

            if(!dico.equals(ALL_DICO)) {
                id = ddm.select(dico).getId();
            } else {
                id = Word.ALL_DICTIONARIES;
            }

            System.out.println(id);

            // search
            WordDataModel wdm = new WordDataModel(this);

/*            wdm.insert(new Word(1, "try", "essayer", "This is my note"));
            wdm.insert(new Word(2, "try2", "essayer", "This is my note"));
            wdm.insert(new Word(1, "play", "jouer", "This is my note yeah yeah"));
            wdm.insert(new Word(1, "break", "briser, pause", "Great or note"));*/

            if(headWhole.equals("head")){
                results = wdm.selectHeadwordWithBeginMiddleEnd(begin, middle, end, id);
            } else {
                results = wdm.selectWholeWordWithBeginMiddleEnd(begin, middle, end, id);
            }
        }

        System.out.println(results.size());

        // Display results
        vue = (ListView) findViewById(R.id.resultsList);

        List<HashMap<String, String>> liste = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> element;

        for(int i = 0 ; i < results.size() ; i++) {
            // we add each word of the results list in this new list
            element = new HashMap<String, String>();

            element.put("id", String.valueOf(results.get(i).getId()));
            element.put("dicoid", String.valueOf(results.get(i).getDictionaryID()));
            element.put("headword", results.get(i).getHeadword());
            element.put("translation", results.get(i).getTranslation());
            element.put("note", results.get(i).getNote());

            liste.add(element);
        }

        ListAdapter adapter = new SimpleAdapter(this,
                liste,
                android.R.layout.simple_list_item_2,
                new String[] {"headword", "translation"},
                new int[] {android.R.id.text1, android.R.id.text2 });

        //Pour finir, on donne a la ListView le SimpleAdapter
        vue.setAdapter(adapter);

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

}
