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
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.Data;

import com.example.goodweather.MainActivity;
import com.example.goodweather.R;
import com.example.goodweather.data.db.App;
import com.example.goodweather.data.db.CityHistoryDao;
import com.example.goodweather.data.db.CityHistorySource;
import com.example.goodweather.data.web.RetrofitGetter;
import com.example.goodweather.observer.IObserver;
import com.example.goodweather.observer.Publisher;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CitySelector extends Fragment implements IObserver {
    private RecyclerView citiesList;
    private String cityName;
    private boolean isLandscape;
    private RecyclerAdapter adapter;
    private CityHistorySource cityHistorySource;
    private static List<String> cities;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

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
        isLandscape = getResources().getConfiguration().orientation
                == Configuration.ORIENTATION_LANDSCAPE;
        initList();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {
            cityName = savedInstanceState.getString("cityName", getDefaultCityName());
        }

        Publisher.getInstance().subscribe(this);
    }

    private void findViews(View view){
        citiesList = view.findViewById(R.id.cities_list_view);
    }


    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putString("cityName", cityName);
        super.onSaveInstanceState(outState);
    }


    @Override
    public void onDestroyView() {
        Publisher.getInstance().unsubscribe(this);
        super.onDestroyView();
    }

    private void initList() {
        IRVOnItemClick cityListOnClick = cityName -> {
            this.cityName = cityName;
            ((MainActivity)requireActivity()).setWeatherFragment();
        };

        LinearLayoutManager layoutManager = new LinearLayoutManager(requireActivity().getBaseContext(),
                isLandscape? LinearLayoutManager.HORIZONTAL: LinearLayoutManager.VERTICAL, false);
        citiesList.setLayoutManager(layoutManager);
        CityHistoryDao dao = App
                .getInstance()
                .getCityHistoryDao();
        cityHistorySource = new CityHistorySource(dao);
        adapter = new RecyclerAdapter(cityHistorySource, cityListOnClick, R.layout.city_item, (MainActivity) getActivity());
        citiesList.setAdapter(adapter);
        ((MainActivity) requireActivity()).setAdapter(adapter);
        ((MainActivity) requireActivity()).setCityHistorySource(cityHistorySource);

        DividerItemDecoration itemDecoration = new DividerItemDecoration(requireActivity().getBaseContext(),
                isLandscape? LinearLayout.HORIZONTAL: LinearLayout.VERTICAL);
        itemDecoration.setDrawable(getResources().getDrawable(R.drawable.city_item_separator));
        citiesList.addItemDecoration(itemDecoration);
    }

    @Override
    public void updateData(String cityName, Data data) {
        adapter.notifyDataSetChanged();
    }

    public static List<String> getCities(Resources resources) {
        if (cities == null) {
            cities = new ArrayList<>();
            Collections.addAll(cities, resources.getStringArray(R.array.cities));
        }
        return cities;
    }

    public static void deleteCity(int index, RecyclerAdapter adapter) {
        adapter.remove(index);
    }

    public static void addCity(Activity activity, LifecycleOwner lifecycleOwner, String cityName,
                               View view, RecyclerAdapter adapter) {
        adapter.add(cityName);
        Snackbar.make(view, cityName + ": " + activity.getResources().getString(R.string.data_updating),
                Snackbar.LENGTH_SHORT)
                .setAction("Action", null).show();
        RetrofitGetter.getData(activity.getApplicationContext(), lifecycleOwner, cityName,
                activity, null, null, view);
    }

    private String getDefaultCityName() {
        if (getActivity() != null) {
            return ((MainActivity)getActivity()).getDefaultCityName();
        } else
            return null;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }
}