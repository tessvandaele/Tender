package com.codepath.tender.adapters;

import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.codepath.tender.tabs.HoursFragment;
import com.codepath.tender.tabs.InfoFragment;
import com.codepath.tender.tabs.MenuFragment;

public class ViewPagerAdapter extends FragmentPagerAdapter {
    Context context;
    int totalTabs;

    public ViewPagerAdapter(Context c, FragmentManager fm, int totalTabs) {
        super(fm);
        context = c;
        this.totalTabs = totalTabs;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0: //info fragment
                return new InfoFragment();
            case 1: //hours fragment
                return new HoursFragment();
            case 2: //menu fragment
                return new MenuFragment();
            default:
                return new InfoFragment();
        }
    }

    @Override
    public int getCount() {
        return totalTabs;
    }
}
