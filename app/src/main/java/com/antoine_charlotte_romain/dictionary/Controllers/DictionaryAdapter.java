package com.antoine_charlotte_romain.dictionary.Controllers;

import com.antoine_charlotte_romain.dictionary.Business.Dictionary;
import com.antoine_charlotte_romain.dictionary.R;

import android.content.Context;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by summer2 on 29/06/2015.
 */
public class DictionaryAdapter extends ArrayAdapter<Dictionary>{

    private Context context;
    private int layoutResourceId;
    private ArrayList<Dictionary> data = null;
    private DictionaryAdapterCallback callback;

    public DictionaryAdapter(Context context, int layoutResourceId, ArrayList<Dictionary> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Dictionary dictionary = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.dico_grid_view, parent, false);
        }

        // Lookup view for data population
        TextView title = (TextView) convertView.findViewById(R.id.dictionary_title);
        ImageButton menuButton = (ImageButton) convertView.findViewById(R.id.dico_more_button);
        menuButton.setColorFilter(R.color.textColor, PorterDuff.Mode.MULTIPLY);
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.dico_more_button:
                        PopupMenu popup = new PopupMenu(context, v);
                        popup.getMenuInflater().inflate(R.menu.context_menu_dictionary, popup.getMenu());
                        popup.show();
                        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                switch (item.getItemId()) {
                                    case R.id.open:
                                        callback.read(position);
                                        break;

                                    case R.id.rename:
                                        callback.update(position);
                                        break;

                                    case R.id.delete:
                                        callback.delete(position);
                                        break;

                                    default:
                                        break;
                                }
                                return true;
                            }
                        });
                        break;

                    default:
                        break;
                }
            }
        });

        // Populate the data into the template view using the data object
        title.setText(dictionary.getTitle());

        // Return the completed view to render on screen
        return convertView;
    }



    public void setCallback(DictionaryAdapterCallback callback){
        this.callback = callback;
    }

    public interface DictionaryAdapterCallback {
        void delete(int position);
        void update(int position);
        void read(int position);
    }

}
