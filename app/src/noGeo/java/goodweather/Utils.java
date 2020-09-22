package goodweather;

import android.app.Activity;
import android.content.SharedPreferences;

import com.example.goodweather.R;

import static android.content.Context.MODE_PRIVATE;

public class Utils {
    private static final String SHARED_PREF_LAST_CITY = "last_city";

    public static String getLastCityName(Activity activity) {
        SharedPreferences sharedPref = activity.getPreferences(MODE_PRIVATE);
        return sharedPref.getString(SHARED_PREF_LAST_CITY, activity.getString(R.string.defaultCity));
    }

    public static void setLastCityName(String cityName, Activity activity) {
        SharedPreferences sharedPref = activity.getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(SHARED_PREF_LAST_CITY, cityName);
        editor.apply();
    }
}
