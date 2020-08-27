package com.example.goodweather.settings;

public class Settings {
    boolean isDarkTheme;
    boolean updateOnStart;
    boolean updateInBackgorund;

    static private Settings instance;

    public static Settings getInstance() {
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
}
