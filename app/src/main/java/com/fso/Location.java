package com.fso;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by hoint on 16/06/2017.
 */

public class Location implements Parcelable {
    public String Addredss;
    public String Time;
    public String Image;
    public String Latitude;
    public String Longitude;


    public Location(String addredss, String time, String imgae) {
        Addredss = addredss;
        Time = time;
        Image = imgae;
    }

    public Location(String addredss, String time, String imgae, String latitude, String longitude) {
        Addredss = addredss;
        Time = time;
        Image = imgae;
        Latitude = latitude;
        Longitude = longitude;
    }

    protected Location(Parcel in) {
        Addredss = in.readString();
        Time = in.readString();
        Image = in.readString();
        Latitude = in.readString();
        Longitude = in.readString();
    }

    public static final Creator<Location> CREATOR = new Creator<Location>() {
        @Override
        public Location createFromParcel(Parcel in) {
            return new Location(in);
        }

        @Override
        public Location[] newArray(int size) {
            return new Location[size];
        }
    };

    public String getAddredss() {
        return Addredss;
    }

    public String getTime() {
        return Time;
    }

    public String getImgae() {
        return Image;
    }

    public void setAddredss(String addredss) {
        Addredss = addredss;
    }

    public void setTime(String time) {
        Time = time;
    }

    public void setImgae(String imgae) {
        Image = imgae;
    }

    @Override
    public String toString()  {
        return "";
    }
    @Override
    public int describeContents() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int arg1) {
        // TODO Auto-generated method stub
        dest.writeString(Addredss);
        dest.writeString(Time);
        dest.writeString(Image);
        dest.writeString(Latitude);
        dest.writeString(Longitude);
    }
}
