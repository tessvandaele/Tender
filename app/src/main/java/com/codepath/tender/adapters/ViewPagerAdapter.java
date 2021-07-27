package com.codepath.tender.adapters;

import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.codepath.tender.models.Restaurant;
import com.codepath.tender.tabs.HoursFragment;
import com.codepath.tender.tabs.InfoFragment;
import com.codepath.tender.tabs.GalleryFragment;

public class ViewPagerAdapter extends FragmentPagerAdapter {
    Context context;
    int totalTabs;

    private InfoFragment infoFragment;
    private HoursFragment hoursFragment;
    private GalleryFragment galleryFragment;

    public ViewPagerAdapter(Context c, FragmentManager fm, int totalTabs) {
        super(fm);
        context = c;
        this.totalTabs = totalTabs;
        infoFragment = new InfoFragment();
        hoursFragment = new HoursFragment();
        galleryFragment = new GalleryFragment();
    }

    public void setRestaurant(Restaurant restaurant) {
        infoFragment.setRestaurant(restaurant);
        hoursFragment.setRestaurant(restaurant);
        galleryFragment.setRestaurant(restaurant);
    }


    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0: //info fragment
                return infoFragment;
            case 1: //hours fragment
                return hoursFragment;
            case 2: //gallery fragment
                return galleryFragment;
            default:
                return infoFragment;
        }
    }

    @Override
    public int getCount() {
        return totalTabs;
    }
}
