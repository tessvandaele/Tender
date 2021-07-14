package com.codepath.tender.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.RecyclerView;

import com.codepath.tender.R;
import com.codepath.tender.RestaurantViewModel;
import com.codepath.tender.models.YelpSearchResult;
import com.codepath.tender.YelpService;
import com.codepath.tender.adapters.CardStackAdapter;
import com.codepath.tender.models.Restaurant;
import com.yuyakaido.android.cardstackview.CardStackLayoutManager;
import com.yuyakaido.android.cardstackview.CardStackListener;
import com.yuyakaido.android.cardstackview.CardStackView;
import com.yuyakaido.android.cardstackview.Direction;
import com.yuyakaido.android.cardstackview.StackFrom;
import com.yuyakaido.android.cardstackview.SwipeAnimationSetting;
import com.yuyakaido.android.cardstackview.SwipeableMethod;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SwipeFragment extends Fragment {

    private static final String TAG = "SwipeFragment";
    private static final String BASE_URL = "https://api.yelp.com/v3/";
    private static final String API_KEY = "GrsRS-QAb3mRuvqWsTPW5Bye4DAJ1TJY9v5addUNFFIhpb-iL8DwR0NJ_y-hOWIc94vW7wpIYZc3HRU7NQyAf0PQ0vsSddtF1qnNXlebmvey-5Vq6myMcfFgYJrtYHYx";

    //library used for easier API requests and querying
    private Retrofit retrofit;
    private YelpService yelpService;
    private int offset;

    private CardStackLayoutManager layoutManager;
    private CardStackAdapter adapter;

    //declaring views
    private CardStackView cardStackView;
    private ImageButton ibLike;
    private ImageButton ibDislike;
    private ImageButton ibRefresh;

    private List<Restaurant> restaurants;
    private RestaurantViewModel restaurantViewModel;

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

        restaurants = new ArrayList<>();
        cardStackView = view.findViewById(R.id.card_stack_view);
        ibLike = view.findViewById(R.id.ibLike);
        ibDislike = view.findViewById(R.id.ibDislike);
        ibRefresh = view.findViewById(R.id.ibRefresh);

        initializeLayoutManager();
        setLayoutManagerProperties();

        adapter = new CardStackAdapter(getContext(), restaurants);

        cardStackView.setLayoutManager(layoutManager);
        cardStackView.setAdapter(adapter);
        cardStackView.setItemAnimator(new DefaultItemAnimator());
        restaurantViewModel = new ViewModelProvider(getActivity()).get(RestaurantViewModel.class);

        setAutomatedSwiping();
        retrieveOldData();
    }

    //saving state of fragment to view model
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        restaurantViewModel.setTopPosition(layoutManager.getTopPosition());
        restaurantViewModel.setOffset(offset);
    }

    private void initializeLayoutManager() {
        layoutManager = new CardStackLayoutManager(getContext(), new CardStackListener() {
            //called when a card is dragged from original position
            @Override
            public void onCardDragging(Direction direction, float ratio) {
                Log.d(TAG, "onCardDragging: d = " + direction.name() + ", ratio = " + ratio);
            }

            //called when a card is swiped off the deck
            @Override
            public void onCardSwiped(Direction direction) {
                Log.d(TAG, "onCardSwiped: p = " + layoutManager.getTopPosition() + " d = " + direction.name());

                //check if more cards need to be loaded
                if(offset - layoutManager.getTopPosition() < 2) {
                    fetchRestaurants();
                }

                //add restaurant to favorites list if user swiped right
                if(direction == Direction.Right) {
                    restaurantViewModel.insert(restaurants.get(layoutManager.getTopPosition()-1));
                }
            }

            //called when a card is rewinded/brought back using .rewind()
            @Override
            public void onCardRewound() {
                Log.d(TAG, "onCardRewound: " + layoutManager.getTopPosition());
            }

            //called when a card is dragged and then released back to original position
            @Override
            public void onCardCanceled() {
                Log.d(TAG, "onCardCanceled: " + layoutManager.getTopPosition());
            }

            //called when a new card appears
            @Override
            public void onCardAppeared(View view, int position) {
                TextView text = view.findViewById(R.id.item_name);
                Log.d(TAG, "onCardAppeared: " + position + ", name: " + text.getText());
            }

            //called when a card disappears
            @Override
            public void onCardDisappeared(View view, int position) {
                TextView text = view.findViewById(R.id.item_name);
                Log.d(TAG, "onCardDisappeared: " + position + ", name: " + text.getText());
            }
        });
    }

    //retrieves the data that user had already loaded in last fragment access
    //currently makes a new request to API (will be adjusted to use database instead)
    private void retrieveOldData() {
        //setting the stored value of offset and current position in stack
        offset = restaurantViewModel.getOffset();
        layoutManager.setTopPosition(restaurantViewModel.getTopPosition());

        //calls a normal fetch of data (user has not yet swiped through any cards)
        if(restaurantViewModel.getTopPosition() == 0) {
            fetchRestaurants();
            return;
        }

        //API call to retrieve cards that user had previously loaded
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        yelpService = retrofit.create(YelpService.class);
        yelpService.getRestaurants("Bearer " + API_KEY, "New York", offset, 0).enqueue(new Callback<YelpSearchResult>() {
            @Override
            public void onResponse(Call<YelpSearchResult> call, Response<YelpSearchResult> response) {
                YelpSearchResult searchResult = response.body();
                if(searchResult == null){
                    Log.e(TAG, "No restaurants retrieved");
                    return;
                }
                restaurants.addAll(searchResult.restaurants);

                //saving location since adapter will override value of topPosition
                int saved_location = layoutManager.getTopPosition();

                adapter.notifyDataSetChanged();

                //setting the position to where user was previously
                cardStackView.scrollToPosition(saved_location);
            }

            @Override
            public void onFailure(Call<YelpSearchResult> call, Throwable t) {
                Log.d(TAG, "onFailure " + t);
            }
        });
    }

    //helper method to run GET request to Yelp API and add resulting restaurant objects to the list of restaurants
    private void fetchRestaurants() {
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        yelpService = retrofit.create(YelpService.class);

        yelpService.getRestaurants("Bearer " + API_KEY, "New York", 5, offset).enqueue(new Callback<YelpSearchResult>() {
            @Override
            public void onResponse(Call<YelpSearchResult> call, Response<YelpSearchResult> response) {
                YelpSearchResult searchResult = response.body();
                if(searchResult == null){
                    Log.e(TAG, "No restaurants retrieved");
                    return;
                }
                restaurants.addAll(searchResult.restaurants);
                adapter.notifyItemRangeInserted(offset, 5);
                offset += 5;
            }

            @Override
            public void onFailure(Call<YelpSearchResult> call, Throwable t) {
                Log.d(TAG, "onFailure " + t);
            }
        });

    }

    //helper method to set up automated swiping
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
                cardStackView.scrollToPosition(0);
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
}