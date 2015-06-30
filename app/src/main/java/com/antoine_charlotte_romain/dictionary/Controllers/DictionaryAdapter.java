package com.antoine_charlotte_romain.dictionary.Controllers;

import com.antoine_charlotte_romain.dictionary.Business.Dictionary;
import com.antoine_charlotte_romain.dictionary.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by summer2 on 29/06/2015.
 */
public class DictionaryAdapter extends ArrayAdapter<Dictionary>{

    Context context;
    int layoutResourceId;
    ArrayList<Dictionary> data = null;

    public DictionaryAdapter(Context context, int layoutResourceId, ArrayList<Dictionary> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Dictionary dictionary = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.dico_grid_view, parent, false);
        }

        // Lookup view for data population
        TextView title = (TextView) convertView.findViewById(R.id.dictionary_title);

        // Populate the data into the template view using the data object
        title.setText(dictionary.getTitle());

        // Return the completed view to render on screen
        return convertView;
    }

}
