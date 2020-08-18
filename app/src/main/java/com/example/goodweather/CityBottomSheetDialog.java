package com.example.goodweather;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;

public class CityBottomSheetDialog extends BottomSheetDialogFragment {

    public static final int BUTTON_CITY_SELECT_CODE = 1;
    public static final int BUTTON_CITY_DELETE_CODE = 2;
    private BottomSheetListener listener;
    private String cityName;

    public CityBottomSheetDialog(BottomSheetListener listener, String cityName) {
        super();
        this.listener = listener;
        this.cityName = cityName;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.city_on_click_dialog, container, false);
        init(view);
        return view;
    }

    private void init(View view){
        MaterialButton showCityButton = view.findViewById(R.id.show_city);
        showCityButton.setOnClickListener(view1 -> {
            listener.onBottomClicked(BUTTON_CITY_SELECT_CODE);
            dismiss();
        });
        MaterialButton deleteCityButton = view.findViewById(R.id.delete_city);
        deleteCityButton.setOnClickListener(view1 -> {
            listener.onBottomClicked(BUTTON_CITY_DELETE_CODE);
            dismiss();
        });
        TextView cityNameTextView = view.findViewById(R.id.dialog_city_name);
        cityNameTextView.setText(cityName);
    }


    public interface BottomSheetListener{
        void onBottomClicked(int code);
    }

}
