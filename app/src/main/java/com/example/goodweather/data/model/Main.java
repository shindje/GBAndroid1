package com.example.goodweather.data.model;

import java.util.Locale;

public class Main {
    private float temp;
    private int pressure;
    private int humidity;
    public static final double GPA_TO_MMRTS = 0.750064;

    public float getTemp() {
        return temp;
    }

    public String getTempStr() {
        return String.format(Locale.getDefault(), "%+d", Math.round(temp));
    }

    public void setTemp(float temp) {
        this.temp = temp;
    }

    public int getPressure() {
        return pressure;
    }

    public String getPressureMMStr() {
        return "" + Math.round(pressure * GPA_TO_MMRTS);
    }

    public void setPressure(int pressure) {
        this.pressure = pressure;
    }

    public int getHumidity() {
        return humidity;
    }

    public void setHumidity(int humidity) {
        this.humidity = humidity;
    }
}
