package com.example.goodweather;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    static String[][] validCityNames = {{"Москва", "moscow"}, {"Moscow", "moscow"},
                                        {"Лондон", "london"}, {"London", "london"},
                                        {"Париж", "paris"}, {"Paris", "paris"},
                                        {"Нью-Йорк", "new-york"}, {"New York", "new-york"}};

    TextView windTextView, pressureTextView, cityTextView, temperatureTextView, windValueTextView,
            pressureValueTextView;
    CheckBox addInfoCheckBox;
    Button updateDataButton, selectCityBtn, yandexWeatherBtn;

    final static String temperatureKey = "temperature";
    final static String cityKey = "city";
    final static String windValueKey = "windValue";
    final static String pressureValueKey = "pressureValue";
    final static int cityRequestCode = 100;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViews();
        setOnClickListeners();
        setViewsVisible();
        onLifeCycle("onCreate");
    }

    @Override
    protected void onStart() {
        super.onStart();
        onLifeCycle("onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        onLifeCycle("onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        onLifeCycle("onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        onLifeCycle("onStop");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        onLifeCycle("onRestart");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        onLifeCycle("onDestroy");
    }

    private void findViews(){
        windTextView = findViewById(R.id.windTextView);
        pressureTextView = findViewById(R.id.pressureTextView);
        addInfoCheckBox = findViewById(R.id.addInfoCheckBox);
        temperatureTextView = findViewById(R.id.temperature);
        updateDataButton = findViewById(R.id.updateDataButton);
        selectCityBtn = findViewById(R.id.selectCityBtn);
        cityTextView = findViewById(R.id.cityTextView);
        windValueTextView = findViewById(R.id.windValueTextView);
        pressureValueTextView = findViewById(R.id.pressureValueTextView);
        yandexWeatherBtn = findViewById(R.id.yandexWeatherBtn);
    }

    private void setOnClickListeners(){
        addInfoCheckBox.setOnClickListener(view -> setViewsVisible());
        updateDataButton.setOnClickListener(view -> updateData());
        selectCityBtn.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, CitySelector.class);
            intent.putExtra(cityKey, cityTextView.getText().toString());
            intent.putExtra(windValueKey, windValueTextView.getText().toString());
            intent.putExtra(pressureValueKey, pressureValueTextView.getText().toString());
            startActivityForResult(intent, cityRequestCode);
        });
        yandexWeatherBtn.setOnClickListener(view -> {
            String cityForURL = getCityforURL();
            Uri uri = Uri.parse("https://yandex.ru/pogoda/" + cityForURL);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        });
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putString(cityKey, cityTextView.getText().toString());
        outState.putString(temperatureKey, temperatureTextView.getText().toString());
        outState.putString(windValueKey, windValueTextView.getText().toString());
        outState.putString(pressureValueKey, pressureValueTextView.getText().toString());
        super.onSaveInstanceState(outState);
        onLifeCycle("onSaveInstanceState");
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        cityTextView.setText(savedInstanceState.getString(cityKey));
        temperatureTextView.setText(savedInstanceState.getString(temperatureKey));
        windValueTextView.setText(savedInstanceState.getString(windValueKey));
        pressureValueTextView.setText(savedInstanceState.getString(pressureValueKey));
        setViewsVisible();
        onLifeCycle("onRestoreInstanceState");
    }

    private void updateData(){
        temperatureTextView.setText("+" + ((int)(Math.random()*5) + 20));
    }

    private void setViewsVisible() {
        boolean showAddInfo = addInfoCheckBox.isChecked();
        windTextView.setVisibility(showAddInfo? View.VISIBLE: View.INVISIBLE);
        pressureTextView.setVisibility(showAddInfo? View.VISIBLE: View.INVISIBLE);
        windValueTextView.setVisibility(showAddInfo? View.VISIBLE: View.INVISIBLE);
        pressureValueTextView.setVisibility(showAddInfo? View.VISIBLE: View.INVISIBLE);
    }

    private void onLifeCycle(String action) {
        String text = getString(R.string.app_name) + " " + action;
        Log.d("onLifeCycle", text);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == cityRequestCode && resultCode == RESULT_OK) {
            if (data != null) {
                cityTextView.setText(data.getStringExtra(cityKey));
                windValueTextView.setText(data.getStringExtra(windValueKey));
                pressureValueTextView.setText(data.getStringExtra(pressureValueKey));
            }
        }
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