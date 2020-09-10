package com.example.goodweather.data.web.model;

import com.google.gson.annotations.SerializedName;

public class Coord {
    @SerializedName("lat") public float lat;
    @SerializedName("lon") public float lon;

    public float getLon() {
        return lon;
    }

    public void setLon(float lon) {
        this.lon = lon;
    }

    public float getLat() {
        return lat;
    }

    public void setLat(float lat) {
        this.lat = lat;
    }
}
