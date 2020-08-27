package com.example.goodweather.settings;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.goodweather.R;

public class SettingsFragment extends Fragment {

    private RadioGroup themeRadioGroup;
    private CheckBox updateOnStartCb;
    private CheckBox updateInBackCb;

    private static Settings settings = Settings.getInstance();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        findViews(view);
        initData();
        setOnClickListeners();
    }

    private void findViews(View view){
        themeRadioGroup = view.findViewById(R.id.settings_theme_radio_group);
        updateOnStartCb = view.findViewById(R.id.settings_update_on_start_cb);
        updateInBackCb = view.findViewById(R.id.settings_update_in_back_cb);
    }

    private void setOnClickListeners() {
        themeRadioGroup.setOnCheckedChangeListener((RadioGroup group, int rb) -> {
            if (rb == R.id.settings_dark_theme_rb) {
                settings.isDarkTheme = true;
            } else {
                settings.isDarkTheme = false;
            }
            getActivity().recreate();
        });
        updateOnStartCb.setOnCheckedChangeListener((CompoundButton cb, boolean val) -> {
            if (val) {
                settings.updateOnStart = true;
            } else {
                settings.updateOnStart = false;
            }
        });
        updateInBackCb.setOnCheckedChangeListener((CompoundButton cb, boolean val) -> {
            if (val) {
                settings.updateInBackgorund = true;
            } else {
                settings.updateInBackgorund = false;
            }
        });
    }


    private void initData() {
        if (settings.isDarkTheme()) {
            themeRadioGroup.check(R.id.settings_dark_theme_rb);
        }
        if (settings.isUpdateOnStart()) {
            updateOnStartCb.setChecked(true);
        }
        if (settings.isUpdateInBackgorund()) {
            updateInBackCb.setChecked(true);
        }
    }

}
