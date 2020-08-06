package com.example.goodweather;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import static com.example.goodweather.MainActivity.cityKey;
import static com.example.goodweather.MainActivity.pressureValueKey;
import static com.example.goodweather.MainActivity.windValueKey;

public class CitySelector extends AppCompatActivity {
    EditText cityNameEditText;
    TextView londonTextView, parisTextView, newYorkTextView, windTextView, pressureTextView,
             windValueTextView, pressureValueTextView;
    CheckBox addInfoCheckBox;
    Button selectCityBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.city_selector);
        findViews();
        setOnClickListeners();
        setViewsVisible();
        setData();
    }

    private void findViews(){
        cityNameEditText = findViewById(R.id.cityNameEditText);
        londonTextView = findViewById(R.id.londonTextView);
        parisTextView = findViewById(R.id.parisTextView);
        newYorkTextView = findViewById(R.id.newYorkTextView);
        windTextView = findViewById(R.id.windTextView);
        pressureTextView = findViewById(R.id.pressureTextView);
        addInfoCheckBox = findViewById(R.id.addInfoCheckBox);
        selectCityBtn = findViewById(R.id.selectCityBtn);
        windValueTextView = findViewById(R.id.windValueTextView);
        pressureValueTextView = findViewById(R.id.pressureValueTextView);
    }

    private void setOnClickListeners(){
        londonTextView.setOnClickListener(cityNameOnClick);
        parisTextView.setOnClickListener(cityNameOnClick);
        newYorkTextView.setOnClickListener(cityNameOnClick);
        addInfoCheckBox.setOnClickListener(view -> setViewsVisible());
        selectCityBtn.setOnClickListener(view -> {
            Intent data = new Intent();
            data.putExtra(cityKey, cityNameEditText.getText().toString());
            data.putExtra(windValueKey, windValueTextView.getText().toString());
            data.putExtra(pressureValueKey, pressureValueTextView.getText().toString());
            setResult(RESULT_OK, data);
            finish();
        });
    }

    View.OnClickListener cityNameOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            cityNameEditText.setText(((TextView)view).getText());
            windValueTextView.setText("" + ((int)(Math.random()*10) + 10));
            pressureValueTextView.setText("" + ((int)(Math.random()*20) + 740));
        }
    };

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putString(windValueKey, windValueTextView.getText().toString());
        outState.putString(pressureValueKey, pressureValueTextView.getText().toString());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        windValueTextView.setText(savedInstanceState.getString(windValueKey));
        pressureValueTextView.setText(savedInstanceState.getString(pressureValueKey));
        setViewsVisible();
    }

    private void setViewsVisible() {
        boolean showAddInfo = addInfoCheckBox.isChecked();
        windTextView.setVisibility(showAddInfo? View.VISIBLE: View.INVISIBLE);
        pressureTextView.setVisibility(showAddInfo? View.VISIBLE: View.INVISIBLE);
        windValueTextView.setVisibility(showAddInfo? View.VISIBLE: View.INVISIBLE);
        pressureValueTextView.setVisibility(showAddInfo? View.VISIBLE: View.INVISIBLE);
    }

    private void setData() {
        Intent data = getIntent();
        if (data != null) {
            cityNameEditText.setText(data.getStringExtra(cityKey));
            windValueTextView.setText(data.getStringExtra(windValueKey));
            pressureValueTextView.setText(data.getStringExtra(pressureValueKey));
        }
    }
}
