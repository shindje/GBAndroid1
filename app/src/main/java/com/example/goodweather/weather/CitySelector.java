package com.example.goodweather.weather;

import android.app.Activity;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.Data;

import com.example.goodweather.MainActivity;
import com.example.goodweather.R;
import com.example.goodweather.data.Converter;
import com.example.goodweather.data.RetrofitGetter;
import com.example.goodweather.observer.IObserver;
import com.example.goodweather.observer.Publisher;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CitySelector extends Fragment implements IObserver {
    private RecyclerView citiesList;
    private int index = 0;
    private boolean isLandscape;
    private RecyclerAdapter adapter;
    private static List<String> cities;
    private static List<String> temperatures;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.city_selector, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        findViews(view);
        if (cities == null) {
            cities = new ArrayList<>();
            cities.addAll(Arrays.asList(getResources().getStringArray(R.array.cities)));
        }
        if (temperatures == null) {
            temperatures = new ArrayList<>();
            for (int i = 0; i < cities.size(); i++) {
                temperatures.add(getString(R.string.default_temperature));
            }
        }
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
        IRVOnItemClick cityListOnClick = position -> {
            index = position;
            showWeather();
        };

        LinearLayoutManager layoutManager = new LinearLayoutManager(requireActivity().getBaseContext(),
                isLandscape? LinearLayoutManager.HORIZONTAL: LinearLayoutManager.VERTICAL, false);
        citiesList.setLayoutManager(layoutManager);
        adapter = new RecyclerAdapter(cities, temperatures, cityListOnClick, R.layout.city_item, getActivity());
        citiesList.setAdapter(adapter);
        ((MainActivity) requireActivity()).setAdapter(adapter);

        DividerItemDecoration itemDecoration = new DividerItemDecoration(requireActivity().getBaseContext(),
                isLandscape? LinearLayout.HORIZONTAL: LinearLayout.VERTICAL);
        itemDecoration.setDrawable(getResources().getDrawable(R.drawable.city_item_separator));
        citiesList.addItemDecoration(itemDecoration);
    }

    private void showWeather() {
        WeatherFragment detail;
        if (isLandscape) {
            detail = (WeatherFragment)
                    getParentFragmentManager().findFragmentById(R.id.weather_fragment);
            if (detail == null || detail.getIndex() != index) {
                detail = WeatherFragment.create(index, cities.get(index), temperatures.get(index));

                FragmentTransaction ft = getParentFragmentManager().beginTransaction();
                ft.replace(R.id.weather_fragment, detail);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.addToBackStack(null);
                ft.commit();
            }
        } else {
            detail = WeatherFragment.create(index, cities.get(index), temperatures.get(index));
            ((MainActivity) requireActivity()).setFragment(detail);
        }
    }

    @Override
    public void updateData(Integer idx, Data data) {
        temperatures.set(idx, data.getString(Converter.PARAM_TEMP_STR));
        adapter.notifyItemChanged(idx);
    }

    public static List<String> getCities(Resources resources) {
        if (cities == null) {
            cities = new ArrayList<>();
            Collections.addAll(cities, resources.getStringArray(R.array.cities));
        }
        return cities;
    }

    public static void deleteCity(int index, RecyclerAdapter adapter) {
        cities.remove(index);
        temperatures.remove(index);
        adapter.notifyDataSetChanged();
    }

    public static void addCity(Activity activity, LifecycleOwner lifecycleOwner, String cityName,
                               String temperature, View view, RecyclerAdapter adapter) {
        cities.add(cityName);
        temperatures.add(temperature);
        adapter.notifyDataSetChanged();
        Snackbar.make(view, cityName + ": " + activity.getResources().getString(R.string.data_updating),
                Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
        RetrofitGetter.getData(activity.getApplicationContext(), lifecycleOwner, cityName, cities.size() - 1,
                activity, null, view);
    }
}