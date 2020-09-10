package com.example.goodweather.data.db.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Date;

@Entity(primaryKeys = {CityHistory.CITY_NAME, CityHistory.DATE})
public class CityHistory {
    public final static String CITY_NAME = "city_name";
    public final static String DATE = "date";

    @ColumnInfo(name = CITY_NAME)
    @NonNull
    public String cityName;

    @NonNull
    public Date date;

    public Time time;

    public int temperature;

    public String getDateText() {
        return new SimpleDateFormat("dd/MM/yyy").format(date);
    }

    public String getTimeText() {
        if (time == null)
            return "";
        else
            return " " + new SimpleDateFormat("HH:mm").format(time);
    }
}
