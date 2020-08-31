package com.example.goodweather;

import android.os.Bundle;
import android.os.Handler;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.goodweather.data.DataUpdater;
import com.example.goodweather.settings.SettingsFragment;
import com.example.goodweather.weather.CitySelector;
import com.example.goodweather.weather.RecyclerAdapter;
import com.example.goodweather.settings.Settings;
import com.example.goodweather.weather.WeatherFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

public class MainActivity extends AppCompatActivity implements CityBottomSheetDialog.BottomSheetListener{
    private Toolbar toolbar;
    private RecyclerAdapter adapter;
    private NavigationView navigationView;
    private DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Settings settings = Settings.getInstance();
        if (settings.isDarkTheme()) {
            setTheme(R.style.AppDarkTheme);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        if (savedInstanceState == null && Settings.getInstance().isUpdateOnStart()) {
            updateFullData();
        }

        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        setWeatherFragment();
        setOnClickForSideMenuItems();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        // Обработка нажатия на плавающую кнопку
        fab.setOnClickListener(this::showAddItemDialog);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        handleMenuItemClick(item);
        return super.onOptionsItemSelected(item);
    }

    private void handleMenuItemClick(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.menu_refresh_all:
                updateFullData();
                break;
            case R.id.menu_add:
                showAddItemDialog(toolbar);
                break;
        }
    }

    public void updateFullData() {
        Snackbar.make(toolbar, "Обновление данных", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
        List<String> cities = CitySelector.getCities(getResources());
        for (int i = 0; i < cities.size(); i++) {
            DataUpdater.updateData(new Handler(), null, cities, i, null);
        }
    }

    private void showAddItemDialog(View view) {
        // Создаем билдер и передаем контекст приложения
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        // Вытащим макет диалога
        final View contentView = getLayoutInflater().inflate(R.layout.add_diaolog, null);
        // в билдере указываем заголовок окна (можно указывать как ресурс, так и строку)
        builder.setTitle(R.string.addDialogTitle)
                // Установим макет диалога (можно устанавливать любой view)
                .setView(contentView)
                .setPositiveButton(R.string.addButtonText, null);
        AlertDialog alert = builder.create();
        alert.show();

        //Переопределяем обработку нажатия BUTTON_POSITIVE, чтобы диалог не закрывался, если ввод неверен
        alert.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(
                v -> {
                    EditText editText = contentView.findViewById(R.id.editText);
                    if (validateCityEditText(editText)) {
                        CitySelector.addCity(editText.getText().toString(), getString(R.string.default_temperature), adapter);
                        alert.dismiss();
                    }
                }
        );
    }

    private boolean validateCityEditText(EditText editText) {
        String text = editText.getText().toString();
        if (text.equals("")) {
            editText.setError(getString(R.string.addDialogEmptyError));
            return false;
        }
        if (CitySelector.getCities(getResources()).indexOf(text) > -1) {
            editText.setError(getString(R.string.addDialogAlreadyAddedError));
            return false;
        }
        return true;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        ContextMenu.ContextMenuInfo menuInfo = item.getMenuInfo();
        int id = item.getItemId();
        if (id == R.id.delete_context) {
            final CityBottomSheetDialog.BottomSheetListener bottomSheetListener = this;
            CityBottomSheetDialog dialog = new CityBottomSheetDialog(bottomSheetListener, CitySelector.getCities(getResources()).get(adapter.getItemIndexFromMenu()));
            dialog.show(getSupportFragmentManager(), "Диалог удаления города");
        }
        return super.onContextItemSelected(item);
    }

    public void setAdapter(RecyclerAdapter adapter) {
        this.adapter = adapter;
    }

    @Override
    public void onBottomClicked(int code) {
        if (code == CityBottomSheetDialog.OK_CODE) {
            CitySelector.deleteCity(adapter.getItemIndexFromMenu(), adapter);
        }
    }

    private void setWeatherFragment() {
        CitySelector fragment = new CitySelector();
        setFragment(fragment);
    }

    private void setSetingsFragment() {
        setFragment(new SettingsFragment());
    }

    private void setAboutFragment()  {
        setFragment(new AboutFragment());
    }

    private void setReplyFragment()  {
        setFragment(new ReplyFragment());
    }

    private void setOnClickForSideMenuItems() {
        navigationView.setNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.nav_weather: {
                    setWeatherFragment();
                    drawer.close();
                    break;
                }
                case R.id.nav_settings: {
                    setSetingsFragment();
                    drawer.close();
                    break;
                }
                case R.id.nav_about: {
                    setAboutFragment();
                    drawer.close();
                    break;
                }
                case R.id.nav_reply: {
                    setReplyFragment();
                    drawer.close();
                    break;
                }                }
            return true;
        });
    }

    private void setFragment(Fragment fragment) {
        if (fragment instanceof WeatherFragment) {
            showWeatherFragment();
        } else {
            hideWeatherFragment();
        }
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragmentContainer, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void hideWeatherFragment() {
        WeatherFragment detail = (WeatherFragment)
                getSupportFragmentManager().findFragmentById(R.id.weather_fragment);
        if (detail != null && !detail.isHidden()) {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.hide(detail);
            fragmentTransaction.commit();
        }
    }

    private void showWeatherFragment() {
        WeatherFragment detail = (WeatherFragment)
                getSupportFragmentManager().findFragmentById(R.id.weather_fragment);
        if (detail != null && detail.isHidden()) {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.show(detail);
            fragmentTransaction.commit();
        }
    }

    @Override
    public void onBackPressed() {
        if (navigationView.getCheckedItem() != null && navigationView.getCheckedItem().getItemId() == R.id.nav_weather) {
            showWeatherFragment();
        } else {
            hideWeatherFragment();
        }
        super.onBackPressed();
    }
}