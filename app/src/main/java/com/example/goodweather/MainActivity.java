package com.example.goodweather;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
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

import com.example.goodweather.data.db.App;
import com.example.goodweather.data.db.CityHistoryDao;
import com.example.goodweather.data.db.CityHistorySource;
import com.example.goodweather.data.db.model.CityHistory;
import com.example.goodweather.data.web.RetrofitGetter;
import com.example.goodweather.settings.SettingsFragment;
import com.example.goodweather.weather.CitySelector;
import com.example.goodweather.weather.RecyclerAdapter;
import com.example.goodweather.settings.Settings;
import com.example.goodweather.weather.WeatherFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import java.sql.Time;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements CityBottomSheetDialog.BottomSheetListener{
    private Toolbar toolbar;
    private RecyclerAdapter adapter = null;
    private CityHistorySource cityHistorySource;
    private NavigationView navigationView;
    private DrawerLayout drawer;
    private static CitySelector citySelector;
    private static final String SHARED_PREF_LAST_CITY = "last_city";
    private WiFiStateReceiver wiFiStateReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Settings settings = Settings.getInstance(this);
        if (settings.isDarkTheme()) {
            setTheme(R.style.AppDarkTheme);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        if (savedInstanceState == null && Settings.getInstance(this).isUpdateOnStart()) {
            getFullData();
        }

        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        setWeatherFragment();
        setOnClickForSideMenuItems();

        wiFiStateReceiver = new WiFiStateReceiver();
        registerReceiver(wiFiStateReceiver, new IntentFilter(WifiManager.NETWORK_STATE_CHANGED_ACTION));
        initNotificationChannel();
    }

    // инициализация канала нотификаций
    private void initNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel channel = new NotificationChannel("2", "name", importance);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(wiFiStateReceiver);
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
                getFullData();
                break;
            case R.id.menu_add:
                showAddItemDialog(toolbar);
                break;
        }
    }

    public void getFullData() {
        Snackbar.make(toolbar, getString(R.string.data_updating), Snackbar.LENGTH_SHORT)
                .setAction("Action", null).show();
        for (String city: CitySelector.getCities(getResources())) {
            RetrofitGetter.getData(getApplicationContext(), this, city,
                    this, null, null,null);
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
                        String cityName = editText.getText().toString();
                        CitySelector.addCity(this, this, cityName,
                                toolbar, getAdapter());
                        alert.dismiss();
                        citySelector.setCityName(cityName);
                        setWeatherFragment();
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
            CityBottomSheetDialog dialog = new CityBottomSheetDialog(bottomSheetListener, CitySelector.getCities(getResources()).get(getAdapter().getItemIndexFromMenu()));
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
            CitySelector.deleteCity(getAdapter().getItemIndexFromMenu(), getAdapter());
        }
    }

    public String getDefaultCityName() {
        SharedPreferences sharedPref = getPreferences(MODE_PRIVATE);
        return sharedPref.getString(SHARED_PREF_LAST_CITY, getString(R.string.defaultCity));
    }

    public String getDefaultTemperature() {
        return getString(R.string.default_temperature);
    }

    public void setLastCityName(String cityName) {
        SharedPreferences sharedPref = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(SHARED_PREF_LAST_CITY, cityName);
        editor.apply();
    }

    public void addHistory(String cityName, String temperature) {
        CityHistory history = new CityHistory();
        history.cityName = cityName;
        Date date = new Date();
        history.date = date;
        history.time = new Time(date.getTime());
        try {
            history.temperature = Integer.parseInt(temperature);
        } catch (Exception e) {

        }
        getCityHistorySource().addHistory(history);
        getAdapter().notifyDataSetChanged();
    }

    public void setWeatherFragment() {
        String cityName = getDefaultCityName();
        if (citySelector == null) {
            citySelector = new CitySelector();
            citySelector.setCityName(cityName);
        } else {
            cityName = citySelector.getCityName();
        }
        setFragment(WeatherFragment.create(cityName));
    }

    private void setCitySelectorFragment() {
        if (citySelector == null)
            citySelector = new CitySelector();
        setFragment(citySelector);
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
                case R.id.nav_city: {
                    setCitySelectorFragment();
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
                }
            }
            return true;
        });
    }

    public void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragmentContainer, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    RecyclerAdapter getAdapter() {
        if (adapter == null) {
            adapter = new RecyclerAdapter(getCityHistorySource(), null, R.layout.city_item, this);
        }
        return adapter;
    }

    CityHistorySource getCityHistorySource() {
        if (cityHistorySource == null) {
            CityHistoryDao dao = App
                    .getInstance()
                    .getCityHistoryDao();
            cityHistorySource = new CityHistorySource(dao);

        }
        return cityHistorySource;
    }

    public void setCityHistorySource(CityHistorySource cityHistorySource) {
        this.cityHistorySource = cityHistorySource;
    }
}