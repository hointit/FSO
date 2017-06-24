package com.fso;

/**
 * Created by hoint on 16/06/2017.
 */

public class LocationData {
    public String Latitude;
    public String Longitude;
    public String Time;

    public LocationData() {
        // mmmmm
    }

    public LocationData(String latitude, String longitude, String time) {
        Latitude = latitude;
        Longitude = longitude;
        Time = time;
    }

    public String toString(){
        return "Latitude: " + this.Latitude +
                ".   Longitude: " + this.Longitude +
                ".   Time: " + this.Time;
    }
}
