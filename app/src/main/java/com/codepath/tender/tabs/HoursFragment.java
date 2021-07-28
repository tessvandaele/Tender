package com.codepath.tender.tabs;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.codepath.tender.R;
import com.codepath.tender.RestaurantViewModel;
import com.codepath.tender.models.Open;
import com.codepath.tender.models.Restaurant;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class HoursFragment extends Fragment {

    private Restaurant restaurant;

    private TextView monday;
    private TextView tuesday;
    private TextView wednesday;
    private TextView thursday;
    private TextView friday;
    private TextView saturday;
    private TextView sunday;

    private RestaurantViewModel model;

    public HoursFragment() {}

    //inflate layout
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_hours, container, false);
    }

    //construct view hierarchy
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        monday = view.findViewById(R.id.tvMonday);
        tuesday = view.findViewById(R.id.tvTuesday);
        wednesday = view.findViewById(R.id.tvWednesday);
        thursday = view.findViewById(R.id.tvThursday);
        friday = view.findViewById(R.id.tvFriday);
        saturday = view.findViewById(R.id.tvSaturday);
        sunday = view.findViewById(R.id.tvSunday);

        model = new ViewModelProvider(getActivity()).get(RestaurantViewModel.class);

        model.getCurrent_restaurant().observe(getViewLifecycleOwner(), restaurant1 -> {
            this.restaurant = restaurant1;
            try {
                bindHours();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        });
    }

    //coverts 4-digit military time to standard time with AM/PM
    public String getDisplayTime(String time) throws ParseException {
        DateFormat originalFormat = new SimpleDateFormat("HHmm");
        DateFormat targetFormat = new SimpleDateFormat("hh:mm a");
        Date date = originalFormat.parse(time);
        String formattedDate = targetFormat.format(date);  // 20120821
        return formattedDate;
    }

    //binds the hours data of the restaurant to hours tab
    public void bindHours() throws ParseException {
        if(restaurant.getHours() == null) {
            return;
        }
        Open[] hours = restaurant.getHours()[0].getOpen();
        for(int i = 0; i<hours.length; i++) {
            int day = hours[i].getDay();
            switch (day) {
                case 0: //monday
                    monday.setText(getDisplayTime(hours[i].getStart()) + " - " + getDisplayTime(hours[i].getEnd()));
                case 1: //tuesday
                    tuesday.setText(getDisplayTime(hours[i].getStart()) + " - " + getDisplayTime(hours[i].getEnd()));
                case 2: //wednesday
                    wednesday.setText(getDisplayTime(hours[i].getStart()) + " - " + getDisplayTime(hours[i].getEnd()));
                case 3: //thursday
                    thursday.setText(getDisplayTime(hours[i].getStart()) + " - " + getDisplayTime(hours[i].getEnd()));
                case 4: //friday
                    friday.setText(getDisplayTime(hours[i].getStart()) + " - " + getDisplayTime(hours[i].getEnd()));
                case 5: //saturday
                    saturday.setText(getDisplayTime(hours[i].getStart()) + " - " + getDisplayTime(hours[i].getEnd()));
                case 6: //sunday
                    sunday.setText(getDisplayTime(hours[i].getStart()) + " - " + getDisplayTime(hours[i].getEnd()));
                default:

            }
        }
    }
}