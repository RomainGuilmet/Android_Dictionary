package com.antoine_charlotte_romain.dictionary.Controllers;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.antoine_charlotte_romain.dictionary.Business.Word;
import com.antoine_charlotte_romain.dictionary.DataModel.DictionaryDataModel;
import com.antoine_charlotte_romain.dictionary.DataModel.WordDataModel;
import com.antoine_charlotte_romain.dictionary.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class AdvancedSearchResultActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ListView listResults;

    private ArrayList<Word> results;
    private WordDataModel wdm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advanced_search_result);

        // Creating The Toolbar and setting it as the Toolbar for the activity
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Get data associated to the advanced search
        Intent intent = getIntent();
        if (intent != null){
            String begin = intent.getStringExtra(MainActivity.EXTRA_BEGIN_STRING);
            String middle = intent.getStringExtra(MainActivity.EXTRA_MIDDLE_STRING);
            String end = intent.getStringExtra(MainActivity.EXTRA_END_STRING);
            String headWhole = intent.getStringExtra(MainActivity.EXTRA_HEAD_OR_WHOLE);
            String dico = intent.getStringExtra(MainActivity.EXTRA_DICTIONARY);

            // find id of the dictionary
            long id;
            DictionaryDataModel ddm = new DictionaryDataModel(this);
            if(!dico.equals(MainActivity.ALL_DICO)) {
                id = ddm.select(dico).getId();
            } else {
                id = Word.ALL_DICTIONARIES;
            }

            // search
            wdm = new WordDataModel(this);
            if(headWhole.equals("head")){
                results = wdm.selectHeadwordWithBeginMiddleEnd(begin, middle, end, id);
            } else {
                results = wdm.selectWholeWordWithBeginMiddleEnd(begin, middle, end, id);
            }
        }

        // Display results
        listResults = (ListView) findViewById(R.id.resultsList);
        List<HashMap<String, String>> liste = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> element;

        if(results.size()>0) {
            for (int i = 0; i < results.size(); i++) {
                // we add each word of the results list in this new list
                element = new HashMap<String, String>();

                element.put("headword", results.get(i).getHeadword());
                element.put("translation", results.get(i).getTranslation());

                liste.add(element);
            }

            ListAdapter adapter = new SimpleAdapter(this,
                    liste,
                    android.R.layout.simple_list_item_2,
                    new String[]{"headword", "translation"},
                    new int[]{android.R.id.text1, android.R.id.text2});

            // Give ListView to the SimpleAdapter
            listResults.setAdapter(adapter);

            listResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent wordDetailIntent = new Intent(AdvancedSearchResultActivity.this, WordActivity.class);
                    wordDetailIntent.putExtra(MainActivity.EXTRA_WORD, results.get(position));

                    DictionaryDataModel ddm = new DictionaryDataModel(getApplicationContext());
                    wordDetailIntent.putExtra(MainActivity.EXTRA_DICTIONARY, ddm.select(results.get(position).getDictionaryID()));

                    startActivity(wordDetailIntent);
                }
            });
        }

        else {
            LinearLayout advancedSearchLayout = (LinearLayout)findViewById(R.id.advanced_search);

            advancedSearchLayout.removeView(listResults);

            TextView textResult = new TextView(this);
            textResult.setText("No result");
            textResult.setGravity(Gravity.CENTER);
            textResult.setPadding(0, 10, 0, 0);
            advancedSearchLayout.addView(textResult);
        }
    }
}
