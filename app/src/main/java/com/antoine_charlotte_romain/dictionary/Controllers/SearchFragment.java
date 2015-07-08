package com.antoine_charlotte_romain.dictionary.Controllers;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioGroup;

import com.antoine_charlotte_romain.dictionary.Business.Dictionary;
import com.antoine_charlotte_romain.dictionary.DataModel.DictionaryDataModel;
import com.antoine_charlotte_romain.dictionary.R;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchFragment extends Fragment {

    private final String target = "Target dictionary : ";

    private View thisView;

    private Dictionary selectedDictionary;

    private EditText beginningText;
    private EditText containsText;
    private EditText endText;
    private EditText targetDictionary;
    private FloatingActionButton searchFloatingButton;
    private MenuItem searchTabButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        thisView = inflater.inflate(R.layout.fragment_search,container,false);

        ((MainActivity)getActivity()).setSearchFragment(this);

        Intent intent = getActivity().getIntent();
        selectedDictionary = (Dictionary)intent.getSerializableExtra(MainActivity.EXTRA_DICTIONARY);

        beginningText = ((EditText) thisView.findViewById(R.id.beginString));
        containsText = ((EditText) thisView.findViewById(R.id.middleString));
        endText = ((EditText) thisView.findViewById(R.id.endString));
        targetDictionary = ((EditText) thisView.findViewById(R.id.targetDico));
        searchFloatingButton = ((FloatingActionButton) thisView.findViewById(R.id.searchFloatingButton));

        setHasOptionsMenu(true);

        return thisView;
    }

    @Override
    public void onResume() {
        super.onResume();

        if(selectedDictionary == null) {
            targetDictionary.setText(target + MainActivity.ALL_DICO);
        }
        else {
            targetDictionary.setText(target + selectedDictionary.getTitle());
        }

        beginningText.setText("");
        containsText.setText("");
        endText.setText("");

        targetDictionary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayDictionaries(v);
            }
        });

        searchFloatingButton.setVisibility(View.GONE);

        beginningText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable arg0) {
                boolean isReady = beginningText.getText().toString().length() > 0;
                searchTabButton.setVisible(isReady);
                if(isReady){
                    searchFloatingButton.setVisibility(View.VISIBLE);
                }
                else {
                    searchFloatingButton.setVisibility(View.GONE);
                }
            }

        });

        containsText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable arg0) {
                boolean isReady = containsText.getText().toString().length() > 0;
                searchTabButton.setVisible(isReady);
                if(isReady){
                    searchFloatingButton.setVisibility(View.VISIBLE);
                }
                else {
                    searchFloatingButton.setVisibility(View.GONE);
                }
            }

        });

        endText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable arg0) {
                boolean isReady = endText.getText().toString().length() > 0;
                searchTabButton.setVisible(isReady);
                if(isReady){
                    searchFloatingButton.setVisibility(View.VISIBLE);
                }
                else {
                    searchFloatingButton.setVisibility(View.GONE);
                }
            }

        });
    }

    /**
     * This function creates the buttons on the toolBar
     * @param menu
     * @return
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        menu.clear();
        menuInflater.inflate(R.menu.menu_search_fragment, menu);
        searchTabButton = menu.findItem(R.id.action_search);
        searchTabButton.setVisible(false);
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
            case R.id.action_search:
                advancedSearch(thisView);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void displayDictionaries(View v){
        DictionaryDataModel ddm = new DictionaryDataModel(this.getActivity());
        ArrayList<Dictionary> dico = ddm.select();
        String[] nameDico = new String[dico.size()+1];
        nameDico[0] = MainActivity.ALL_DICO;
        for (int i=0; i<dico.size(); i++){
            nameDico[i+1] = dico.get(i).getTitle();
        }
        final String[] names = nameDico.clone();

        AlertDialog.Builder ad = new AlertDialog.Builder(getActivity())
                .setTitle(R.string.advsearch_choose_dico)
                .setItems(names, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        targetDictionary.setText(target + names[which]);
                    }
                })
                .setNegativeButton(R.string.advsearch_returnString, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert);
        AlertDialog alert = ad.create();
        alert.show();
    }

    public void advancedSearch(View v){
        Intent intent = new Intent(getActivity(), AdvancedSearchResultActivity.class);

        intent.putExtra(MainActivity.EXTRA_BEGIN_STRING, beginningText.getText().toString());
        intent.putExtra(MainActivity.EXTRA_MIDDLE_STRING, containsText.getText().toString());
        intent.putExtra(MainActivity.EXTRA_END_STRING, endText.getText().toString());

        // Let's see if the search has to be done on headword or whole word
        switch (((RadioGroup)thisView.findViewById(R.id.boutonsradio)).getCheckedRadioButtonId()) {
            case R.id.headword:
                intent.putExtra(MainActivity.EXTRA_HEAD_OR_WHOLE, "head");
                break;
            case R.id.whole:
                intent.putExtra(MainActivity.EXTRA_HEAD_OR_WHOLE, "whole");
                break;
        }

        // Let's get the targeted dictionary
        String temp = targetDictionary.getText().toString();
        String dico = temp.replace(target, "");
        intent.putExtra(MainActivity.EXTRA_DICTIONARY, dico);

        startActivity(intent);
    }
}
