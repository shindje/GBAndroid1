package goodweather.data.web;

import androidx.work.Data;

import goodweather.data.web.model.WeatherData;

public class Converter {
    public static final String PARAM_TEMP_STR = "tempStr";
    public static final String PARAM_TEMP = "temp";
    public static final String PARAM_WIND_SPEED_STR = "windSpeedStr";
    public static final String PARAM_PRESSURE_MM_STR = "pressureMMStr";
    public static final String PARAM_DESCRIPTION = "description";
    public static final String PARAM_ICON = "icon";
    public static final String PARAM_NAME = "name";

    public static Data convert(WeatherData weatherData) {
        return new Data.Builder()
                .putString(PARAM_TEMP_STR, weatherData.getMain().getTempStr())
                .putFloat(PARAM_TEMP, weatherData.getMain().getTemp())
                .putString(PARAM_WIND_SPEED_STR, weatherData.getWind().getSpeedStr())
                .putString(PARAM_PRESSURE_MM_STR, weatherData.getMain().getPressureMMStr())
                .putString(PARAM_DESCRIPTION, weatherData.getWeather()[0].getDescription())
                .putString(PARAM_ICON, weatherData.getWeather()[0].getIcon())
                .putString(PARAM_NAME, weatherData.getName())
                .build();
    }
}
