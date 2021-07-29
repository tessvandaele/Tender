package com.codepath.tender.fragments;

import android.graphics.Color;
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
import com.codepath.tender.UserViewModel;
import com.codepath.tender.adapters.CardStackAdapter;
import com.codepath.tender.adapters.ViewPagerAdapter;
import com.codepath.tender.models.Categories;
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
    private RatingBar ratingBarSheet;
    private TextView reviewCount;
    private TextView price;
    private TextView open;
    private TextView categories;

    //tab layout in bottom sheet
    private TabLayout tabLayout;

    private RestaurantViewModel restaurantViewModel;
    private UserViewModel userViewModel;
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
        ratingBarSheet = view.findViewById(R.id.ratingBarSheet);
        reviewCount = view.findViewById(R.id.tvReviewCountSheet);
        price = view.findViewById(R.id.tvPriceSheet);
        open = view.findViewById(R.id.tvOpenOrClosed);
        categories = view.findViewById(R.id.tvCategoriesSheet);

        //tab layout
        tabLayout = view.findViewById(R.id.tabLayout);
        ViewPager viewPager = view.findViewById(R.id.viewPager);
        setTabLayout(viewPager);

        //view model
        restaurantViewModel = new ViewModelProvider(getActivity()).get(RestaurantViewModel.class);
        userViewModel = new ViewModelProvider(getActivity()).get(UserViewModel.class);

        initializeLayoutManager();
        setLayoutManagerProperties();

        //setting the bottom sheet behavior
        LinearLayout linearLayout = view.findViewById(R.id.design_bottom_sheet);
        bottomSheetBehavior = BottomSheetBehavior.from(linearLayout);

        setFetchListener();
        setCardStackAdapter();
        setAutomatedSwiping();
        setBottomSheetButtons();

        //setting offset and position from view model
        offset = restaurantViewModel.getOffset();
        layoutManager.scrollToPosition(restaurantViewModel.getTopPosition());
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
                offset = restaurantViewModel.getOffset();
                int top = layoutManager.getTopPosition();
                if(offset - top < 2) {
                    addRestaurants(offset);

                    offset+= 30;
                    restaurantViewModel.setOffset(offset);
                }

                //add restaurant to favorites list if user swiped right
                if(direction == Direction.Right) {
                    int position = layoutManager.getTopPosition() - 1;
                    restaurantViewModel.insertFavorite(restaurantViewModel.getRestaurants().getValue().get(position).getId(), ParseUser.getCurrentUser().getObjectId());
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
            public void onCardAppeared(View view, int position) { populateBottomSheetData(position); }

            //called when a card disappears
            @Override
            public void onCardDisappeared(View view, int position) { }
        });
    }

    //creates the card stack adapter and sets up the stack view
    private void setCardStackAdapter() {
        adapter = new CardStackAdapter(getContext(), restaurantViewModel.getRestaurants().getValue());
        cardStackView.setLayoutManager(layoutManager);
        cardStackView.setAdapter(adapter);
        cardStackView.setItemAnimator(new DefaultItemAnimator());
    }

    //saving state of fragment to view model
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        restaurantViewModel.setTopPosition(layoutManager.getTopPosition());
        restaurantViewModel.setOffset(offset);
    }

    //populate bottom sheet
    private void populateBottomSheetData(int position) {
        ArrayList<Restaurant> restaurants = restaurantViewModel.getRestaurants().getValue();
        nameSheet.setText(restaurants.get(position).getName());
        ratingBarSheet.setRating((float) restaurants.get(position).getRating());
        reviewCount.setText(restaurants.get(position).getReview_count() + " reviews");
        price.setText(restaurants.get(position).getPrice());

        String categoriesString = "";
        Categories[] categories_array = restaurants.get(position).getCategories();
        for(int i = 0; i<categories_array.length; i++) {
            categoriesString += categories_array[i].getTitle() + ", ";
        }
        categories.setText(categoriesString.substring(0, categoriesString.length() - 2));
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
                populateBottomSheetData(0);
            }
        });
    }

    //set up the bottom info sheet pop up
    private void setBottomSheetButtons() {
        ibUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                restaurantViewModel.getRestaurantDetails(restaurantViewModel.getRestaurants().getValue().get(layoutManager.getTopPosition()).getId());
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });

        ibDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            }
        });

        restaurantViewModel.setRestaurantDetailsListener(new RestaurantRepository.RestaurantDetailsListener() {
            @Override
            public void onFinishDetailsFetch(Restaurant restaurant) {
                restaurantViewModel.setRestaurant(restaurant);
                setOpenOrClosed(restaurant);
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
        restaurantViewModel.setFetchRestaurantListener(new RestaurantRepository.FetchRestaurantsListener() {
            @Override
            public void onFinishFetch(List<Restaurant> restaurants) {
                if(restaurantViewModel.getOffset() == 30) { //refreshing deck completely
                    restaurantViewModel.clearRestaurants();
                    restaurantViewModel.addAllRestaurants(restaurants);
                    layoutManager.scrollToPosition(0);
                    adapter.setRestaurants(restaurants);
                } else { //adding to the end of the deck
                    restaurantViewModel.addAllRestaurants(restaurants);
                    adapter.addRestaurants(restaurants, offset);
                }

                //populate the first bottom sheet (not recognized by cardAppeared())
                if(layoutManager.getTopPosition() == 0 && restaurants.size() != 0) {
                    populateBottomSheetData(0);
                }
            }
        });
    }

    //sets up the tab layout
    private void setTabLayout(ViewPager viewPager) {
        tabLayout.addTab(tabLayout.newTab().setText("Info"));
        tabLayout.addTab(tabLayout.newTab().setText("Hours"));
        tabLayout.addTab(tabLayout.newTab().setText("Gallery"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getContext(),getParentFragmentManager(),
                tabLayout.getTabCount());
        viewPager.setAdapter(viewPagerAdapter);
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

    //sets textview to be closed/red when restaurant is closed and open/green when it is open
    private void setOpenOrClosed(Restaurant restaurant) {
        if(restaurant.getHours() == null) return;
        if(restaurant.getHours()[0].isIs_open_now()){
            open.setText("Open");
            open.setTextColor(Color.parseColor("#4fa64f"));
        } else {
            open.setText("Closed");
            open.setTextColor(Color.parseColor("#ed2939"));
        }
    }

    //helper method to fetch more restaurants when the end of the deck is reached
    public void addRestaurants(int offset) {
        //fetch restaurants
        double latitude = userViewModel.getLatitude().getValue();
        double longitude = userViewModel.getLongitude().getValue();
        int radius = userViewModel.getRadius().getValue() * 1609;
        String prices = userViewModel.getPrices().getValue();
        String categories = userViewModel.getCategories().getValue();
        restaurantViewModel.fetchRestaurants(latitude, longitude, 30, offset, radius, prices, categories, "distance");
    }
}