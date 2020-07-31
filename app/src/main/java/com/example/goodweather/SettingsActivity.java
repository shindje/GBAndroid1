package com.example.goodweather;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SettingsActivity extends AppCompatActivity {
    EditText cityNameEditText;
    TextView londonTextView;
    TextView parisTextView;
    TextView newYorkTextView;
    TextView windTextView;
    TextView pressureTextView;
    CheckBox addInfoCheckBox;
    Button selectCityBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.city_selector);
        findViews();
        setOnClickListeners();
        windTextView.setVisibility(View.INVISIBLE);
        pressureTextView.setVisibility(View.INVISIBLE);
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
    }

    private void setOnClickListeners(){
        londonTextView.setOnClickListener(cityNameOnClick);
        parisTextView.setOnClickListener(cityNameOnClick);
        newYorkTextView.setOnClickListener(cityNameOnClick);
        addInfoCheckBox.setOnClickListener((View view) -> {
            if (((CheckBox)view).isChecked()) {
                windTextView.setVisibility(View.VISIBLE);
                pressureTextView.setVisibility(View.VISIBLE);
            } else {
                windTextView.setVisibility(View.INVISIBLE);
                pressureTextView.setVisibility(View.INVISIBLE);
            }
        });
        selectCityBtn.setOnClickListener((View view) -> {
            Toast.makeText(getApplicationContext(),
                    getText(R.string.selectedCityText) + " " + cityNameEditText.getText(),
                    Toast.LENGTH_SHORT).show();
        });
    }

    View.OnClickListener cityNameOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            cityNameEditText.setText(((TextView)view).getText());
        }
    };
}