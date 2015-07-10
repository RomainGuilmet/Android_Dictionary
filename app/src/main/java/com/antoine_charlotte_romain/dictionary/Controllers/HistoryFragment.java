package com.antoine_charlotte_romain.dictionary.Controllers;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

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

        setHasOptionsMenu(true);

        return thisView;
    }

    @Override
    public void onCreateOptionsMenu(Menu m, MenuInflater inflater)
    {
        inflater.inflate(R.menu.menu_history, m);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId())
        {
            case R.id.action_clear_history:
                clearHistory();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
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
     * This function is used to clear the search history of the app
     */
    private void clearHistory(){
        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        alert.setMessage(getString(R.string.clearHistory) + " ?");
        alert.setPositiveButton(getString(R.string.delete), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                Toast.makeText(getActivity(), getString(R.string.historyCleared), Toast.LENGTH_SHORT).show();
                sddm.deleteAll();
                initListView();
            }
        });

        alert.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });

        alert.show();
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
