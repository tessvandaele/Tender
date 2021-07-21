package com.codepath.tender;

import android.util.Log;

import com.codepath.tender.models.Favorite;
import com.codepath.tender.models.Restaurant;
import com.codepath.tender.models.YelpSearchResult;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.parse.ParseObject.saveAllInBackground;

/* Repository class provides a clean API for data access to the rest of the application */

public class RestaurantRepository {

    private static final String BASE_URL = "https://api.yelp.com/v3/";
    private static final String API_KEY = "GrsRS-QAb3mRuvqWsTPW5Bye4DAJ1TJY9v5addUNFFIhpb-iL8DwR0NJ_y-hOWIc94vW7wpIYZc3HRU7NQyAf0PQ0vsSddtF1qnNXlebmvey-5Vq6myMcfFgYJrtYHYx";

    private static  final String FAVORITE_TABLE_KEY = "Favorite";
    private static  final String RESTAURANT_ID_KEY = "restaurantId";
    private static  final String USER_ID_KEY = "userId";

    private Retrofit retrofit;
    private YelpService yelpService;
    private FetchRestaurantsListener restaurantsListener;
    private FetchFavoritesListener favoritesListener;

    private ArrayList<Restaurant> favorites;

    //interface to refresh swipe adapter when user preferences are updated
    public interface FetchRestaurantsListener {
        void onFinishFetch(List<Restaurant> restaurants);
    }

    //interface to refresh swipe adapter when data is updated
    public interface FetchFavoritesListener {
        void onFinishFetch(List<Restaurant> restaurants);
    }

    public RestaurantRepository() {
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        yelpService = retrofit.create(YelpService.class);
        favorites = new ArrayList<>();
    }

    //inserting unique favorite for current user
    void insertFavorite(String restaurant_id, String user_id) {
        ParseObject favorite = new ParseObject(FAVORITE_TABLE_KEY);
        favorite.put(Favorite.RESTAURANT_ID_KEY, restaurant_id);
        favorite.put(Favorite.USER_ID_KEY, user_id);

        ParseQuery<ParseObject> query = ParseQuery.getQuery(FAVORITE_TABLE_KEY);
        query.whereEqualTo(USER_ID_KEY, user_id);
        query.whereEqualTo(RESTAURANT_ID_KEY, restaurant_id);

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if(objects.size() == 0) { //favorite does not already exist
                    favorite.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if(e != null) {
                                Log.e("SwipeFragment", "Error saving restaurant", e);
                                return;
                            }
                        }
                    });
                }
            }
        });
    }

    //run GET request to Yelp API and send resulting list to swipe fragment through listener
    public void fetchRestaurants(double latitude, double longitude, int limit, int offset, int radius, String prices) {
        yelpService.getRestaurants("Bearer " + API_KEY, latitude, longitude, limit, offset, radius, prices).enqueue(new Callback<YelpSearchResult>() {
            @Override
            public void onResponse(Call<YelpSearchResult> call, Response<YelpSearchResult> response) {
                YelpSearchResult searchResult = response.body();
                if(searchResult == null){
                    Log.e("View model", "No restaurants retrieved");
                    return;
                }
                //sends the list of restaurants to the swipe fragment through a listener
                restaurantsListener.onFinishFetch(searchResult.restaurants);
            }
            @Override
            public void onFailure(Call<YelpSearchResult> call, Throwable t) {
                Log.d("View model", "onFailure " + t);
            }
        });
    }

    //method to initialize the fetch restaurant listener
    public void setFetchRestaurantListener(FetchRestaurantsListener listener) {
        this.restaurantsListener = listener;
    }

    //method to initialize the fetch favorites listener
    public void setFetchFavoritesListener(FetchFavoritesListener listener) {
        this.favoritesListener = listener;
    }

    //returns a list of the current user's favorite restaurants
    public void getFavorites() {
        favorites.clear();
        //parse for favorites restaurant objects
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(FAVORITE_TABLE_KEY);
        query.whereEqualTo(USER_ID_KEY, ParseUser.getCurrentUser().getObjectId());
        query.findInBackground(new FindCallback<ParseObject>() { //parse for favorites of user
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                //populating favorites list with favorite restaurants
                for (ParseObject object : objects) {
                    String id = object.getString(RESTAURANT_ID_KEY);
                    getRestaurant(id);
                }
            }
        });
    }

    //run GET request to Yelp API and add resulting restaurant objects to the list of restaurants
    private void getRestaurant(String id) {
        yelpService.getRestaurantDetails("Bearer " + API_KEY, id).enqueue(new Callback<Restaurant>() {
            @Override
            public void onResponse(Call<Restaurant> call, Response<Restaurant> response) {
                favorites.add(response.body());
                favoritesListener.onFinishFetch(favorites);
            }

            @Override
            public void onFailure(Call<Restaurant> call, Throwable t) {
                Log.d("Favorites fragment", "Could not retrieve restaurant");
            }
        });
    }
}
