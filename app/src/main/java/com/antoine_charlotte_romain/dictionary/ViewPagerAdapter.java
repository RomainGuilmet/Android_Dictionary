package com.antoine_charlotte_romain.dictionary;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;


public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    int NumbOfTabs; // Store the number of tabs, this will also be passed when the ViewPagerAdapter is created

    //Store the icons ids
    int[] drawablesIds = {
            R.drawable.home_tab_drawable,
            R.drawable.history_tab_drawable,
            R.drawable.search_tab_drawable
    };

    // Build a Constructor and assign the passed Values to appropriate values in the class
    public ViewPagerAdapter(FragmentManager fm, int mNumbOfTabsumb) {
        super(fm);

        this.NumbOfTabs = mNumbOfTabsumb;

    }

    //This method return the fragment for the every position in the View Pager
    @Override
    public Fragment getItem(int position) {

        if(position == 0)
        {
            HomeFragment homeTab = new HomeFragment();
            return homeTab;
        }
        else if (position == 1)
        {
            HistoryFragment historyTab = new HistoryFragment();
            return historyTab;
        }
        else
        {
            SearchFragment searchTab = new SearchFragment();
            return searchTab;
        }
    }

    // This method return the Number of tabs for the tabs Strip
    @Override
    public int getCount() {
        return NumbOfTabs;
    }

    // This method return the specific tab icon
    public int getDrawableId(int position){
        return drawablesIds[position];
    }


}