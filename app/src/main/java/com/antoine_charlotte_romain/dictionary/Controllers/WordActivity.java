package com.antoine_charlotte_romain.dictionary.Controllers;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.antoine_charlotte_romain.dictionary.Business.Dictionary;
import com.antoine_charlotte_romain.dictionary.Business.Word;
import com.antoine_charlotte_romain.dictionary.DataModel.WordDataModel;
import com.antoine_charlotte_romain.dictionary.R;
import com.antoine_charlotte_romain.dictionary.Utilities.KeyboardUtility;


public class WordActivity extends AppCompatActivity {

    private EditText dictionaryText;
    private EditText headwordText;
    private EditText translationText;
    private EditText noteText;
    private Toolbar toolbar;
    private MenuItem saveButton;

    private WordDataModel wdm;
    private Word selectedWord;
    private Dictionary selectedDictionary;

    /**
     * This function is called when the Activity start
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.word);

        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        selectedWord = (Word)intent.getSerializableExtra(MainActivity.EXTRA_WORD);
        selectedDictionary = (Dictionary)intent.getSerializableExtra(MainActivity.EXTRA_DICTIONARY);

        dictionaryText = (EditText) findViewById(R.id.editTextDictionary);
        headwordText = (EditText) findViewById(R.id.editTextHeadword);
        translationText = (EditText) findViewById(R.id.editTextTranslation);
        noteText = (EditText) findViewById(R.id.editTextNote);
        dictionaryText.setEnabled(false);
        dictionaryText.setText(selectedDictionary.getTitle());

        setupUI(findViewById(R.id.word_layout));
    }

    /**
     * This function is called when a chil activity back to this view or finish
     */
    @Override
    public void onResume() {
        super.onResume();
    }

    /**
     * This function creates the buttons on the toolBar
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if(selectedWord != null){
            getMenuInflater().inflate(R.menu.menu_word_details, menu);
            showDetails();
        }
        else{
            getMenuInflater().inflate(R.menu.menu_new_word, menu);
            saveButton = menu.findItem(R.id.action_add_word);
            newWord();
        }
        return true;
    }

    /**
     * This function is called when the user click on a button of the toolBar
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId())
        {
            case R.id.action_add_word:
                addWord();
                return true;

            case R.id.action_update_word:
                updateWord();
                return true;

            case R.id.action_delete_word:
                deleteWord(findViewById(R.id.word_layout));
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * This function show the details of a word and allow the user to update or delete it.
     * This function is called after the onCreate if a word was selected by the user.
     */
    private void showDetails(){
        headwordText.setText(selectedWord.getHeadword());
        headwordText.setEnabled(false);
        translationText.setText(selectedWord.getTranslation());
        noteText.setText(selectedWord.getNote());

        getSupportActionBar().setTitle("Details : " + selectedWord.getHeadword());
    }

    /**
     *  This function allow the user to create a new word.
     *  This function is called after the onCreate if no word was selected by the user.
     */
    private void newWord(){
        headwordText.setText("");
        headwordText.setFocusable(true);
        translationText.setText("");
        noteText.setText("");

        getSupportActionBar().setTitle("New word");

        saveButton.setVisible(false);

        headwordText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable arg0) {
                boolean isReady = headwordText.getText().toString().length() > 0;
                saveButton.setVisible(isReady);
            }

        });
    }

    /**
     * This function is called on click on the saveButton, it update the selected word with the new values enter by the user.
     */
    private void updateWord(){
        wdm = new WordDataModel(getApplicationContext());
        selectedWord.setTranslation(translationText.getText().toString());
        selectedWord.setNote(noteText.getText().toString());
        wdm.update(selectedWord);

        Toast.makeText(this, selectedWord.getHeadword() + " updated with success !", Toast.LENGTH_LONG).show();
        finish();
    }

    /**
     * This function is called on click on the addWordButton, it insert a new word with the values enter by the user.
     */
    private void addWord(){
        wdm = new WordDataModel(getApplicationContext());
        Word w = new Word();
        w.setDictionaryID(selectedDictionary.getId());
        w.setHeadword(headwordText.getText().toString());
        w.setTranslation(translationText.getText().toString());
        w.setNote(noteText.getText().toString());
        int i = wdm.insert(w);

        switch (i){
            case 0:
                Toast.makeText(this, w.getHeadword() + " created with success !", Toast.LENGTH_SHORT).show();
                finish();
                break;
            case 1:
                Toast.makeText(this, "Error : " + w.getHeadword() + " already exists.", Toast.LENGTH_SHORT).show();
                break;
            case 2:
                Toast.makeText(this, "Error : this dictionary doesn't exists.", Toast.LENGTH_SHORT).show();
                break;
            case 3:
                Toast.makeText(this, "Error : no dictionary selected.", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    /**
     * This function is called on click on the deleteButton, it asks for a confirmation and then delete the selected word.
     * @param view
     */
    public void deleteWord(View view) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setMessage("Delete this word ?");
        alert.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                Toast.makeText(getApplicationContext(), selectedWord.getHeadword() + " deleted with success !", Toast.LENGTH_SHORT).show();
                wdm = new WordDataModel(getApplicationContext());
                wdm.delete(selectedWord.getId());
                finish();
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });

        alert.show();
    }

    /**
     * This function is used to hide the keyBoard on click outside an editText
     * @param view
     */
    public void setupUI(View view) {
        //Set up touch listener for non-text box views to hide keyboard.
        if(!(view instanceof EditText)) {

            view.setOnTouchListener(new View.OnTouchListener() {

                public boolean onTouch(View v, MotionEvent event) {
                    KeyboardUtility.hideSoftKeyboard(WordActivity.this);
                    return false;
                }

            });
        }

        //If a layout container, iterate over children and seed recursion.
        if (view instanceof ViewGroup) {

            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {

                View innerView = ((ViewGroup) view).getChildAt(i);

                setupUI(innerView);
            }
        }
    }
}
