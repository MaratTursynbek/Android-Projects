package com.marat.apps.android.pro3.Models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class Box implements Parcelable {

    private int boxId;
    private ArrayList<TimetableRow> timetableRows;

    public int getBoxId() {
        return boxId;
    }

    public void setBoxId(int boxId) {
        this.boxId = boxId;
    }

    public ArrayList<TimetableRow> getTimetableRows() {
        return timetableRows;
    }

    public void setTimetableRows(ArrayList<TimetableRow> timetableRows) {
        this.timetableRows = timetableRows;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(boxId);
        dest.writeList(timetableRows);
    }

    public Box() {}

    private Box(Parcel in) {
        boxId = in.readInt();
        timetableRows = new ArrayList<>();
        in.readList(timetableRows, null);
    }

    public static final Creator<Box> CREATOR = new Creator<Box>() {
        @Override
        public Box createFromParcel(Parcel in) {
            return new Box(in);
        }

        @Override
        public Box[] newArray(int size) {
            return new Box[size];
        }
    };
}
