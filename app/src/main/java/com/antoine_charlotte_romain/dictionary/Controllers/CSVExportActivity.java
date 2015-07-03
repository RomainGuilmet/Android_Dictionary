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
import android.widget.EditText;
import android.widget.ListView;

import com.antoine_charlotte_romain.dictionary.Business.Word;
import com.antoine_charlotte_romain.dictionary.DataModel.DictionaryDataModel;
import com.antoine_charlotte_romain.dictionary.DataModel.WordDataModel;
import com.antoine_charlotte_romain.dictionary.R;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class CSVExportActivity extends AppCompatActivity {

    Toolbar toolbar;

    final String EXTRA_NEW_DICO_NAME = "name dico";

    String dictionaryName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_csvexport);

        // Creating The Toolbar and setting it as the Toolbar for the activity
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        // Get data associated to the advanced search
        Intent intent = getIntent();
        if (intent != null){
            dictionaryName = intent.getStringExtra(EXTRA_NEW_DICO_NAME);
        }

        // Au clic sur le bouton, on recup le dictionnaire associ au nom transmis
        // on recup tous les mots de ce dictionnaire et on les ajoute dans un nouveau fichier
        // Changer AndroidManifest.xml ???

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_csvexport, menu);
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

    public void export(View v){
        EditText fileName = (EditText) v.findViewById(R.id.nameCSVfile);

        List<String> existingCSV = getAvailableCSV(Environment.getExternalStorageDirectory());

        // if the specified name do not already exists
        if (!existingCSV.contains(fileName.getText().toString()+".csv")){
            exportCSV(fileName.getText().toString());
            new AlertDialog.Builder(CSVExportActivity.this)
                    .setTitle(R.string.csvexport_popuptitle)
                    .setMessage(R.string.csvexport_popupmessage)
                    .setPositiveButton(R.string.csvexport_popuppositive, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(CSVExportActivity.this, MainActivity.class);
                            startActivity(intent);
                        }
                    })
                /*.setNegativeButton(R.string.csvimport_popupnegative, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // pop up closes and it is possible to import another CSV file
                    }
                })*/
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        } else {
            new AlertDialog.Builder(CSVExportActivity.this)
                    .setTitle(R.string.csvexport_popuptitle1)
                    .setMessage(R.string.csvexport_popupmessage1)
                    .setPositiveButton(R.string.csvexport_popuppositive1, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                /*.setNegativeButton(R.string.csvimport_popupnegative, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // pop up closes and it is possible to import another CSV file
                    }
                })*/
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }

    }

    /**
     * Find all CSV files available on the mobile
     *
     * @return
     *          A list of CSV file names
     */
    private List<String> getAvailableCSV(File f){
        List<String> csvDispo = new ArrayList<String>();

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
                csvDispo.add(f.getName());
            }
        }
        return csvDispo;
    }

    private void exportCSV(String fileName){
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/" + fileName + ".csv");
        DictionaryDataModel ddm = new DictionaryDataModel(this);
        long dicoID = ddm.select(dictionaryName).getId();

        WordDataModel wdm = new WordDataModel(this);
        List<Word> words = wdm.selectAllFromDictionary(dicoID);

        if (!file.exists()){
            BufferedWriter bw;
            String comma = ",";
            String headword = "";
            String translation = "";
            String note = "";

            try {
                file.createNewFile();
                FileWriter fw = new FileWriter(file.getAbsoluteFile());
                bw = new BufferedWriter(fw);
                    // Pour chaque mot du dictionnaire
                for (Word w : words){
                    headword = w.getHeadword();
                    translation = w.getTranslation();
                    note = w.getNote();

                    bw.write(headword + comma + translation + comma + note);
                    bw.newLine();
                    // bw.flush(); // ???
                }
                bw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }


        }
    }

}

