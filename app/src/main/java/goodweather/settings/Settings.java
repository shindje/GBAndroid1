package goodweather.settings;

import android.content.Context;
import android.content.SharedPreferences;

import goodweather.MainActivity;

import static android.content.Context.MODE_PRIVATE;

public class Settings {
    private boolean isDarkTheme;
    private boolean updateOnStart;
    private boolean updateInBackgorund;
    private String token;
    private Context context;

    static private Settings instance;

    public static Settings getInstance(Context context) {
        getInstance().context = context;
        return instance;
    }

    private static Settings getInstance() {
        if (instance == null) {
            loadSettings();
        }
        return instance;
    }

    private static void loadSettings() {
        instance = new Settings();
        instance.isDarkTheme = false;
        instance.updateOnStart = true;
        instance.updateInBackgorund = false;
    }

    public boolean isDarkTheme() {
        return isDarkTheme;
    }

    public boolean isUpdateOnStart() {
        return updateOnStart;
    }

    public boolean isUpdateInBackgorund() {
        return updateInBackgorund;
    }

    public String getToken() {
        if (token == null) {
            SharedPreferences sharedPref = context.getSharedPreferences(
                    MainActivity.class.getSimpleName(),
                    MODE_PRIVATE);
            token = sharedPref.getString("token", "");
        }
        return token;
    }

    public void setToken(String token) {
        this.token = token;
        SharedPreferences sharedPref = context.getSharedPreferences(
                MainActivity.class.getSimpleName(),
                MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("token", token);
        editor.apply();
    }

    public void setDarkTheme(boolean darkTheme) {
        isDarkTheme = darkTheme;
    }

    public void setUpdateOnStart(boolean updateOnStart) {
        this.updateOnStart = updateOnStart;
    }

    public void setUpdateInBackgorund(boolean updateInBackgorund) {
        this.updateInBackgorund = updateInBackgorund;
    }
}
