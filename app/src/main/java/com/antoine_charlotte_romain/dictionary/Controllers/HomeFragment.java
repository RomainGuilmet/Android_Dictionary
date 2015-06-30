package com.antoine_charlotte_romain.dictionary.Controllers;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.util.TypedValue;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;

import com.antoine_charlotte_romain.dictionary.Business.Dictionary;
import com.antoine_charlotte_romain.dictionary.DataModel.DictionaryDataModel;
import com.antoine_charlotte_romain.dictionary.R;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    public final static String EXTRA_DICTIONARY = "SelectedDictionary";
    private final int CONTEXT_MENU_RENAME = 0;
    private final int CONTEXT_MENU_DELETE = 1;
    private View v;
    private ArrayList<Dictionary> dictionaries;
    private ArrayList<Dictionary> dictionariesDisplay;
    private ArrayAdapter<Dictionary> adapter;
    private CoordinatorLayout rootLayout;
    private DictionaryDataModel ddm;
    private EditText searchField, input;
    private GridView gridView;
    private boolean undo;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        v = inflater.inflate(R.layout.fragment_home,container,false);
        rootLayout = (CoordinatorLayout) v.findViewById(R.id.rootLayout);

        initData();
        initGridView();
        initEditText();
        initFloatingActionButton();

        return v;
    }


    private void initData()
    {
        //Selecting all the dictionaries
        ddm = new DictionaryDataModel(getActivity());
        ddm.open();
        dictionaries = ddm.selectAll();
        dictionariesDisplay = new ArrayList<Dictionary>(dictionaries);

    }

    private void initGridView()
    {
        //Creating the ListView and populating it
        gridView = (GridView) v.findViewById(R.id.dictionary_list);
        adapter = new DictionaryAdapter(getActivity(), android.R.layout.simple_list_item_1 , dictionariesDisplay);
        gridView.setAdapter(adapter);

        //Configuring the ListView listener
        gridView.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        /*Intent intent = new Intent(HomeFragment.this.getActivity(),DictionaryActivity.class);
                        intent.putExtra(EXTRA_DICTIONARY, dictionariesDisplay.get(position));
                        startActivity(intent);*/
                    }
                }
        );

        //Adding the context menu on each rows
        registerForContextMenu(gridView);
    }

    private void initEditText()
    {
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
    }

    private void initFloatingActionButton()
    {
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
    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        AdapterView.AdapterContextMenuInfo  info = (AdapterView.AdapterContextMenuInfo) menuInfo;

        String title = (adapter.getItem(info.position)).getTitle();
        menu.setHeaderTitle(title);

        menu.add(Menu.NONE, CONTEXT_MENU_RENAME, Menu.NONE, R.string.rename);
        menu.add(Menu.NONE, CONTEXT_MENU_DELETE, Menu.NONE, R.string.delete);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        final Dictionary d = dictionariesDisplay.get(info.position);
        switch (item.getItemId()) {
            case CONTEXT_MENU_DELETE:
                dictionariesDisplay.remove(d);
                adapter.notifyDataSetChanged();
                undo = false;
                Snackbar.make(rootLayout, d.getTitle() + getString(R.string.deleted), Snackbar.LENGTH_LONG).setAction(R.string.undo_button, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dictionariesDisplay.add(info.position, d);
                        adapter.notifyDataSetChanged();
                        undo = true;
                    }
                }).show();
                Thread timer = new Thread(){
                    public void run(){
                        try {
                            sleep(4000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        finally {
                            if(!undo) {
                                dictionaries.remove(d);
                                ddm.delete(d.getId());
                            }
                        }
                    }
                };
                timer.start();
                return true;
            case CONTEXT_MENU_RENAME:
                LinearLayout layout = new LinearLayout(getActivity());
                layout.setOrientation(LinearLayout.VERTICAL);
                layout.setPadding(60, 30, 60, 0);

                input = new EditText(getActivity());
                input.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                input.setText(d.getTitle());
                input.selectAll();
                layout.addView(input);

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
                alertDialog.setTitle(R.string.rename_dictionary);
                alertDialog.setView(layout);

                alertDialog.setPositiveButton(R.string.rename_button,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                final String title = d.getTitle();
                                d.setTitle(input.getText().toString());
                                if (ddm.update(d) == 1) {
                                    adapter.notifyDataSetChanged();
                                    searchField.setText("");

                                    Snackbar.make(rootLayout, R.string.dictionary_renamed, Snackbar.LENGTH_LONG).setAction(R.string.close_button, new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {

                                        }
                                    }).show();
                                }
                                else {
                                    d.setTitle(title);
                                    Snackbar.make(rootLayout, R.string.dictionary_not_renamed, Snackbar.LENGTH_LONG).setAction(R.string.close_button, new View.OnClickListener() {
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

                alertDialog.show();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }
}
