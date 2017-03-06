package com.marat.apps.android.pro3.Models;

public class CarType {

    private long rowID;
    private int carTypeID;
    private String carTypeName;
    private int carTypeIconId;

    public long getRowID() {
        return rowID;
    }

    public void setRowID(long rowID) {
        this.rowID = rowID;
    }

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

    public int getCarTypeIconId() {
        return carTypeIconId;
    }

    public void setCarTypeIconId(int carTypeIconId) {
        this.carTypeIconId = carTypeIconId;
    }
}
