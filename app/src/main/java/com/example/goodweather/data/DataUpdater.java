package com.example.goodweather.data;

import android.os.Handler;
import android.util.Log;
import android.view.View;

import com.example.goodweather.data.model.WeatherData;
import com.example.goodweather.observer.Publisher;
import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.util.List;

public class DataUpdater {

    public abstract static class WeatherRequestRunnable implements Runnable {
        protected WeatherData weatherData;

        public void setWeatherData(WeatherData weatherData) {
            this.weatherData = weatherData;
        }
    }

    public static void updateData(Handler handler, View view, List<String> cities, Integer idx, WeatherRequestRunnable actionOk){
        new Thread(new Runnable() {
            public void run() {
                String cityName = cities.get(idx);
                try {
                    WeatherData weatherData = Web.getWeather(cityName);

                    handler.post(() -> {
                        Publisher.getInstance().notify(idx, weatherData);
                        if (view != null) {
                            Snackbar.make(view, cityName + ": данные обновлены", Snackbar.LENGTH_SHORT)
                                    .setAction("Action", null).show();
                        }
                    });
                    if (actionOk != null) {
                        actionOk.setWeatherData(weatherData);
                        handler.post(actionOk);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    if (view != null) {
                        Snackbar.make(view, cityName + ": ошибка получения данных: " + e.getMessage() , Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }
                }
            }
        }).start();
    }
}
