package com.antoine_charlotte_romain.dictionary.Controllers;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import com.antoine_charlotte_romain.dictionary.Business.Dictionary;
import com.antoine_charlotte_romain.dictionary.DataModel.DictionaryDataModel;
import com.antoine_charlotte_romain.dictionary.R;
import java.util.ArrayList;
import android.support.v7.widget.Toolbar;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    public final static String EXTRA_DICTIONARY = "DictionaryID";
    private ArrayList<Dictionary> dictionaries;
    private ArrayList<Dictionary> dictionariesDisplay;
    private ArrayAdapter<Dictionary> adapter;
    private CoordinatorLayout rootLayout;
    private DictionaryDataModel ddm;
    private EditText searchField, input;
    private ListView listView;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.fragment_home,container,false);
        rootLayout = (CoordinatorLayout) v.findViewById(R.id.rootLayout);

        //Selecting all the dictionaries
        ddm = new DictionaryDataModel(getActivity());
        ddm.open();
        dictionaries = ddm.selectAll();
        dictionariesDisplay = new ArrayList<Dictionary>(dictionaries);

        //Creating the ListView and populating it
        listView = (ListView) v.findViewById(R.id.dictionary_list);
        adapter = new ArrayAdapter<Dictionary>(getActivity(), android.R.layout.simple_list_item_1 , dictionariesDisplay);
        listView.setAdapter(adapter);

        //Configuring the ListView listener
        listView.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        /*Intent intent = new Intent(HomeFragment.this.getActivity(),DictionaryActivity.class);
                        intent.putExtra(EXTRA_DICTIONARY, dictionariesDisplay.get(position).getTitle());
                        startActivity(intent);*/
                    }
                }
        );

        /*listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            int mLastFirstVisibleItem = 0;

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {}

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (view.getId() == listView.getId()) {
                    final int currentFirstVisibleItem = listView.getFirstVisiblePosition();

                    if (currentFirstVisibleItem > mLastFirstVisibleItem) {
                        System.out.println("En Bas");
                    } else if (currentFirstVisibleItem < mLastFirstVisibleItem) {
                        System.out.println("En Haut");
                    }

                    mLastFirstVisibleItem = currentFirstVisibleItem;
                }
            }

        });*/

        //Creating the EditText for searching inside the dictionaries list
        searchField = (EditText) v.findViewById(R.id.search_field);
        searchField.setMovementMethod(new ScrollingMovementMethod());
        searchField.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s)
            {
                dictionariesDisplay.clear();
                String search= s.toString();
                for(int i = 0 ; i < dictionaries.size() ; i++)
                {
                    if(dictionaries.get(i).getTitle().toLowerCase().contains(search.toLowerCase()))
                        dictionariesDisplay.add(dictionaries.get(i));
                }
                adapter.notifyDataSetChanged();
            };
        });

        //Creating the Floating Action Button to add a dictionary
        FloatingActionButton addButton = (FloatingActionButton) v.findViewById(R.id.add_button);
        addButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v)
            {
                LinearLayout layout = new LinearLayout(getActivity());
                layout.setOrientation(LinearLayout.VERTICAL);
                layout.setPadding(60, 30, 60, 0);

                input = new EditText(getActivity());
                input.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                input.setHint(R.string.dictionary_name);
                layout.addView(input);

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
                alertDialog.setTitle(R.string.add_dictionary);
                alertDialog.setView(layout);

                alertDialog.setPositiveButton(R.string.add_button,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Dictionary d = new Dictionary(input.getText().toString());
                                if (ddm.insert(d) == 1) {
                                    dictionaries.add(d);
                                    dictionariesDisplay.add(d);
                                    adapter.notifyDataSetChanged();
                                    searchField.setText("");

                                    Snackbar.make(rootLayout, R.string.dictionary_added, Snackbar.LENGTH_LONG).setAction(R.string.close_button, new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {

                                        }
                                    }).show();
                                }
                                else {
                                    Snackbar.make(rootLayout, R.string.dictionary_not_added, Snackbar.LENGTH_LONG).setAction(R.string.close_button, new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {

                                        }
                                    }).show();
                                }
                                dialog.cancel();
                            }
                        });


                alertDialog.setNegativeButton(R.string.cancel_button,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

                alertDialog.setNeutralButton(R.string.csv_button,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });

                alertDialog.show();
            }

        });
        return v;
    }


}
