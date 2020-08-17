package com.example.goodweather.fragments;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.goodweather.CityBottomSheetDialog;
import com.example.goodweather.R;
import com.example.goodweather.WeatherActivity;
import com.example.goodweather.observer.IObserver;
import com.example.goodweather.observer.Publisher;

import java.util.Objects;

public class CitySelector extends Fragment implements IObserver, CityBottomSheetDialog.BottomSheetListener {
    private RecyclerView citiesList;
    private int index = 0;
    private boolean isLandscape;
    private RecyclerAdapter adapter;
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
        if (cities == null)
            cities = getResources().getStringArray(R.array.cities);
        if (temperatures == null)
            temperatures = getResources().getStringArray(R.array.temperatures);
        isLandscape = getResources().getConfiguration().orientation
                == Configuration.ORIENTATION_LANDSCAPE;
        initList();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {
            index = savedInstanceState.getInt("index", 0);
        }

        if (isLandscape) {
            showWeather();
        }

        Publisher.getInstance().subscribe(this);
    }

    private void findViews(View view){
        citiesList = view.findViewById(R.id.cities_list_view);
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

    private void initList() {
        final CityBottomSheetDialog.BottomSheetListener bottomSheetListener = this;
        IRVOnItemClick cityListOnClick = new IRVOnItemClick() {
            @Override
            public void onItemClicked(int position) {
                index = position;
                CityBottomSheetDialog dialog = new CityBottomSheetDialog(bottomSheetListener, cities[index]);
                dialog.show(getFragmentManager(), "Диалог города");
            }
        };

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity().getBaseContext(),
                isLandscape? LinearLayoutManager.HORIZONTAL: LinearLayoutManager.VERTICAL, false);
        citiesList.setLayoutManager(layoutManager);
        adapter = new RecyclerAdapter(cities, temperatures, cityListOnClick, R.layout.city_item);
        citiesList.setAdapter(adapter);

        DividerItemDecoration itemDecoration = new DividerItemDecoration(getActivity().getBaseContext(),
                isLandscape? LinearLayout.HORIZONTAL: LinearLayout.VERTICAL);
        itemDecoration.setDrawable(getResources().getDrawable(R.drawable.city_item_separator));
        citiesList.addItemDecoration(itemDecoration);
    }

    private void showWeather() {
        if (isLandscape) {
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
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onBottomClicked(int code) {
        if (code == CityBottomSheetDialog.BUTTON_CITY_SELECT_CODE) {
            showWeather();
        }
        if (code == CityBottomSheetDialog.BUTTON_CITY_DELETE_CODE) {
            deleteCity();
        }
    }

    private void deleteCity() {
        int newLength = cities.length > 0? cities.length: 0;
        String[] newCities = new String[newLength];
        String[] newTemperatures = new String[newLength];
        for (int i = 0; i < cities.length; i++) {
            if (i < index)
                newCities[i] = cities[i];
            else if (i > index)
                newCities[i - 1] = cities[i];
        }
        for (int i = 0; i < temperatures.length; i++) {
            if (i < index)
                newTemperatures[i] = temperatures[i];
            else if (i > index)
                newTemperatures[i - 1] = temperatures[i];
        }
        cities = newCities;
        temperatures = newTemperatures;
        initList();
    }
}
