package com.example.goodweather.fragments;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.goodweather.R;
import com.example.goodweather.WeatherActivity;
import com.example.goodweather.observer.IObserver;
import com.example.goodweather.observer.Publisher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CitySelector extends Fragment implements IObserver {
    private ListView citiesListView;
    private int index = 0;
    private boolean isLandscape;
    private ArrayList<Map<String, Object>> adapterData;
    private SimpleAdapter adapter;
    private static String[] cities;
    private static String[] temperatures;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.city_selector, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        findViews(view);
        cities = getResources().getStringArray(R.array.cities);
        if (temperatures == null)
            temperatures = getResources().getStringArray(R.array.temperatures);
        initAdapterData();
        initList();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        isLandscape = getResources().getConfiguration().orientation
                == Configuration.ORIENTATION_LANDSCAPE;

        if (savedInstanceState != null) {
            index = savedInstanceState.getInt("index", 0);
        }

        if (isLandscape) {
            citiesListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
            showWeather();
        }

        Publisher.getInstance().subscribe(this);
    }

    private void findViews(View view){
        citiesListView = view.findViewById(R.id.cities_list_view);
    }


    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putInt("index", index);
        super.onSaveInstanceState(outState);
    }


    @Override
    public void onDestroyView() {
        Publisher.getInstance().unsubscribe(this);
        super.onDestroyView();
    }

    private void initAdapterData() {
        adapterData = new ArrayList<Map<String, Object>>(cities.length);
        Map <String, Object> m;
        for (int i = 0; i < cities.length; i++) {
            m = new HashMap<String, Object>();
            m.put("city", cities[i]);
            m.put("temperature", temperatures[i]);
            adapterData.add(m);
        }
    }

    private void initList() {
        adapter = new SimpleAdapter(getActivity(), adapterData, R.layout.city_item,
                new String[]{"city", "temperature"},
                new int[] {R.id.cityItemNameTextView, R.id.cityItemTemperatureTextView});

        citiesListView.setAdapter(adapter);

        citiesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                index = position;
                showWeather();
            }
        });
    }

    private void showWeather() {
        if (isLandscape) {
            citiesListView.setItemChecked(index, true);
            WeatherFragment detail = (WeatherFragment)
                    Objects.requireNonNull(getFragmentManager()).findFragmentById(R.id.weather_fragment);
            if (detail == null || detail.getIndex() != index) {
                detail = WeatherFragment.create(index, cities[index], temperatures[index]);

                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.weather_fragment, detail);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.addToBackStack(null);
                ft.commit();
            }
        } else {
            Intent intent = new Intent();
            intent.setClass(Objects.requireNonNull(getActivity()), WeatherActivity.class);

            intent.putExtra("index", index);
            intent.putExtra("cityName", cities[index]);
            intent.putExtra("temperature", temperatures[index]);
            startActivity(intent);
        }
    }

    @Override
    public void updateTemperature(String city, String temperature) {
        temperatures[index] = temperature;
        for (Map <String, Object> m : adapterData) {
            if (m.get("city").equals(city))
                m.put("temperature", temperature);
        }
        adapter.notifyDataSetChanged();
    }
}
