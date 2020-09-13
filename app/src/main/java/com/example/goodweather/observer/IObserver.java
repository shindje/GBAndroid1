package com.example.goodweather.observer;

import androidx.work.Data;

public interface IObserver {
    void updateData(String cityName, Data data);
}
