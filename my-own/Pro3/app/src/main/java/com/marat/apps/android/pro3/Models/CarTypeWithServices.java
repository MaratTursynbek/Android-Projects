package com.marat.apps.android.pro3.Models;

import java.util.ArrayList;

public class CarTypeWithServices {

    private int carTypeID;
    private String carTypeName;
    private ArrayList<Service> services;
    private int carTypeIconID;
    private String carTypeIconURL;

    public int getCarTypeID() {
        return carTypeID;
    }

    public void setCarTypeID(int carTypeID) {
        this.carTypeID = carTypeID;
    }

    public String getCarTypeName() {
        return carTypeName;
    }

    public void setCarTypeName(String carTypeName) {
        this.carTypeName = carTypeName;
    }

    public ArrayList<Service> getServices() {
        return services;
    }

    public void setServices(ArrayList<Service> services) {
        this.services = services;
    }

    public int getCarTypeIconID() {
        return carTypeIconID;
    }

    public void setCarTypeIconID(int carTypeIconID) {
        this.carTypeIconID = carTypeIconID;
    }

    public String getCarTypeIconURL() {
        return carTypeIconURL;
    }

    public void setCarTypeIconURL(String carTypeIconURL) {
        this.carTypeIconURL = carTypeIconURL;
    }
}
