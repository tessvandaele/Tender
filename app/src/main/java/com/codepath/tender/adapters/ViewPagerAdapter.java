package com.codepath.tender.adapters;

import android.content.Context;
import android.util.Log;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

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
        Fragment fragment = new Fragment();
        switch (position) {
            case 0:
                //fragment 1
                Log.d("Adapter", "Fragment 1");
                return fragment;
            case 1:
                //fragment 2
                Log.d("Adapter", "Fragment 2");
                return fragment;
            case 2:
                //fragment3
                Log.d("Adapter", "Fragment 3");
                return fragment;
            default:
                return null;
        }
    }
    @Override
    public int getCount() {
        return totalTabs;
    }
}
