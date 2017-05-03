package com.marat.apps.android.pro3.Models;

public class TimetableRow {

    private String time;
    private int boxId;
    private boolean available = false;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getBoxId() {
        return boxId;
    }

    public void setBoxId(int boxId) {
        this.boxId = boxId;
        available = true;
    }

    public boolean isAvailable() {
        return available;
    }
}
