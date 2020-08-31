package com.example.goodweather.weather;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.goodweather.R;
import com.example.goodweather.custom.TemperatureView;
import com.example.goodweather.data.DataUpdater;
import com.example.goodweather.data.model.WeatherData;
import com.example.goodweather.observer.IObserver;
import com.example.goodweather.observer.Publisher;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class WeatherFragment extends Fragment implements IObserver {
    private static String[] webCityNames;

    private TemperatureView temperatureView;
    private TextView cityTextView, temperatureTextView,
             windTextView, pressureTextView,  windValueTextView, pressureValueTextView;
    private CheckBox addInfoCheckBox;
    private Button updateDataButton, yandexWeatherBtn;
    private RecyclerView forecasList;

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
            return requireArguments().getInt("index", 0);
        } catch (Exception e) {
            return 0;
        }
    }

    String getCityName() {
        try {
            return requireArguments().getString("cityName", "");
        } catch (Exception e) {
            return "";
        }
    }

    String getTemperature() {
        try {
            return requireArguments().getString("temperature", "");
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
        initList();
        setOnClickListeners();
        setViewsVisible();
        cityTextView.setText(getCityName());
        temperatureTextView.setText(getTemperature());
        temperatureView.setTemperature(getTemperature());
        webCityNames = getResources().getStringArray(R.array.web_city_names);
    }

    @Override
    public void onResume() {
        super.onResume();
        Publisher.getInstance().subscribe(this);
    }

    @Override
    public void onPause() {
        Publisher.getInstance().unsubscribe(this);
        super.onPause();
    }

    private void findViews(View view){
        windTextView = view.findViewById(R.id.windTextView);
        pressureTextView = view.findViewById(R.id.pressureTextView);
        addInfoCheckBox = view.findViewById(R.id.addInfoCheckBox);
        temperatureTextView = view.findViewById(R.id.temperature);
        temperatureView = view.findViewById(R.id.temperatureView);
        updateDataButton = view.findViewById(R.id.updateDataButton);
        cityTextView = view.findViewById(R.id.cityTextView);
        windValueTextView = view.findViewById(R.id.windValueTextView);
        pressureValueTextView = view.findViewById(R.id.pressureValueTextView);
        yandexWeatherBtn = view.findViewById(R.id.yandexWeatherBtn);
        forecasList = view.findViewById(R.id.forecast_list_view);
    }

    private void initList() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireActivity().getBaseContext(),
                LinearLayoutManager.VERTICAL, false);

        forecasList.setLayoutManager(layoutManager);
        List<String> forecastItems = Arrays.asList(getResources().getStringArray(R.array.forecast_items));
        List<String> forecastValues = new ArrayList<>();
        for (int i = 0; i < forecastItems.size(); i++) {
            forecastValues.add(WeatherFragment.getRandomTemperature());
        }
        RecyclerAdapter forecasListAdapter = new RecyclerAdapter(forecastItems,
                forecastValues, null, R.layout.forecast_item, getActivity());
        forecasList.setAdapter(forecasListAdapter);

        DividerItemDecoration forecasListItemDecoration = new DividerItemDecoration(requireActivity().getBaseContext(),
                LinearLayout.VERTICAL);
        forecasListItemDecoration.setDrawable(getResources().getDrawable(R.drawable.forecast_item_separator));
        forecasList.addItemDecoration(forecasListItemDecoration);
    }

    private void setOnClickListeners(){
        addInfoCheckBox.setOnClickListener(view -> setViewsVisible());
        updateDataButton.setOnClickListener(view -> updateViews());
        yandexWeatherBtn.setOnClickListener(view -> {
            String cityForURL = getCityforURL();
            Uri uri = Uri.parse("https://yandex.ru/pogoda/" + cityForURL);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        });
    }

    public static String getRandomTemperature() {
        return "+" + ((int)(Math.random()*15) + 20);
    }

    private void updateViews() {
        DataUpdater.WeatherRequestRunnable ok = new DataUpdater.WeatherRequestRunnable() {
            @Override
            public void run() {
                updateViews(weatherData);
            }
        };
        DataUpdater.updateData(new Handler(), getView(), CitySelector.getCities(getResources()), getIndex(), ok);
    }

    private void updateViews(WeatherData weatherData) {
        temperatureTextView.setText(weatherData.getMain().getTempStr());
        temperatureView.setTemperature(Math.round(weatherData.getMain().getTemp()));
        windValueTextView.setText(weatherData.getWind().getSpeedStr());
        pressureValueTextView.setText(weatherData.getMain().getPressureMMStr());
    }

    private void setViewsVisible() {
        boolean showAddInfo = addInfoCheckBox.isChecked();
        windTextView.setVisibility(showAddInfo? View.VISIBLE: View.INVISIBLE);
        pressureTextView.setVisibility(showAddInfo? View.VISIBLE: View.INVISIBLE);
        windValueTextView.setVisibility(showAddInfo? View.VISIBLE: View.INVISIBLE);
        pressureValueTextView.setVisibility(showAddInfo? View.VISIBLE: View.INVISIBLE);
    }

    private String getCityforURL() {
        int index = getIndex();
        if (index < webCityNames.length)
            return webCityNames[index];
        else
            return "";
    }

    @Override
    public void updateData(Integer idx, WeatherData weatherData) {
        if (idx == getIndex()) {
            updateViews(weatherData);
        }
    }
}
