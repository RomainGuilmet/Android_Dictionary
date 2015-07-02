package com.antoine_charlotte_romain.dictionary.Controllers;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.util.TypedValue;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.antoine_charlotte_romain.dictionary.Business.Dictionary;
import com.antoine_charlotte_romain.dictionary.DataModel.DictionaryDataModel;
import com.antoine_charlotte_romain.dictionary.R;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment implements DictionaryAdapter.DictionaryAdapterCallback {


    /*---------------------------------------------------------
    *                        CONSTANTS
    *---------------------------------------------------------*/

    public final static String EXTRA_DICTIONARY = "SelectedDictionary";
    private final int CONTEXT_MENU_READ = 0;
    private final int CONTEXT_MENU_UPDATE = 1;
    private final int CONTEXT_MENU_DELETE = 2;

    /*---------------------------------------------------------
    *                     INSTANCE VARIABLES
    *---------------------------------------------------------*/

    /**
     * The view corresponding to this fragment.
     *
     * @see MainActivity
     */
    private View v;

    /**
     * Initial dictionary list. Contains all the dictionary.
     */
    private ArrayList<Dictionary> dictionaries;

    /**
     * List of displayed dictionaries according to the research performed
     */
    private ArrayList<Dictionary> dictionariesDisplay;

    /**
     * Allow to display a list of Objects.
     */
    private GridView gridView;

    /**
     * Custom ArrayAdapter to manage the different rows of the grid
     */
    private DictionaryAdapter adapter;

    /**
     * Used to display the snackBars
     */
    private CoordinatorLayout rootLayout;

    /**
     * Used to communicating with the database
     */
    private DictionaryDataModel ddm;

    /**
     * Used to handle a undo action after deleting a dictionary
     */
    private boolean undo;

    private EditText searchBox, nameBox;


    /*---------------------------------------------------------
    *                       CONSTRUCTORS
    *---------------------------------------------------------*/

    public HomeFragment() {
        // Required empty public constructor
    }



    /*---------------------------------------------------------
    *                     INSTANCE METHODS
    *---------------------------------------------------------*/

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


    /**
     * Initialising the data model and selecting all the dictionaries
     */
    private void initData()
    {
        ddm = new DictionaryDataModel(getActivity());
        ddm.open();
        //for (int i = 0; i < 10; i++) {ddm.insert(new Dictionary("Dictionary " + i));}
        dictionaries = ddm.selectAll();
        dictionariesDisplay = new ArrayList<Dictionary>(dictionaries);

    }

    /**
     * Initialising the GridView to display the dictionary list and making its clickables
     */
    private void initGridView()
    {
        //Creating the GridView and populating it
        gridView = (GridView) v.findViewById(R.id.dictionary_list);
        adapter = new DictionaryAdapter(getActivity(), android.R.layout.simple_list_item_1 , dictionariesDisplay);
        adapter.setCallback(this);
        gridView.setAdapter(adapter);
        gridView.setDrawSelectorOnTop(true);

        //Configuring the ListView listener
        gridView.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        read(position);
                    }
                }
        );

        //Adding the context menu on each rows
        registerForContextMenu(gridView);

        Animation anim = AnimationUtils.loadAnimation(getActivity(), R.anim.fly_in_from_center);
        gridView.setAnimation(anim);
        anim.start();

    }

    /**
     * Initialising the search box to dynamically researching on the dictionary list
     */
    private void initEditText()
    {
        //Creating the EditText for searching inside the dictionaries list
        searchBox = (EditText) v.findViewById(R.id.search_field);
        searchBox.setMovementMethod(new ScrollingMovementMethod());
        searchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                dictionariesDisplay.clear();
                String search = s.toString();
                for (int i = 0; i < dictionaries.size(); i++) {
                    if (dictionaries.get(i).getTitle().toLowerCase().contains(search.toLowerCase()))
                        dictionariesDisplay.add(dictionaries.get(i));
                }
                adapter.notifyDataSetChanged();
            };
        });
    }

    /**
     * Creating the Floating Action Button to add a dictionary through a dialog window
     */
    private void initFloatingActionButton()
    {
        FloatingActionButton addButton = (FloatingActionButton) v.findViewById(R.id.add_button);
        addButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                create();
            }

        });
    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        AdapterView.AdapterContextMenuInfo  info = (AdapterView.AdapterContextMenuInfo) menuInfo;

        String title = (adapter.getItem(info.position)).getTitle();
        menu.setHeaderTitle(title);

        menu.add(Menu.NONE, CONTEXT_MENU_READ, Menu.NONE, R.string.open);
        menu.add(Menu.NONE, CONTEXT_MENU_UPDATE, Menu.NONE, R.string.rename);
        menu.add(Menu.NONE, CONTEXT_MENU_DELETE, Menu.NONE, R.string.delete);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case CONTEXT_MENU_READ:
                read(info.position);
                return true;
            case CONTEXT_MENU_UPDATE:
                update(info.position);
                return true;
            case CONTEXT_MENU_DELETE:
                delete(info.position);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }


    /**
     * Method which allows user to create a dictionary with a unique name
     */
    public void create()
    {
        //Creating the dialog layout
        LinearLayout layout = new LinearLayout(getActivity());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(60, 30, 60, 0);

        //Creating the EditText to type the dictionary name
        nameBox = new EditText(getActivity());
        nameBox.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        nameBox.setHint(R.string.dictionary_name);
        nameBox.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        nameBox.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI);

        //Adding the EditText to the layout
        layout.addView(nameBox);

        //Creating the dialog builder
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.add_dictionary);

        //Adding the layout to the dialog
        builder.setView(layout);

        //Dialog positive action
        builder.setPositiveButton(R.string.add_button,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Dictionary d = new Dictionary(nameBox.getText().toString());
                        if (ddm.insert(d) == 1) {
                            dictionaries.add(d);
                            dictionariesDisplay.add(d);
                            adapter.notifyDataSetChanged();
                            searchBox.setText("");

                            Snackbar.make(rootLayout, R.string.dictionary_added, Snackbar.LENGTH_LONG).setAction(R.string.close_button, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                }
                            }).show();
                        } else {
                            Snackbar.make(rootLayout, R.string.dictionary_not_added, Snackbar.LENGTH_LONG).setAction(R.string.close_button, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                }
                            }).show();
                        }
                        dialog.cancel();
                    }
                });

        //Dialog negative action
        builder.setNegativeButton(R.string.cancel_button,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        builder.setNeutralButton(R.string.csv_button,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });

        //Creating the dialog and opening the keyboard
        final AlertDialog alertDialog = builder.create();
        alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

        //Listening the keyboard to handle a "Done" action
        nameBox.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                //Simulating a positive button click. The positive action is executed.
                alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).performClick();
                return true;
            }
        });

        alertDialog.show();
    }

    /**
     * Method which allows user to open a dictionary. It is Redirecting to the ... Activity.
     *
     * @param position position of the dictionary to read in the dictionariesDisplay list.
     */
    public void read(int position)
    {

        Dictionary d = dictionariesDisplay.get(position);
        System.out.println("read " + d.getTitle()) ;
        /*Intent intent = new Intent(HomeFragment.this.getActivity(),DictionaryActivity.class);
        intent.putExtra(EXTRA_DICTIONARY, d);
        startActivity(intent);*/
    }

    /**
     * Method which allows user to rename a dictionary with a unique name.
     *
     * @param position position of the dictionary to update in the dictionariesDisplay list.
     */
    public void update(int position)
    {
        final Dictionary d = dictionariesDisplay.get(position);

        LinearLayout layout = new LinearLayout(getActivity());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(60, 30, 60, 0);

        nameBox = new EditText(getActivity());
        nameBox.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        nameBox.setText(d.getTitle());
        nameBox.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        nameBox.selectAll();
        layout.addView(nameBox);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.rename_dictionary);
        builder.setView(layout);


        builder.setPositiveButton(R.string.rename_button,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        final String title = d.getTitle();
                        d.setTitle(nameBox.getText().toString());
                        if (ddm.update(d) == 1) {
                            adapter.notifyDataSetChanged();
                            searchBox.setText("");

                            Snackbar.make(rootLayout, R.string.dictionary_renamed, Snackbar.LENGTH_LONG).setAction(R.string.close_button, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                }
                            }).show();
                        } else {
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

        builder.setNegativeButton(R.string.cancel_button,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        //Creating the dialog and opening the keyboard
        final AlertDialog alertDialog = builder.create();
        alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

        //Listening the keyboard to handle a "Done" action
        nameBox.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                //Simulating a positive button click. The positive action is executed.
                alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).performClick();
                return true;
            }
        });

        alertDialog.show();

    }

    /**
     * Method which allows user to delete a dictionary.
     *
     * @param position position of the dictionary to delete in the dictionariesDisplay list.
     */
    public void delete(final int position)
    {
            final Dictionary d = dictionariesDisplay.get(position);
            System.out.println(gridView.getChildCount());
            dictionariesDisplay.remove(d);
            adapter.notifyDataSetChanged();
            undo = false;
            Snackbar snack = Snackbar.make(rootLayout, d.getTitle() + getString(R.string.deleted), Snackbar.LENGTH_LONG).setAction(R.string.undo_button, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    undo = true;
                }
            });
            snack.getView().addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
                @Override
                public void onViewAttachedToWindow(View v) {
                }

                @Override
                public void onViewDetachedFromWindow(View v) {
                    //Once snackbar is closed, whatever the way : undo button clicked, change activity, an other snackbar, etc.
                    if (!undo)
                    {
                        dictionaries.remove(d);
                        ddm.delete(d.getId());
                    }
                    else
                    {
                        dictionariesDisplay.add(position, d);
                        adapter.notifyDataSetChanged();
                    }
                }
            });
            snack.show();

    }


}
