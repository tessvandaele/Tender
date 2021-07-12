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
import com.codepath.tender.adapters.CardStackAdapter;
import com.codepath.tender.models.Restaurant;
import com.yuyakaido.android.cardstackview.CardStackLayoutManager;
import com.yuyakaido.android.cardstackview.CardStackListener;
import com.yuyakaido.android.cardstackview.CardStackView;
import com.yuyakaido.android.cardstackview.Direction;
import com.yuyakaido.android.cardstackview.Duration;
import com.yuyakaido.android.cardstackview.RewindAnimationSetting;
import com.yuyakaido.android.cardstackview.StackFrom;
import com.yuyakaido.android.cardstackview.SwipeAnimationSetting;
import com.yuyakaido.android.cardstackview.SwipeableMethod;

import java.util.ArrayList;
import java.util.List;

public class SwipeFragment extends Fragment {

    private static final String TAG = "SwipeFragment";

    private CardStackLayoutManager layoutManager;
    private CardStackAdapter adapter;

    private CardStackView cardStackView;
    private ImageButton ibLike;
    private ImageButton ibDislike;
    private ImageButton ibRefresh;

    private List<Restaurant> restaurants;

    //empty constructor
    public SwipeFragment() {}

    //set up to view object hierarchy
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_swipe, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        restaurants = new ArrayList<>();
        cardStackView = view.findViewById(R.id.card_stack_view);
        ibLike = view.findViewById(R.id.ibLike);
        ibDislike = view.findViewById(R.id.ibDislike);
        ibRefresh = view.findViewById(R.id.ibRefresh);

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
                if(direction == Direction.Right) {
                    Toast.makeText(getContext(), "Swipe right", Toast.LENGTH_SHORT).show();
                }
                if(direction == Direction.Left) {
                    Toast.makeText(getContext(), "Swipe left", Toast.LENGTH_SHORT).show();
                }
                if(direction == Direction.Bottom) {
                    Toast.makeText(getContext(), "Swipe down", Toast.LENGTH_SHORT).show();
                }
                if(direction == Direction.Top) {
                    Toast.makeText(getContext(), "Swipe up", Toast.LENGTH_SHORT).show();
                }

                //refill the recyclerview when the end is reached
                if(layoutManager.getTopPosition() == adapter.getItemCount() - 1 ) {
                    Toast.makeText(getContext(), "reached bottom of stack", Toast.LENGTH_SHORT).show();
                    //disable swiping
                    layoutManager.setCanScrollHorizontal(false);
                    layoutManager.setCanScrollVertical(false);
                }
            }

            //called when a card is rewinded
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

        setLayoutManager();

        populate();

        adapter = new CardStackAdapter(getContext(), restaurants);

        cardStackView.setLayoutManager(layoutManager);
        cardStackView.setAdapter(adapter);
        cardStackView.setItemAnimator(new DefaultItemAnimator());

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
                cardStackView.smoothScrollToPosition(0);
                adapter.notifyDataSetChanged();
            }
        });

    }

    //sets all preferences on visual and functional features of card stack
    private void setLayoutManager() {
        layoutManager.setStackFrom(StackFrom.None); //sets whether and where the stack graphic is displayed
        layoutManager.setSwipeThreshold(0.3f); //sets how far a card must swipe to be automatically swiped off the stack
        layoutManager.setMaxDegree(20.0f); //sets how much the cards rotate when swiped
        layoutManager.setDirections(Direction.HORIZONTAL); //allows user to swipe horizontally only (user can still drag vertically)
        layoutManager.setSwipeableMethod(SwipeableMethod.AutomaticAndManual); //sets swiping method
    }

    //populates the list of restaurants with data
    private void populate() {
        restaurants.add(new Restaurant("Italia", R.drawable.sample1, "5.8 miles away", 4.8));
        restaurants.add(new Restaurant("Citris Grill", R.drawable.sample2, "5.8 miles away", 4.6));
        restaurants.add(new Restaurant("Burger Hut", R.drawable.sample3, "2.3 miles away", 4.6));
        restaurants.add(new Restaurant("Garden Cafe", R.drawable.sample4, "7.2 miles away", 4.5));
        restaurants.add(new Restaurant("Coffee Bar", R.drawable.sample5, "2.6 miles away", 4.2));

        restaurants.add(new Restaurant("Italia", R.drawable.sample1, "5.8 miles away", 4.8));
        restaurants.add(new Restaurant("Citris Grill", R.drawable.sample2, "5.8 miles away", 4.6));
        restaurants.add(new Restaurant("Burger Hut", R.drawable.sample3, "2.3 miles away", 4.6));
        restaurants.add(new Restaurant("Garden Cafe", R.drawable.sample4, "7.2 miles away", 4.5));
        restaurants.add(new Restaurant("Coffee Bar", R.drawable.sample5, "2.6 miles away", 4.2));


    }
}