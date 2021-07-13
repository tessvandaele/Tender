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
import androidx.recyclerview.widget.DefaultItemAnimator;

import com.codepath.tender.R;
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

    private CardStackLayoutManager layoutManager;
    private CardStackAdapter adapter;

    //declaring views
    private CardStackView cardStackView;
    private ImageButton ibLike;
    private ImageButton ibDislike;
    private ImageButton ibRefresh;

    private List<Restaurant> restaurants;

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

        setAutomatedSwiping();

        fetchRestaurants();
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
                Toast.makeText(getContext(), "Swiped " + direction.name(), Toast.LENGTH_SHORT).show();
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

    //helper method to run GET request to Yelp API and add resulting restaurant objects to the list of restaurants
    private void fetchRestaurants() {
        retrofit = new Retrofit.Builder()
                            .baseUrl(BASE_URL)
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();

        YelpService yelpService = retrofit.create(YelpService.class);
        yelpService.getRestaurants("Bearer " + API_KEY, "New York").enqueue(new Callback<YelpSearchResult>() {
            @Override
            public void onResponse(Call<YelpSearchResult> call, Response<YelpSearchResult> response) {
                YelpSearchResult searchResult = response.body();
                if(searchResult == null){
                    Log.e(TAG, "No restaurants retrieved");
                    return;
                }
                restaurants.addAll(searchResult.restaurants);
                adapter.notifyDataSetChanged();
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