package com.antoine_charlotte_romain.dictionary.Controllers;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

    private static final String ALL_DICO = "All";

    private View thisView;
    private Dictionary selectedDictionary;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        thisView = inflater.inflate(R.layout.fragment_search,container,false);

        ((MainActivity)getActivity()).setSearchFragment(this);

        Intent intent = getActivity().getIntent();
        selectedDictionary = (Dictionary)intent.getSerializableExtra(MainActivity.EXTRA_DICTIONARY);

        if(selectedDictionary == null) {
            ((Button) thisView.findViewById(R.id.dicoButton)).setText(ALL_DICO);
        }
        else {
            ((Button) thisView.findViewById(R.id.dicoButton)).setText(selectedDictionary.getTitle());
        }

        return thisView;
    }

    @Override
    public void onResume() {
        super.onResume();

        ((EditText) thisView.findViewById(R.id.beginString)).setText("");
        ((EditText) thisView.findViewById(R.id.middleString)).setText("");
        ((EditText) thisView.findViewById(R.id.endString)).setText("");
    }

    public void displayDictionaries(View v){
        DictionaryDataModel ddm = new DictionaryDataModel(this.getActivity());
        ArrayList<Dictionary> dico = ddm.select();
        String[] nameDico = new String[dico.size()+1];
        nameDico[0] = ALL_DICO;
        for (int i=0; i<dico.size(); i++){
            nameDico[i+1] = dico.get(i).getTitle();
        }
        final String[] names = nameDico.clone();

        AlertDialog.Builder ad = new AlertDialog.Builder(getActivity())
                .setTitle(R.string.advsearch_choose_dico)
                .setItems(names, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        ((Button)thisView.findViewById(R.id.dicoButton)).setText(names[which]);
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

        // Let's take the string indicated by the user
        EditText beginS = (EditText) thisView.findViewById(R.id.beginString);
        EditText middleS = (EditText) thisView.findViewById(R.id.middleString);
        EditText endS = (EditText) thisView.findViewById(R.id.endString);

        String bString = "";
        String mString = "";
        String eString = "";
        if (beginS.getText() != null)
            bString = beginS.getText().toString();
        if (middleS.getText() != null)
            mString = middleS.getText().toString();
        if (beginS.getText() != null)
            eString = endS.getText().toString();

        intent.putExtra(MainActivity.EXTRA_BEGIN_STRING, bString);
        intent.putExtra(MainActivity.EXTRA_MIDDLE_STRING, mString);
        intent.putExtra(MainActivity.EXTRA_END_STRING, eString);

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
        String dico = ((Button)thisView.findViewById(R.id.dicoButton)).getText().toString();
        intent.putExtra(MainActivity.EXTRA_DICTIONARY, dico);

        startActivity(intent);
    }
}
