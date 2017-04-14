package com.marat.apps.android.pro3.Models;

import android.os.Parcel;
import android.os.Parcelable;

public class TimetableRow implements Parcelable {

    private String time;
    private boolean available;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public TimetableRow() {
    }

    private TimetableRow(Parcel in) {
        time = in.readString();
        available = in.readByte() != 0;
    }

    public static final Creator<TimetableRow> CREATOR = new Creator<TimetableRow>() {
        @Override
        public TimetableRow createFromParcel(Parcel in) {
            return new TimetableRow(in);
        }

        @Override
        public TimetableRow[] newArray(int size) {
            return new TimetableRow[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(time);
        dest.writeByte((byte) (available ? 1 : 0));
    }
}
