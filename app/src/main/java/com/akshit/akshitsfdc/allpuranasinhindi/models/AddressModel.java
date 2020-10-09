package com.akshit.akshitsfdc.allpuranasinhindi.models;

import java.io.Serializable;

public class AddressModel implements Serializable {

    private String name;
    private String country;
    private String state;
    private String pin;
    private String city;
    private String address;
    private String landmark;

    public AddressModel() {
    }

    public AddressModel(String name, String country, String state, String pin, String city, String address, String landmark) {
        this.setName(name);
        this.setCountry(country);
        this.setState(state);
        this.setPin(pin);
        this.setCity(city);
        this.setAddress(address);
        this.setLandmark(landmark);
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLandmark() {
        return landmark;
    }

    public void setLandmark(String landmark) {
        this.landmark = landmark;
    }
}
