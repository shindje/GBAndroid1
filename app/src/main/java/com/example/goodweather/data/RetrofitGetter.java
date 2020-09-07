package com.example.goodweather.data;

import android.app.Activity;
import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.work.Data;

import com.example.goodweather.BuildConfig;
import com.example.goodweather.R;
import com.example.goodweather.data.model.WeatherData;
import com.example.goodweather.data.source.OpenWeatherRepo;
import com.example.goodweather.observer.Publisher;
import com.google.android.material.snackbar.Snackbar;

import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RetrofitGetter {
    public static void getData(Context context, LifecycleOwner lifecycleOwner, String cityName, int idx,
                               Activity activity, RunnableWithData onFinishAction, View view) {
        OpenWeatherRepo.getInstance().getAPI().loadWeather(cityName,
                BuildConfig.WEATHER_API_KEY, "metric", Locale.getDefault().getLanguage())
                .enqueue(new Callback<WeatherData>() {
                    @Override
                    public void onResponse(@NonNull Call<WeatherData> call,
                                           @NonNull Response<WeatherData> response) {
                        if (response.body() != null && response.isSuccessful()) {
                            Data data = Converter.convert(response.body());

                            Publisher.getInstance().notify(idx, data);

                            if (activity != null && view != null) {
                                Snackbar.make(view, cityName + ": " + activity.getString(R.string.data_updated), Snackbar.LENGTH_SHORT)
                                        .setAction("Action", null).show();
                            }

                            if (onFinishAction != null) {
                                onFinishAction.setData(data);
                                activity.runOnUiThread(onFinishAction);
                            }
                        } else {
                            Publisher.getInstance().notify(idx, new Data.Builder().build());
                            if (activity != null && view != null) {
                                String error = activity.getString(R.string.city_not_foud);

                                Snackbar.make(view, cityName + ": " + activity.getString(R.string.error_getting_data) + ": " + error, Snackbar.LENGTH_LONG)
                                        .setAction("Action", null).show();
                            }
                        }
                    }

                    //сбой при интернет подключении
                    @Override
                    public void onFailure(Call<WeatherData> call, Throwable t) {
                        Publisher.getInstance().notify(idx, new Data.Builder().build());
                        if (activity != null && view != null) {
                            String error = t.getLocalizedMessage();

                            Snackbar.make(view, cityName + ": " + activity.getString(R.string.error_getting_data) + ": " + error, Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        }
                    }
                });
    }
}
