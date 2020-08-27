package com.example.goodweather.observer;

import com.example.goodweather.data.model.WeatherData;

public interface IObserver {
    void updateData(Integer idx, WeatherData weatherData);
}
