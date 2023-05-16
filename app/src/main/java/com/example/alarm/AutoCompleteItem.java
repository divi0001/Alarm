package com.example.alarm;

import android.widget.ImageView;

import androidx.annotation.NonNull;

public class AutoCompleteItem {

    private String street, city, country;
    private int housenumber, postalCode;
    ImageView imgAutoComplete;

    public AutoCompleteItem(String street, String city, String country, int housenumber, int postalCode, ImageView imgAutoComplete) {
        this.street = street;
        this.city = city;
        this.country = country;
        this.housenumber = housenumber;
        this.postalCode = postalCode;
        this.imgAutoComplete = imgAutoComplete;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public int getHousenumber() {
        return housenumber;
    }

    public void setHousenumber(int housenumber) {
        this.housenumber = housenumber;
    }

    public int getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(int postalCode) {
        this.postalCode = postalCode;
    }

    public ImageView getImgAutoComplete() {
        return imgAutoComplete;
    }

    public void setImgAutoComplete(ImageView imgAutoComplete) {
        this.imgAutoComplete = imgAutoComplete;
    }

    @NonNull
    @Override
    public String toString() {
        return "AutoCompleteItem{" +
                "street='" + street + '\'' +
                ", city='" + city + '\'' +
                ", country='" + country + '\'' +
                ", housenumber=" + housenumber +
                ", postalCode=" + postalCode +
                ", imgAutoComplete=" + imgAutoComplete +
                '}';
    }
}


