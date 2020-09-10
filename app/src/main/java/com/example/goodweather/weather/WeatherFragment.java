package com.example.goodweather.weather;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.Data;

import com.example.goodweather.MainActivity;
import com.example.goodweather.R;
import com.example.goodweather.custom.TemperatureView;
import com.example.goodweather.data.web.Converter;
import com.example.goodweather.data.web.RetrofitGetter;
import com.example.goodweather.data.web.RunnableWithData;
import com.example.goodweather.observer.IObserver;
import com.example.goodweather.observer.Publisher;
import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.Arrays;
import java.util.List;

public class WeatherFragment extends Fragment implements IObserver {
    private static String[] webCityNames;

    private TemperatureView temperatureView;
    private TextView cityTextView, temperatureTextView, windTextView, pressureTextView,
            windValueTextView, pressureValueTextView, weatherDescriptionTextView;
    private CheckBox addInfoCheckBox;
    private Button updateDataButton, yandexWeatherBtn;
    private RecyclerView forecasList;
    private ImageView weatherIconView;
    private ProgressBar weatherIconProgressBar;

    public static WeatherFragment create(String cityName) {
        WeatherFragment fragment = new WeatherFragment();

        Bundle args = new Bundle();
        args.putString("cityName", cityName);
        fragment.setArguments(args);
        return fragment;
    }

    String getCityName() {
        try {
            return requireArguments().getString("cityName", "");
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
        temperatureTextView.setText(((MainActivity)requireActivity()).getDefaultTemperature());
        webCityNames = getResources().getStringArray(R.array.web_city_names);
    }

    @Override
    public void onResume() {
        super.onResume();
        Publisher.getInstance().subscribe(this);
        if (!isHidden()) {
            ((MainActivity)requireActivity()).setLastCityName(getCityName());
            getData(weatherIconView);
        }
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
        weatherDescriptionTextView = view.findViewById(R.id.weatherDescription);
        weatherIconView = view.findViewById(R.id.weatherIcon);
        weatherIconProgressBar = view.findViewById(R.id.weatherIconProgressBar);
    }

    private void initList() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireActivity().getBaseContext(),
                LinearLayoutManager.VERTICAL, false);

        forecasList.setLayoutManager(layoutManager);
        List<String> forecastItems = Arrays.asList(getResources().getStringArray(R.array.forecast_items));
        //TODO
//        RecyclerAdapter forecasListAdapter = new RecyclerAdapter(forecastItems, null,
//                R.layout.forecast_item, (MainActivity) getActivity());
//        forecasList.setAdapter(forecasListAdapter);

        DividerItemDecoration forecasListItemDecoration = new DividerItemDecoration(requireActivity().getBaseContext(),
                LinearLayout.VERTICAL);
        forecasListItemDecoration.setDrawable(getResources().getDrawable(R.drawable.forecast_item_separator));
        forecasList.addItemDecoration(forecasListItemDecoration);
    }

    private void setOnClickListeners(){
        addInfoCheckBox.setOnClickListener(view -> setViewsVisible());
        updateDataButton.setOnClickListener(this::getData);
        weatherIconView.setOnClickListener(this::getData);
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

    private void updateViews(Data data) {
        if (isResumed()) {
            temperatureTextView.setText(data.getString(Converter.PARAM_TEMP_STR));
            float temp = data.getFloat(Converter.PARAM_TEMP, Float.MAX_VALUE);
            temperatureView.setTemperature(temp == Float.MAX_VALUE ? null : Math.round(temp));
            windValueTextView.setText(data.getString(Converter.PARAM_WIND_SPEED_STR));
            pressureValueTextView.setText(data.getString(Converter.PARAM_PRESSURE_MM_STR));
            weatherDescriptionTextView.setText(data.getString(Converter.PARAM_DESCRIPTION));
            weatherIconProgressBar.setVisibility(View.VISIBLE);
            weatherIconView.setVisibility(View.INVISIBLE);
            weatherIconView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            Picasso.get()
                    .load("http://openweathermap.org/img/wn/" + data.getString(Converter.PARAM_ICON) + "@4x.png")
                    .into(weatherIconView, new Callback() {
                        @Override
                        public void onSuccess() {
                            weatherIconProgressBar.setVisibility(View.INVISIBLE);
                            weatherIconView.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onError(Exception e) {
                            weatherIconProgressBar.setVisibility(View.INVISIBLE);
                            weatherIconView.setImageDrawable(requireActivity().getDrawable(R.drawable.ic_baseline_info_50));
                            weatherIconView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                            weatherIconView.setVisibility(View.VISIBLE);
                        }
                    });
        }
    }

    private void setViewsVisible() {
        boolean showAddInfo = addInfoCheckBox.isChecked();
        windTextView.setVisibility(showAddInfo? View.VISIBLE: View.INVISIBLE);
        pressureTextView.setVisibility(showAddInfo? View.VISIBLE: View.INVISIBLE);
        windValueTextView.setVisibility(showAddInfo? View.VISIBLE: View.INVISIBLE);
        pressureValueTextView.setVisibility(showAddInfo? View.VISIBLE: View.INVISIBLE);
    }

    private String getCityforURL() {
        int index = -1;
        List<String> cities = CitySelector.getCities(getResources());
        for (int i = 0; i < cities.size(); i++) {
            if (cities.get(i).equals(getCityName()))
                index = i;
        }
        if (index > -1 && index < webCityNames.length)
            return webCityNames[index];
        else
            return "";
    }

    @Override
    public void updateData(String cityName, Data data) {
        if (cityName.equals(getCityName())) {
            updateViews(data);
        }
    }

    RunnableWithData onUpdateDataAction = new RunnableWithData() {
        @Override
        public void run() {
            updateViews(data);
            ((MainActivity)requireActivity()).addHistory(getCityName(), data.getString(Converter.PARAM_TEMP_STR));
        }
    };
    RunnableWithData onUpdateErrorAction = new RunnableWithData() {
        @Override
        public void run() {
            weatherIconProgressBar.setVisibility(View.INVISIBLE);
            weatherIconView.setVisibility(View.VISIBLE);
        }
    };

    private void getData(View view) {
        Snackbar.make(view, getCityName() + ": " + getString(R.string.data_updating), Snackbar.LENGTH_SHORT)
                .setAction("Action", null).show();
        RetrofitGetter.getData(requireContext(), getViewLifecycleOwner(), getCityName(),
                getActivity(), onUpdateDataAction, onUpdateErrorAction, requireView());
    }
}
