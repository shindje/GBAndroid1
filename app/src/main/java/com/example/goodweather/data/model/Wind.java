package com.example.goodweather.data.model;

import com.google.gson.annotations.SerializedName;

public class Wind {
    @SerializedName("speed") public float speed;
    @SerializedName("deg") public int deg;

    public int getDeg() {
        return deg;
    }

    public void setDeg(int deg) {
        this.deg = deg;
    }

    public float getSpeed() {
        return speed;
    }

    public String getSpeedStr() {
        return "" + Math.round(speed);
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }
}
