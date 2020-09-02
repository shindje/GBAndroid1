package com.example.goodweather.data;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.goodweather.data.model.WeatherData;
import com.google.gson.Gson;

public class Converter extends Worker {
    public static final String PARAM_TEMP_STR = "tempStr";
    public static final String PARAM_TEMP = "temp";
    public static final String PARAM_WIND_SPEED_STR = "windSpeedStr";
    public static final String PARAM_PRESSURE_MM_STR = "pressureMMStr";

    public Converter(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        String stringData = getInputData().getString("stringData");
        try {
            Gson gson = new Gson();
            final WeatherData weatherData = gson.fromJson(stringData, WeatherData.class);

            Data output = new Data.Builder()
                    .putString(PARAM_TEMP_STR, weatherData.getMain().getTempStr())
                    .putFloat(PARAM_TEMP, weatherData.getMain().getTemp())
                    .putString(PARAM_WIND_SPEED_STR, weatherData.getWind().getSpeedStr())
                    .putString(PARAM_PRESSURE_MM_STR, weatherData.getMain().getPressureMMStr())
                    .build();
            return Result.success(output);
        } catch (Exception e) {
            e.printStackTrace();
            Data output = new Data.Builder()
                    .putString("error", e.getLocalizedMessage())
                    .build();
            return Result.failure(output);
        }
    }
}
