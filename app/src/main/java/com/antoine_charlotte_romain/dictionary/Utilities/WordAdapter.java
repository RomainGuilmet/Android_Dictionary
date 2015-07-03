package com.antoine_charlotte_romain.dictionary.Utilities;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.antoine_charlotte_romain.dictionary.Business.Word;
import com.antoine_charlotte_romain.dictionary.Controllers.ListWordsActivity;
import com.antoine_charlotte_romain.dictionary.DataModel.DictionaryDataModel;
import com.antoine_charlotte_romain.dictionary.R;

import java.util.ArrayList;

/**
 * Created by summer1 on 30/06/2015.
 */
public class WordAdapter extends ArrayAdapter<Word> {

    private Context context;
    private int layoutResourceId;
    private ArrayList<Word> aw = null;
    private boolean selectedDictionary;
    private WordAdapterCallback callback;

    /**
     * This function creates a custom ArrayAdapter of words
     *
     * @param context
     * @param resource
     * @param data
     * @param selectedDictionary if true the row will show the headWord and its translation else it will show the headWord and the dictionary title instead
     */
    public WordAdapter(Context context, int resource, ArrayList<Word> data, boolean selectedDictionary) {
        super(context, resource, data);
        this.layoutResourceId = resource;
        this.context = context;
        this.aw = data;
        this.selectedDictionary = selectedDictionary;
    }

    @Override
    public int getCount() {

        return aw.size();
    }

    @Override
    public Word getItem(int position) {

        return aw.get(position);
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
        Word word = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_word, parent, false);
        }

        // Lookup view for data population
        TextView header = (TextView) convertView.findViewById(R.id.textHeader);
        TextView subItem = (TextView) convertView.findViewById(R.id.textSub);
        ImageButton menuButton = (ImageButton) convertView.findViewById(R.id.imageButtonWord);
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!callback.getOpen()) {
                    switch (v.getId()) {
                        case R.id.imageButtonWord:
                            PopupMenu popup = new PopupMenu(context, v);
                            popup.getMenuInflater().inflate(R.menu.context_menu_word, popup.getMenu());
                            popup.show();
                            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                @Override
                                public boolean onMenuItemClick(MenuItem item) {
                                    switch (item.getItemId()) {
                                        case R.id.modify:
                                            callback.modifyPressed(position);
                                            break;

                                        case R.id.delete:
                                            callback.deletePressed(position);
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
                else{
                    callback.showFloatingMenu(v);
                }
            }
        });

        // Populate the data into the template view using the data object
        header.setText(word.getHeadword());
        if (selectedDictionary) {
            subItem.setText(word.getTranslation());
        } else {
            DictionaryDataModel ddm = new DictionaryDataModel(context);
            subItem.setText((ddm.select(word.getDictionaryID())).getTitle());
        }

        // Return the completed view to render on screen
        return convertView;
    }

    public void setCallback(WordAdapterCallback callback){
        this.callback = callback;
    }

    public interface WordAdapterCallback {
        void deletePressed(int position);
        void modifyPressed(int position);
        boolean getOpen();
        void showFloatingMenu(View v);
    }

}
