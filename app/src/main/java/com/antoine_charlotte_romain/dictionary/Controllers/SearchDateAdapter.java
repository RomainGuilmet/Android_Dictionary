package com.antoine_charlotte_romain.dictionary.Controllers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.antoine_charlotte_romain.dictionary.Business.SearchDate;
import com.antoine_charlotte_romain.dictionary.R;

import java.util.ArrayList;

/**
 * Created by summer1 on 30/06/2015.
 */
public class SearchDateAdapter extends ArrayAdapter<SearchDate> {

    private Context context;
    private int layoutResourceId;
    private ArrayList<SearchDate> asd = null;
    private SearchDateAdapterCallback callback;

    /**
     * This function creates a custom ArrayAdapter of words
     *
     * @param context
     * @param resource
     * @param data
     */
    public SearchDateAdapter(Context context, int resource, ArrayList<SearchDate> data) {
        super(context, resource, data);
        this.layoutResourceId = resource;
        this.context = context;
        this.asd = data;
    }

    @Override
    public int getCount() {

        return asd.size();
    }

    @Override
    public SearchDate getItem(int position) {

        return asd.get(position);
    }

    @Override
    public long getItemId(int position) {

        return position;
    }

    /**
     * This function is used to show the word in the listView each word in a custom layout "row_word"
     *
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        // Get the data item for this position
        SearchDate sd = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_history, parent, false);
        }

        // Lookup view for data population
        TextView textHeadword = (TextView) convertView.findViewById(R.id.textHeadword);
        TextView textTranslation = (TextView) convertView.findViewById(R.id.textTranslation);
        TextView textDate  = (TextView) convertView.findViewById(R.id.textDate);

        // Populate the data into the template view using the data object
        textHeadword.setText(sd.getWord().getHeadword());
        textTranslation.setText(sd.getWord().getTranslation());
        textDate.setText(sd.getDate());

        // Return the completed view to render on screen
        return convertView;
    }

    public void setCallback(SearchDateAdapterCallback callback){
        this.callback = callback;
    }

    public interface SearchDateAdapterCallback {

    }

}
