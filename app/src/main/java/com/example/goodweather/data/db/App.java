package com.example.goodweather.data.db;

import android.app.Application;

import androidx.room.Room;

// паттерн синглтон, наследуем класс Application
// создаем базу данных в методе onCreate
public class App extends Application {

    private static App instance;

    // база данных
    private CityHistoryDatabase db;

    // Так получаем объект приложения
    public static App getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // Это для синглтона, сохраняем объект приложения
        instance = this;

        // строим базу
        db = Room.databaseBuilder(
                getApplicationContext(),
                CityHistoryDatabase.class,
                "city_history_database")
                .allowMainThreadQueries() //Только для примеров и тестирования. TODO
                .build();
    }

    // Получаем CityHistoryDao для составления запросов
    public CityHistoryDao getCityHistoryDao() {
        return db.getCityHistoryDao();
    }
}

