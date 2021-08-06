package com.codepath.tender;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import com.parse.ParseUser;

import static com.codepath.tender.Constants.CATEGORIES_KEY;
import static com.codepath.tender.Constants.LATITUDE_KEY;
import static com.codepath.tender.Constants.LONGITUDE_KEY;
import static com.codepath.tender.Constants.PRICES_KEY;
import static com.codepath.tender.Constants.RADIUS_KEY;

/* View model that provides user data to the UI */

public class UserViewModel extends AndroidViewModel {

    private MutableLiveData<Integer> radius;
    private MutableLiveData<String> prices;
    private MutableLiveData<Double> latitude;
    private MutableLiveData<Double> longitude;
    private MutableLiveData<String> categories;

    public UserViewModel(@NonNull Application application) {
        super(application);
    }

    public void setRadius(int radius) {
        if(this.radius == null) {
            this.radius = new MutableLiveData<>();
        }
        this.radius.setValue(radius);
    }
    public void setPrices(String prices) {
        if(this.prices == null) {
            this.prices = new MutableLiveData<>();
        }
        this.prices.setValue(prices);
    }
    public void setLatitude(Double latitude) {
        if(this.latitude == null) {
            this.latitude = new MutableLiveData<>();
        }
        this.latitude.setValue(latitude);
    }
    public void setLongitude(Double longitude) {
        if(this.longitude == null) {
            this.longitude = new MutableLiveData<>();
        }
        this.longitude.setValue(longitude);
    }
    public void setCategories(String categories) {
        if(this.categories == null) {
            this.categories = new MutableLiveData<>();
        }
        this.categories.setValue(categories);
    }

    public MutableLiveData<Integer> getRadius() {
        if(radius == null) {
            radius = new MutableLiveData<>();
            radius.setValue(ParseUser.getCurrentUser().getInt(RADIUS_KEY));
        }
        return radius;
    }
    public MutableLiveData<String> getPrices() {
        if(prices == null) {
            this.prices = new MutableLiveData<>();
            this.prices.setValue(ParseUser.getCurrentUser().getString(PRICES_KEY));
        }
        return prices;
    }
    public MutableLiveData<Double> getLatitude() {
        if(latitude == null) {
            latitude = new MutableLiveData<>();
            latitude.setValue(ParseUser.getCurrentUser().getDouble(LATITUDE_KEY));
        }
        return latitude;
    }
    public MutableLiveData<Double> getLongitude() {
        if(longitude == null) {
            longitude = new MutableLiveData<>();
            longitude.setValue(ParseUser.getCurrentUser().getDouble(LONGITUDE_KEY));
        }
        return longitude;
    }
    public MutableLiveData<String> getCategories() {
        if(categories == null) {
            this.categories = new MutableLiveData<>();
            this.categories.setValue(ParseUser.getCurrentUser().getString(CATEGORIES_KEY));
        }
        return categories;
    }
}
