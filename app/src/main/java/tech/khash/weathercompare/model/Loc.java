package tech.khash.weathercompare.model;

import android.text.TextUtils;

import androidx.annotation.Nullable;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Khashayar "Khash" Mortazavi
 *
 * This class represents a location object. It is used for saving and retrieving locations for
 * weather queries
 *
 * AccuWeather needs a location key (cannot be done using LatLng), so the AW stands for AccuWeather
 */

public class Loc {

    private final static String TAG = Loc.class.getSimpleName();

    private LatLng latLng;
    private String id;
    private String city;
    private String country;
    private String key; //used only for AccuWeather

    //default constructor
    public Loc(){}

    public Loc(String id, LatLng latLng) {
        this.latLng = latLng;
        this.id = id;
    }

    public Loc(String id, LatLng latLng, String city, @Nullable String country) {
        this.id = id;
        this.latLng = latLng;
        this.city = city;
        this.country = country;
    }

    public Loc(LatLng latLng, String city) {
        this.latLng = latLng;
        this.city = city;
    }

    /*
        ------------------------ GETTER METHODS -----------------------------------------
     */

    //NOTE: These getter methods could return null, check when implementing

    public String getId() {
        return id;
    }//getId

    public LatLng getLatLng() {
        return latLng;
    }//getLatLng

    public String getCity() {
        return city;
    }//getCity

    public String getCountry() {
        return country;
    }//getCountry

    public String getKey() {
        return key;
    }//getKey




    /*
        ------------------------ SETTER METHODS -----------------------------------------
     */

    public void setKey(String key) {
        this.key = key;
    }

    public void setId(String id) {
        this.id = id;
    }//setId

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }//setLatLng


    public void setCity(String city) {
        this.city = city;
    }//setCity


    public void setCountry(String country) {
        this.country = country;
    }//setCountry

    public boolean hasKey() {
        return !TextUtils.isEmpty(key);
    }
}//Loc
