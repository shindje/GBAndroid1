package com.example.goodweather.data.source;

import com.example.goodweather.data.model.WeatherData;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface IOpenWeather {
    @GET("data/2.5/weather")
    Call<WeatherData> loadWeather(@Query("q") String city,
                                  @Query("appid") String keyApi,
                                  @Query("units") String units,
                                  @Query("lang") String lang);
}