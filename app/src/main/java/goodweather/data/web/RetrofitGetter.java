package goodweather.data.web;

import android.app.Activity;
import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.work.Data;

import com.example.goodweather.BuildConfig;
import com.example.goodweather.R;

import goodweather.data.web.model.WeatherData;
import goodweather.data.web.source.OpenWeatherRepo;
import goodweather.observer.Publisher;
import com.google.android.material.snackbar.Snackbar;

import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RetrofitGetter {
    public static void getData(Context context, LifecycleOwner lifecycleOwner, String cityName, String lat, String lon,
                               Activity activity, RunnableWithData onOkAction, RunnableWithData onErrorAction,
                               View view) {
        OpenWeatherRepo.getInstance().getAPI().loadWeather(cityName, lat, lon,
                BuildConfig.WEATHER_API_KEY, "metric", Locale.getDefault().getLanguage())
                .enqueue(new Callback<WeatherData>() {
                    @Override
                    public void onResponse(@NonNull Call<WeatherData> call,
                                           @NonNull Response<WeatherData> response) {
                        if (response.body() != null && response.isSuccessful()) {
                            Data data = Converter.convert(response.body());

                            Publisher.getInstance().notify(cityName, data);

                            if (onOkAction != null && activity != null) {
                                onOkAction.setData(data);
                                activity.runOnUiThread(onOkAction);
                            }
                        } else {
                            Publisher.getInstance().notify(cityName, new Data.Builder().build());
                            if (activity != null && view != null && view.isShown()) {
                                String error = activity.getString(R.string.city_not_foud);

                                Snackbar.make(view, cityName + ": " + activity.getString(R.string.error_getting_data) + ": " + error, Snackbar.LENGTH_LONG)
                                        .setAction("Action", null).show();
                            }
                            if (onErrorAction != null && activity != null) {
                                onErrorAction.setData(null);
                                activity.runOnUiThread(onErrorAction);
                            }
                        }
                    }

                    //сбой при интернет подключении
                    @Override
                    public void onFailure(Call<WeatherData> call, Throwable t) {
                        t.printStackTrace();
                        Publisher.getInstance().notify(cityName, new Data.Builder().build());
                        if (activity != null && view != null && view.isShown()) {
                            String error = t.getLocalizedMessage();

                            Snackbar.make(view, cityName + ": " + activity.getString(R.string.error_getting_data) + ": " + error, Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        }
                        if (onErrorAction != null && activity != null) {
                            onErrorAction.setData(null);
                            activity.runOnUiThread(onErrorAction);
                        }
                    }
                });
    }
}
