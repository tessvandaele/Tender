package com.codepath.tender.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.viewpager.widget.ViewPager;

import com.codepath.tender.R;
import com.codepath.tender.RestaurantRepository;
import com.codepath.tender.RestaurantViewModel;
import com.codepath.tender.adapters.CardStackAdapter;
import com.codepath.tender.adapters.ViewPagerAdapter;
import com.codepath.tender.models.Restaurant;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.tabs.TabLayout;
import com.parse.ParseUser;
import com.yuyakaido.android.cardstackview.CardStackLayoutManager;
import com.yuyakaido.android.cardstackview.CardStackListener;
import com.yuyakaido.android.cardstackview.CardStackView;
import com.yuyakaido.android.cardstackview.Direction;
import com.yuyakaido.android.cardstackview.StackFrom;
import com.yuyakaido.android.cardstackview.SwipeAnimationSetting;
import com.yuyakaido.android.cardstackview.SwipeableMethod;

import java.util.ArrayList;
import java.util.List;

/* user can swipe through a deck of restaurants and swipe based on preference */

public class SwipeFragment extends Fragment {

    private CardStackLayoutManager layoutManager;
    private CardStackAdapter adapter;
    private BottomSheetBehavior bottomSheetBehavior;

    //declaring views
    private CardStackView cardStackView;
    private ImageButton ibLike;
    private ImageButton ibDislike;
    private ImageButton ibRefresh;

    //bottom sheet views
    private ImageButton ibUp;
    private ImageButton ibDown;
    private TextView nameSheet;
    private TextView distanceSheet;
    private RatingBar ratingBarSheet;
    private TextView reviewCount;
    private TextView price;

    //tab layout in bottom sheet
    private TabLayout tabLayout;
    private ViewPager viewPager;

    private RestaurantViewModel model;
    private int offset;

    //empty constructor
    public SwipeFragment() {}

    //inflate layout
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_swipe, container, false);
    }

    //construct view hierarchy
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        //fragment views
        cardStackView = view.findViewById(R.id.card_stack_view);
        ibLike = view.findViewById(R.id.ibLike);
        ibDislike = view.findViewById(R.id.ibDislike);
        ibRefresh = view.findViewById(R.id.ibRefresh);

        //bottom sheet
        ibUp = view.findViewById(R.id.ibUp);
        ibDown = view.findViewById(R.id.ibDown);
        nameSheet = view.findViewById(R.id.tvNameSheet);
        distanceSheet = view.findViewById(R.id.tvDistanceSheet);
        ratingBarSheet = view.findViewById(R.id.ratingBarSheet);
        reviewCount = view.findViewById(R.id.tvReviewCountSheet);
        price = view.findViewById(R.id.tvPriceSheet);
        tabLayout = view.findViewById(R.id.tabLayout);
        viewPager = view.findViewById(R.id.viewPager);

        //view model
        model = new ViewModelProvider(getActivity()).get(RestaurantViewModel.class);

        //setting the bottom sheet behavior
        LinearLayout linearLayout = view.findViewById(R.id.design_bottom_sheet);
        bottomSheetBehavior = BottomSheetBehavior.from(linearLayout);

        initializeLayoutManager();
        setLayoutManagerProperties();

        offset = model.getOffset();
        layoutManager.setTopPosition(model.getTopPosition());

        setFetchListener();

        adapter = new CardStackAdapter(getContext(), model.getRestaurants());
        cardStackView.setLayoutManager(layoutManager);
        cardStackView.setAdapter(adapter);
        cardStackView.setItemAnimator(new DefaultItemAnimator());

        setAutomatedSwiping();
        setBottomSheet();
        setTabLayout();
    }

    private void initializeLayoutManager() {
        layoutManager = new CardStackLayoutManager(getContext(), new CardStackListener() {
            //called when a card is dragged from original position
            @Override
            public void onCardDragging(Direction direction, float ratio) {}

            //called when a card is swiped off the deck
            @Override
            public void onCardSwiped(Direction direction) {
                //check if more cards need to be loaded
                if(offset - layoutManager.getTopPosition() < 2) {
                    //add logic for loading more restaurants
                }

                //add restaurant to favorites list if user swiped right
                if(direction == Direction.Right) {
                    int position = layoutManager.getTopPosition() - 1;
                    model.insertFavorite(model.getRestaurants().get(position).getId(), ParseUser.getCurrentUser().getObjectId());
                }
            }

            //called when a card is rewinded/brought back using .rewind()
            @Override
            public void onCardRewound() { }

            //called when a card is dragged and then released back to original position
            @Override
            public void onCardCanceled() { }

            //called when a new card appears
            @Override
            public void onCardAppeared(View view, int position) { populateBottomSheet(position); }

            //called when a card disappears
            @Override
            public void onCardDisappeared(View view, int position) { }
        });
    }

    //saving state of fragment to view model
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        model.setTopPosition(layoutManager.getTopPosition());
        model.setOffset(offset);
    }

    //populate bottom sheet
    private void populateBottomSheet(int position) {
        ArrayList<Restaurant> restaurants = model.getRestaurants();
        nameSheet.setText(restaurants.get(position).getName());
        distanceSheet.setText(restaurants.get(position).getDisplayDistance());
        ratingBarSheet.setRating((float) restaurants.get(position).getRating());
        reviewCount.setText(restaurants.get(position).getReview_count() + " reviews");
        price.setText(restaurants.get(position).getPrice());
    }

    //set up automated swiping
    private void setAutomatedSwiping() {
        //automatically swipe right
        ibLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SwipeAnimationSetting setting = new SwipeAnimationSetting.Builder()
                        .setDirection(Direction.Right)
                        .build();
                layoutManager.setSwipeAnimationSetting(setting);
                cardStackView.swipe();
            }
        });

        //automatically swipe left
        ibDislike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SwipeAnimationSetting setting = new SwipeAnimationSetting.Builder()
                        .setDirection(Direction.Left)
                        .build();
                layoutManager.setSwipeAnimationSetting(setting);
                cardStackView.swipe();
            }
        });

        //restart stack of cards
        ibRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layoutManager.scrollToPosition(0);
                populateBottomSheet(0);
            }
        });
    }

    //set up the bottom info sheet pop up
    private void setBottomSheet() {
        ibUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                model.getRestaurantDetails(model.getRestaurants().get(layoutManager.getTopPosition()).getId());
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });

        ibDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            }
        });

        model.setRestaurantDetailsListener(new RestaurantRepository.RestaurantDetailsListener() {
            @Override
            public void onFinishDetailsFetch(Restaurant restaurant) {
                Log.d("Restaurant", restaurant.getName());
            }
        });
    }

    //sets all preferences on visual and functional features of card stack
    private void setLayoutManagerProperties() {
        layoutManager.setStackFrom(StackFrom.None); //sets whether and where the stack graphic is displayed
        layoutManager.setSwipeThreshold(0.3f); //sets how far a card must swipe to be automatically swiped off the stack
        layoutManager.setMaxDegree(20.0f); //sets how much the cards rotate when swiped
        layoutManager.setDirections(Direction.HORIZONTAL); //allows user to swipe horizontally only (user can still drag vertically)
        layoutManager.setSwipeableMethod(SwipeableMethod.AutomaticAndManual); //sets swiping method
    }

    //implements the fetch listener interface
    private void setFetchListener() {
        model.setFetchRestaurantListener(new RestaurantRepository.FetchRestaurantsListener() {
            @Override
            public void onFinishFetch(List<Restaurant> restaurants) {
                model.addAllRestaurants(restaurants);
                adapter.notifyDataSetChanged();
                layoutManager.scrollToPosition(0);

                //populate the first bottom sheet (not recognized by cardAppeared())
                if(layoutManager.getTopPosition() == 0 && restaurants.size() != 0) {
                    populateBottomSheet(0);
                }

                //increase offset
                offset += 30;
            }
        });
    }

    //sets up the tab layout
    private void setTabLayout() {
        tabLayout.addTab(tabLayout.newTab().setText("Info"));
        tabLayout.addTab(tabLayout.newTab().setText("Hours"));
        tabLayout.addTab(tabLayout.newTab().setText("Menu"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        ViewPagerAdapter adapter = new ViewPagerAdapter(getContext(),getParentFragmentManager(),
                tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }
}