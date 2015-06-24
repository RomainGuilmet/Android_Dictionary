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
        final View vue = inflater.inflate(R.layout.fragment_search,container,false);

        AlertDialog.Builder ad = new AlertDialog.Builder(getActivity())
                .setTitle("try")
                .setMessage("My message !")
                .setNegativeButton("Retour", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert);
        AlertDialog alert = ad.create();
        alert.show();

        // Listener on the ChooseButton
        final Button chooseButton = (Button) vue.findViewById(R.id.dicoButton);
        System.out.println(chooseButton.getText());
        chooseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("NOOOOOOOOOOOOO");
                //displayDictionaries(v);
            }
        });

        System.out.println("end");

        return inflater.inflate(R.layout.fragment_search,container,false);
    }

    public void displayDictionaries(View v){
        System.out.print("YEAH");
        DictionaryDataModel ddm = new DictionaryDataModel(getActivity());
        if (ddm != null)
            System.out.println("YEAH");
        else
            System.out.println("NOOOOOOOOO");
        ArrayList<Dictionary> dico = ddm.select();
        ArrayList<String> nameDico = new ArrayList<String>();
        for (Dictionary d : dico){
            nameDico.add(d.getTitle());
        }
        final String[] names = (String[])nameDico.toArray();

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
