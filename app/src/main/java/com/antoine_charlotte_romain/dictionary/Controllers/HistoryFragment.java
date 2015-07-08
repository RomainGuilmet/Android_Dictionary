package com.antoine_charlotte_romain.dictionary.Controllers;


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
import com.antoine_charlotte_romain.dictionary.DataModel.SearchDateDataModel;
import com.antoine_charlotte_romain.dictionary.R;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * TODO loadMore
 * TODO lien vers les détails du mot au click sur un mot
 * TODO réfléchir à limiter les inserts (un meme mot uniquement une fois par heure)
 */
public class HistoryFragment extends Fragment implements SearchDateAdapter.SearchDateAdapterCallback{

    private View thisView;

    private EditText historySearch;
    private ListView listViewHistory;

    private SearchDateDataModel sddm;
    private ArrayList<SearchDate> mySearchDateList;
    private SearchDateAdapter myAdapter;
    private boolean loadingMore;
    private int historyLimit;
    private int historyOffset;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        thisView = inflater.inflate(R.layout.fragment_history,container,false);

        historySearch = (EditText) thisView.findViewById(R.id.historySearch);
        listViewHistory = (ListView) thisView.findViewById(R.id.listViewHistory);

        initListView();

        return thisView;
    }

    private void initListView(){
        sddm = new SearchDateDataModel(getActivity());
        historyLimit = 10;
        historyOffset = 0;

        mySearchDateList = sddm.selectAll(historyLimit, historyOffset);

        myAdapter = new SearchDateAdapter(getActivity(), android.R.layout.simple_list_item_1, mySearchDateList);
        myAdapter.setCallback(this);

        listViewHistory.setAdapter(myAdapter);
        listViewHistory.setTextFilterEnabled(true);

        listViewHistory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //read(position);
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
                    /*Thread thread = new Thread(null, loadMoreListWords);
                    thread.start();*/
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
                    }
                    else {
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

}
