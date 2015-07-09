package com.antoine_charlotte_romain.dictionary.Controllers;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.antoine_charlotte_romain.dictionary.Business.SearchDate;
import com.antoine_charlotte_romain.dictionary.DataModel.DictionaryDataModel;
import com.antoine_charlotte_romain.dictionary.DataModel.SearchDateDataModel;
import com.antoine_charlotte_romain.dictionary.R;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * TODO réfléchir à limiter les inserts (un meme mot uniquement une fois par heure)
 */
public class HistoryFragment extends Fragment implements SearchDateAdapter.SearchDateAdapterCallback{

    private View thisView;
    private View loading;
    private EditText historySearch;
    private ListView listViewHistory;

    private SearchDateDataModel sddm;
    private ArrayList<SearchDate> mySearchDateList;
    private SearchDateAdapter myAdapter;

    private boolean loadingMore;
    private int historyLimit;
    private int historyOffset;
    private int actualListSize;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        thisView = inflater.inflate(R.layout.fragment_history,container,false);

        historySearch = (EditText) thisView.findViewById(R.id.historySearch);
        listViewHistory = (ListView) thisView.findViewById(R.id.listViewHistory);
        loading = getActivity().getLayoutInflater().inflate(R.layout.loading, null);

        initListView();

        return thisView;
    }

    /**
     * Function that load all the search history on the database and show it on the listView
     */
    private void initListView(){
        sddm = new SearchDateDataModel(getActivity());
        historyLimit = 10;
        historyOffset = 0;

        if(listViewHistory.getFooterViewsCount() == 0) {
            listViewHistory.addFooterView(loading);
            loading.setPadding(0, 0, 0, 90);
        }

        mySearchDateList = sddm.selectAll(historyLimit, historyOffset);

        myAdapter = new SearchDateAdapter(getActivity(), R.layout.row_history, mySearchDateList);
        myAdapter.setCallback(this);

        listViewHistory.setAdapter(myAdapter);
        listViewHistory.setTextFilterEnabled(true);

        listViewHistory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                seeWord(position);
            }
        });

        listViewHistory.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                int lastInScreen = firstVisibleItem + visibleItemCount;
                if ((lastInScreen == totalItemCount) && !(loadingMore)) {
                    Thread thread = new Thread(null, loadMoreHistory);
                    thread.start();
                }
            }
        });

        historySearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s != null) {
                    mySearchDateList.clear();
                    ArrayList<SearchDate> tempList;

                    if (s.length() > 0) {
                        String search = s.toString();
                        tempList = sddm.select(search);
                    } else {
                        historyOffset = 0;
                        tempList = sddm.selectAll(historyLimit, historyOffset);
                    }

                    for (int i = 0; i < tempList.size(); i++) {
                        mySearchDateList.add(tempList.get(i));
                    }
                    myAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    /**
     * This function launches the view details of a word and allows to modify it
     * @param position the position in the listView of the word the user want to see more details or to modify
     */
    public void seeWord(int position){
        Intent wordDetailIntent = new Intent(getActivity(), WordActivity.class);

        wordDetailIntent.putExtra(MainActivity.EXTRA_WORD, mySearchDateList.get(position).getWord());
        DictionaryDataModel ddm = new DictionaryDataModel(getActivity());
        wordDetailIntent.putExtra(MainActivity.EXTRA_DICTIONARY, ddm.select(mySearchDateList.get(position).getWord().getDictionaryID()));

        startActivity(wordDetailIntent);
    }

    /**
     * This thread is launch when the user scroll to the end of the list and it load more history
     */
    private Runnable loadMoreHistory = new Runnable(){
        @Override
        public void run() {
            loadingMore = true;
            ArrayList<SearchDate> tempList = new ArrayList<>();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            historyOffset += 10;
            sddm = new SearchDateDataModel(getActivity());
            if(historySearch.getText().toString().length() == 0) {
                tempList = sddm.selectAll(historyLimit, historyOffset);
            }

            actualListSize = mySearchDateList.size();
            for(int i = 0; i < tempList.size(); i++){
                mySearchDateList.add(tempList.get(i));
            }
            getActivity().runOnUiThread(returnRes);
        }
    };

    /**
     * This thread tell the adapter that the more words were loaded
     */
    private Runnable returnRes = new Runnable() {
        @Override
        public void run() {
            myAdapter.notifyDataSetChanged();
            loadingMore = false;
            if(actualListSize == mySearchDateList.size()){
                listViewHistory.removeFooterView(loading);
            }
        }
    };

}
