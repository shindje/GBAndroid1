package com.example.goodweather;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    TextView windTextView;
    TextView pressureTextView;
    CheckBox addInfoCheckBox;
    TextView temperature;
    Button updateDataButton;

    final String temperatureKey = "temperature";

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
        temperature = findViewById(R.id.temperature);
        updateDataButton = findViewById(R.id.updateDataButton);
    }

    private void setOnClickListeners(){
        addInfoCheckBox.setOnClickListener((View view) -> {
            if (((CheckBox)view).isChecked()) {
                windTextView.setVisibility(View.VISIBLE);
                pressureTextView.setVisibility(View.VISIBLE);
            } else {
                windTextView.setVisibility(View.INVISIBLE);
                pressureTextView.setVisibility(View.INVISIBLE);
            }
        });
        updateDataButton.setOnClickListener((View view) -> {
            updateData();
        });
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putString(temperatureKey, temperature.getText().toString());
        super.onSaveInstanceState(outState);
        onLifeCycle("onSaveInstanceState");
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        temperature.setText(savedInstanceState.getString(temperatureKey));
        setViewsVisible();
        onLifeCycle("onRestoreInstanceState");
    }

    private void updateData(){
        temperature.setText("+" + ((int)(Math.random()*5) + 20));
    }

    private void setViewsVisible() {
        boolean showAddInfo = addInfoCheckBox.isChecked();
        windTextView.setVisibility(showAddInfo? View.VISIBLE: View.INVISIBLE);
        pressureTextView.setVisibility(showAddInfo? View.VISIBLE: View.INVISIBLE);
    }

    private void onLifeCycle(String action) {
        String text = getString(R.string.app_name) + " " + action;
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
        Log.d("onLifeCycle", text);
    }
}