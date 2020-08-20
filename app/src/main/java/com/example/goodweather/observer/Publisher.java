package com.example.goodweather.observer;

import java.util.ArrayList;
import java.util.List;

public class Publisher {
    private static Publisher instance = null;
    private List<IObserver> observers = new ArrayList<>();

    private Publisher() {}

    static public Publisher getInstance() {
        if(instance == null) {
            instance = new Publisher();
        }

        return instance;
    }

    public void subscribe(IObserver observer) {
        observers.add(observer);
    }

    public void unsubscribe(IObserver observer) {
        observers.remove(observer);
    }

    public void notify(String city, String temperature) {
        for (IObserver observer : observers) {
            observer.updateTemperature(city, temperature);
        }
    }
}