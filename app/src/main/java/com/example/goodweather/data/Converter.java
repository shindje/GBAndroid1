package com.example.goodweather.data;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.RxWorker;
import androidx.work.WorkerParameters;

import com.example.goodweather.data.model.WeatherData;
import com.google.gson.Gson;

import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.schedulers.Schedulers;

public class Converter extends RxWorker {
    public static final String PARAM_TEMP_STR = "tempStr";
    public static final String PARAM_TEMP = "temp";
    public static final String PARAM_WIND_SPEED_STR = "windSpeedStr";
    public static final String PARAM_PRESSURE_MM_STR = "pressureMMStr";

    public Converter(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Single<Result> createWork() {
        Single<Result> single = Single.create(new Converter.OnSubscribe());
        return single.subscribeOn(Schedulers.io()).observeOn(Schedulers.io())
                .onErrorReturn(e -> {
                    Data output = new Data.Builder()
                            .putString("error", e.getLocalizedMessage())
                            .build();
                    return Result.failure(output);
                });
    }

    private class OnSubscribe implements SingleOnSubscribe<Result> {
        @Override
        public void subscribe(SingleEmitter<Result> emitter) throws Exception {
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

                emitter.onSuccess(Result.success(output));
            } catch (Exception e) {
                emitter.onError(e);
            }

        }
    }
}
