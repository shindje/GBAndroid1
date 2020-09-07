package com.example.goodweather.data.model;

import com.google.gson.annotations.SerializedName;

public class WeatherData {
    @SerializedName("coord") public Coord coord;
    @SerializedName("weather") public Weather[] weather;
    @SerializedName("main") public Main main;
    @SerializedName("wind") public Wind wind;
    @SerializedName("clouds") public Clouds clouds;
    @SerializedName("name") public String name;

    public Coord getCoord() {
        return coord;
    }

    public void setCoord(Coord coord) {
        this.coord = coord;
    }

    public Weather[] getWeather() {
        return weather;
    }

    public void setWeather(Weather[] weather) {
        this.weather = weather;
    }

    public Main getMain() {
        return main;
    }

    public void setMain(Main main) {
        this.main = main;
    }

    public Wind getWind() {
        return wind;
    }

    public void setWind(Wind wind) {
        this.wind = wind;
    }

    public Clouds getClouds() {
        return clouds;
    }

    public void setClouds(Clouds clouds) {
        this.clouds = clouds;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
