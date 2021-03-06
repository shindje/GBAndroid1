package goodweather.data.web.source;

import goodweather.data.web.model.WeatherData;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface IOpenWeather {
    @GET("data/2.5/weather")
    Call<WeatherData> loadWeather(@Query("q") String city,
                                  @Query("lat") String lat,
                                  @Query("lon") String lon,
                                  @Query("appid") String keyApi,
                                  @Query("units") String units,
                                  @Query("lang") String lang);
}