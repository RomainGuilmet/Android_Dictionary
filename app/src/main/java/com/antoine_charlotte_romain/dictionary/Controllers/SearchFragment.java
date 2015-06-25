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

import com.antoine_charlotte_romain.dictionary.Business.Dictionary;
import com.antoine_charlotte_romain.dictionary.DataModel.DictionaryDataModel;
import com.antoine_charlotte_romain.dictionary.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchFragment extends Fragment {
    private View thisView;

    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        thisView = inflater.inflate(R.layout.fragment_search,container,false);

        ((MainActivity)getActivity()).setSearchFragment(this);

        return thisView;
    }

    public void displayDictionaries(View v){
        DictionaryDataModel ddm = new DictionaryDataModel(this.getActivity());
        ArrayList<Dictionary> dico = ddm.select();
        String[] nameDico = new String[dico.size()];
        for (int i=0; i<dico.size(); i++){
            nameDico[i] = dico.get(i).getTitle();
        }
        final String[] names = nameDico.clone();

        AlertDialog.Builder ad = new AlertDialog.Builder(getActivity())
                .setTitle("@string/choose_dico")
                .setItems(names, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        ((Button)thisView.findViewById(R.id.dicoButton)).setText(names[which]);
                    }
                })
                .setNegativeButton("Retour", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert);
        AlertDialog alert = ad.create();
        alert.show();
    }
}
