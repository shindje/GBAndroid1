package com.example.goodweather.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.goodweather.R;
import com.example.goodweather.observer.Publisher;

import java.util.Objects;

public class WeatherFragment extends Fragment {
    static String[][] validCityNames = {{"Москва", "moscow"}, {"Moscow", "moscow"},
            {"Лондон", "london"}, {"London", "london"},
            {"Париж", "paris"}, {"Paris", "paris"},
            {"Нью-Йорк", "new-york"}, {"New York", "new-york"}};

    TextView cityTextView, temperatureTextView,
             windTextView, pressureTextView,  windValueTextView, pressureValueTextView;
    CheckBox addInfoCheckBox;
    Button updateDataButton, yandexWeatherBtn;

    static WeatherFragment create(int index, String cityName, String temperature) {
        WeatherFragment fragment = new WeatherFragment();

        Bundle args = new Bundle();
        args.putInt("index", index);
        args.putString("cityName", cityName);
        args.putString("temperature", temperature);
        fragment.setArguments(args);
        return fragment;
    }

    int getIndex() {
        try {
            int index = Objects.requireNonNull(getArguments()).getInt("index", 0);
            return index;
        } catch (Exception e) {
            return 0;
        }
    }

    String getCityName() {
        try {
            String cityName = Objects.requireNonNull(getArguments()).getString("cityName", "");
            return cityName;
        } catch (Exception e) {
            return "";
        }
    }

    String getTemperature() {
        try {
            String temperature = Objects.requireNonNull(getArguments()).getString("temperature", "");
            return temperature;
        } catch (Exception e) {
            return "";
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.weather_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        findViews(view);
        setOnClickListeners();
        setViewsVisible();
        cityTextView.setText(getCityName());
        temperatureTextView.setText(getTemperature());
    }

    private void findViews(View view){
        windTextView = view.findViewById(R.id.windTextView);
        pressureTextView = view.findViewById(R.id.pressureTextView);
        addInfoCheckBox = view.findViewById(R.id.addInfoCheckBox);
        temperatureTextView = view.findViewById(R.id.temperature);
        updateDataButton = view.findViewById(R.id.updateDataButton);
        cityTextView = view.findViewById(R.id.cityTextView);
        windValueTextView = view.findViewById(R.id.windValueTextView);
        pressureValueTextView = view.findViewById(R.id.pressureValueTextView);
        yandexWeatherBtn = view.findViewById(R.id.yandexWeatherBtn);
    }

    private void setOnClickListeners(){
        addInfoCheckBox.setOnClickListener(view -> setViewsVisible());
        updateDataButton.setOnClickListener(view -> updateData());
        yandexWeatherBtn.setOnClickListener(view -> {
            String cityForURL = getCityforURL();
            Uri uri = Uri.parse("https://yandex.ru/pogoda/" + cityForURL);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        });
    }

    private void updateData(){
        String randomTemperature = "+" + ((int)(Math.random()*15) + 20);
        temperatureTextView.setText(randomTemperature);
        Publisher.getInstance().notify(getCityName(), randomTemperature);
    }

    private void setViewsVisible() {
        boolean showAddInfo = addInfoCheckBox.isChecked();
        windTextView.setVisibility(showAddInfo? View.VISIBLE: View.INVISIBLE);
        pressureTextView.setVisibility(showAddInfo? View.VISIBLE: View.INVISIBLE);
        windValueTextView.setVisibility(showAddInfo? View.VISIBLE: View.INVISIBLE);
        pressureValueTextView.setVisibility(showAddInfo? View.VISIBLE: View.INVISIBLE);
    }

    private String getCityforURL() {
        String curCityName = cityTextView.getText().toString();
        for (String[] names: validCityNames) {
            if (names[0].equals(curCityName))
                return names[1];
        }
        return "";
    }
}
