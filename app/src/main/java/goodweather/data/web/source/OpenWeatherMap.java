package goodweather.data.web.source;

import com.example.goodweather.BuildConfig;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;

public class OpenWeatherMap {
    private static final String WEATHER_URL = "https://api.openweathermap.org/data/2.5/weather?q=%s&units=metric&appid=%s";

    public static String getWeather(String cityName, String cityNotFound) throws IOException {
        //API ключ хранится в корне проекта в файле apikey.properties в виде строки:
        //weather_api_key = "ключ"
        //Он необходим для сборки!
        String cityUrl = String.format(WEATHER_URL, cityName, BuildConfig.WEATHER_API_KEY);
        final URL uri = new URL(cityUrl);
        HttpsURLConnection urlConnection = null;
        try {
            urlConnection = (HttpsURLConnection) uri.openConnection();
            urlConnection.setRequestMethod("GET"); // установка метода получения данных -GET
            urlConnection.setReadTimeout(10000); // установка таймаута - 10 000 миллисекунд
            BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream())); // читаем  данные в поток
            return getLines(in);
        } catch (IOException e) {
            if (e instanceof FileNotFoundException)
                throw new IOException(cityNotFound);
            else
                throw e;
        } finally {
            if (null != urlConnection) {
                urlConnection.disconnect();
            }
        }
    }

    private static String getLines(BufferedReader in) throws IOException{
        StringBuilder sb = new StringBuilder();
        String line = in.readLine();
        while (line != null) {
            sb.append(line).append("\n");
            line = in.readLine();
        }
        return sb.toString();
    }
}
