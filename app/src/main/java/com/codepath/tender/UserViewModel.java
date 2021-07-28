package com.codepath.tender;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import com.parse.ParseUser;

public class UserViewModel extends AndroidViewModel {

    private MutableLiveData<Integer> radius;
    private MutableLiveData<String> prices;
    private MutableLiveData<Double> latitude;
    private MutableLiveData<Double> longitude;

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

    public MutableLiveData<Integer> getRadius() {
        if(radius == null) {
            radius = new MutableLiveData<>();
            radius.setValue(10);
        }
        return radius;
    }
    public MutableLiveData<String> getPrices() {
        if(prices == null) {
            this.prices = new MutableLiveData<>();
            this.prices.setValue(ParseUser.getCurrentUser().getString("prices"));
        }
        return prices;
    }
    public MutableLiveData<Double> getLatitude() {
        if(latitude == null) {
            latitude = new MutableLiveData<>();
            latitude.setValue(ParseUser.getCurrentUser().getDouble("latitude"));
        }
        return latitude;
    }
    public MutableLiveData<Double> getLongitude() {
        if(longitude == null) {
            longitude = new MutableLiveData<>();
            longitude.setValue(ParseUser.getCurrentUser().getDouble("longitude"));
        }
        return longitude;
    }
}
