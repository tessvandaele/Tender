package com.codepath.tender;

import android.util.Log;

import com.codepath.tender.models.Restaurant;
import com.codepath.tender.models.Review;
import com.codepath.tender.models.User;
import com.codepath.tender.models.YelpReviewResult;
import com.codepath.tender.models.YelpSearchResult;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.codepath.tender.Constants.API_KEY;
import static com.codepath.tender.Constants.BASE_URL;
import static com.codepath.tender.Constants.FAVORITE_TABLE_KEY;
import static com.codepath.tender.Constants.RESTAURANT_ID_KEY;
import static com.codepath.tender.Constants.USER_ID_KEY;

/* Repository class provides a clean API for data access to the rest of the application */

public class RestaurantRepository {

    private Retrofit retrofit;
    private YelpService yelpService;
    private FetchRestaurantsListener restaurantsListener;
    private FetchFavoritesListener favoritesListener;
    private DeleteFavoriteListener deleteFavoriteListener;
    private RestaurantDetailsListener restaurantDetailsListener;
    private ReviewsListener reviewsListener;
    private OtherUsersListener otherUsersListener;

    private ArrayList<Restaurant> favorites;

    //interface to refresh swipe adapter when user preferences are updated
    public interface FetchRestaurantsListener {
        void onFinishFetch(List<Restaurant> restaurants);
    }

    //interface to refresh swipe adapter when data is updated
    public interface FetchFavoritesListener {
        void onFinishFetch(Restaurant restaurants);
    }

    //interface to delete a user favorite from list
    public interface DeleteFavoriteListener {
        void onFinishDelete();
    }

    //interface to get detailed data of restaurant in detail activity
    public interface RestaurantDetailsListener {
        void onFinishDetailsFetch(Restaurant restaurant);
    }

    //interface to get reviews of restaurant in detail activity
    public interface ReviewsListener {
        void onFinishReviewsFetch(List<Review> reviews);
    }

    //interface to get other users that have a restaurant favorited
    public interface OtherUsersListener {
        void onFinishOtherUserFetch(User user);
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
        favorite.put(RESTAURANT_ID_KEY, restaurant_id);
        favorite.put(USER_ID_KEY, user_id);

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
                favoritesListener.onFinishFetch(response.body());
            }

            @Override
            public void onFailure(Call<Restaurant> call, Throwable t) {
                Log.d("Favorites fragment", "Could not retrieve restaurant");
            }
        });
    }

    //queries the correct restaurant to delete and then deletes in background
    public void deleteFavorite(String id) {
        //retrieve correct favorite
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(FAVORITE_TABLE_KEY);
        query.whereEqualTo(USER_ID_KEY, ParseUser.getCurrentUser().getObjectId()); //user id matches current user
        query.whereEqualTo(RESTAURANT_ID_KEY, id); //restaurant id matches id from view holder
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                if(e == null) {
                    //deleting object if found
                    object.deleteInBackground(new DeleteCallback() {
                        @Override
                        public void done(ParseException e) {
                            if(e == null) {
                                deleteFavoriteListener.onFinishDelete();
                            }
                        }
                    });
                }

            }
        });
    }

    //gets the detailed data of a restaurant given the restaurant yelp id
    public void getRestaurantDetails(String restaurant_id) {
        yelpService.getRestaurantDetails("Bearer " + API_KEY, restaurant_id).enqueue(new Callback<Restaurant>() {
            @Override
            public void onResponse(Call<Restaurant> call, Response<Restaurant> response) {
                Restaurant restaurant = response.body();
                restaurantDetailsListener.onFinishDetailsFetch(restaurant);
            }

            @Override
            public void onFailure(Call<Restaurant> call, Throwable t) {
                Log.d("Favorites fragment", "Could not retrieve restaurant");
            }
        });
    }

    //gets the review of a restaurant given restaurant id
    public void getRestaurantReviews(String restaurant_id) {
        yelpService.getRestaurantReviews("Bearer " + API_KEY, restaurant_id).enqueue(new Callback<YelpReviewResult>() {
            @Override
            public void onResponse(Call<YelpReviewResult> call, Response<YelpReviewResult> response) {
                YelpReviewResult searchResult = response.body();
                if(searchResult == null){
                    Log.e("View model", "No restaurants retrieved");
                    return;
                }
                reviewsListener.onFinishReviewsFetch(searchResult.reviews);
            }

            @Override
            public void onFailure(Call<YelpReviewResult> call, Throwable t) {

            }
        });
    }

    //gets the user id's of other users who have the restaurant in their favorites
    public void getOtherUserFavorite(String restaurant_id) {
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(FAVORITE_TABLE_KEY);
        query.whereEqualTo(RESTAURANT_ID_KEY, restaurant_id); //user id matches current user
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                for(ParseObject object : objects) {
                    ParseQuery<ParseObject> new_query = new ParseQuery<ParseObject>("User");
                    String username = object.get("userId").toString();
                    new_query.whereEqualTo("objectId", username);
                    new_query.getFirstInBackground(new GetCallback<ParseObject>() {
                        @Override
                        public void done(ParseObject object, ParseException e) {
                            otherUsersListener.onFinishOtherUserFetch((User)object);
                        }
                    });
                }
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

    //method to initialize the delete favorites listener
    public void setDeleteFavoritesListener(DeleteFavoriteListener listener) {
        this.deleteFavoriteListener = listener;
    }

    //method to initialize the details listener
    public void setRestaurantDetailsListener(RestaurantDetailsListener listener) {
        this.restaurantDetailsListener = listener;
    }

    //method to initialize the reviews listener
    public void setReviewsListener(ReviewsListener listener) {
        this.reviewsListener = listener;
    }

    //method to initialize the other user favorite listener
    public void setOtherUsersListener(OtherUsersListener listener) { this.otherUsersListener = listener; }


}
