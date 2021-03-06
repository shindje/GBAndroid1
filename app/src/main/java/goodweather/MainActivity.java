package goodweather;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.goodweather.R;

import goodweather.data.db.model.CityHistory;
import goodweather.data.db.App;
import goodweather.data.db.CityHistoryDao;
import goodweather.data.db.CityHistorySource;
import goodweather.data.web.RetrofitGetter;
import goodweather.settings.SettingsFragment;
import goodweather.weather.CitySelector;
import goodweather.weather.RecyclerAdapter;
import goodweather.settings.Settings;
import goodweather.weather.WeatherFragment;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;

import java.sql.Time;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements CityBottomSheetDialog.BottomSheetListener{
    private Toolbar toolbar;
    private RecyclerAdapter adapter = null;
    private CityHistorySource cityHistorySource;
    private NavigationView navigationView;
    private DrawerLayout drawer;
    private static CitySelector citySelector;
    private WiFiStateReceiver wiFiStateReceiver;
    private FloatingActionButton fab;
    private LinearLayout navHeader;
    private TextView navName;
    private TextView navWeb;
    private ImageView navAvatarIcon;
    private WeatherFragment weatherFragment;
    private GoogleSignInAccount account;

    private static final int PERMISSION_REQUEST_CODE = 10;
    public static final String TAG = "GOOD_WEATHER";

    // Используется, чтобы определить результат Activity регистрации через
    // Google
    private static final int RC_SIGN_IN = 40404;

    // Клиент для регистрации пользователя через Google
    private GoogleSignInClient googleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Settings settings = Settings.getInstance(this);
        if (settings.isDarkTheme()) {
            setTheme(R.style.AppDarkTheme);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        setWeatherFragment(null);
        setOnClickForSideMenuItems();

        wiFiStateReceiver = new WiFiStateReceiver();
        registerReceiver(wiFiStateReceiver, new IntentFilter(WifiManager.NETWORK_STATE_CHANGED_ACTION));
        initNotificationChannel();

        // Конфигурация запроса на регистрацию пользователя, чтобы получить
        // идентификатор пользователя, его почту и основной профайл
        // (регулируется параметром)
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Получаем клиента для регистрации и данные по клиенту
        googleSignInClient = GoogleSignIn.getClient(this, gso);
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

        fab = findViewById(R.id.fab);
        // Обработка нажатия на плавающую кнопку
        fab.setOnClickListener(this::showAddItemDialog);

        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navHeader = (LinearLayout) navigationView.getHeaderView(0);

        navName = navHeader.findViewById(R.id.navName);
        navWeb = navHeader.findViewById(R.id.navWeb);
        navAvatarIcon = navHeader.findViewById(R.id.navAvatarIcon);
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
            case R.id.toolbar_add:
                showAddItemDialog(toolbar);
                break;
            case R.id.toolbar_current_location:
                showCurrentLocationWeather();
                break;
        }
    }

    public void getFullData() {
        Snackbar.make(toolbar, getString(R.string.data_updating), Snackbar.LENGTH_SHORT)
                .setAction("Action", null).show();
        for (String city: CitySelector.getCities(getResources())) {
            RetrofitGetter.getData(getApplicationContext(), this, city, null, null,
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
                        setWeatherFragment(null);
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

    public String getDefaultTemperature() {
        return getString(R.string.default_temperature);
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

    public void setWeatherFragment(Fragment weatherFragment) {
        if (weatherFragment == null) {
            if ((citySelector == null || citySelector.getCityName() == null) && Utils.getLastCityName(this) == null) {
                showCurrentLocationWeather();
            } else {
                if (citySelector == null) {
                    String lastCityName = Utils.getLastCityName(this);
                    citySelector = new CitySelector();
                    citySelector.setCityName(lastCityName);
                }
                setFragment(WeatherFragment.create(citySelector.getCityName(), null, null));
            }
        } else {
            setFragment(weatherFragment);
        }
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
                    setWeatherFragment(null);
                    drawer.close();
                    break;
                }
                case R.id.nav_city: {
                    setCitySelectorFragment();
                    drawer.close();
                    break;
                }
                case R.id.nav_map: {
                    setMapFragment();
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

    public void setFabVisibility(int visibility) {
        fab.setVisibility(visibility);
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

    private void showCurrentLocationWeather() {
        requestLocationPemissions(this::requestLocation);
    }

    void requestLocationPemissions(Runnable run) {
        // Проверим, есть ли Permission’ы, и если их нет, запрашиваем их у
        // пользователя
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            run.run();
        } else {
            // Permission’ов нет, запрашиваем их у пользователя
            requestLocationPermissions();
        }
    }

    // Запрашиваем Permission’ы для геолокации
    private void requestLocationPermissions() {
        if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CALL_PHONE)) {
            // Запрашиваем эти два Permission’а у пользователя
            ActivityCompat.requestPermissions(this,
                    new String[]{
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION
                    },
                    PERMISSION_REQUEST_CODE);
        }
    }

    // Результат запроса Permission’а у пользователя:
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE) {   // Запрошенный нами
            // Permission
            if (grantResults.length == 2 &&
                    (grantResults[0] == PackageManager.PERMISSION_GRANTED || grantResults[1] == PackageManager.PERMISSION_GRANTED)) {
                // Все препоны пройдены и пермиссия дана
                // Запросим координаты
                requestLocation();
            } else {
                Snackbar.make(toolbar, getString(R.string.error_need_permission), Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        }
    }


    // Запрашиваем координаты
    private void requestLocation() {
        // Ещё раз проверим разрешения (требование SDK)
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            return;
        // Получаем менеджер геолокаций
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        // Получим координаты
        LocationListener locationListener = new LocationListener() {
            boolean gotLocation = false;

            @Override
            public void onLocationChanged(@NonNull Location location) {
                if (!gotLocation) {
                    WeatherFragment fragment = WeatherFragment.create(null,
                            Double.toString(location.getLatitude()), Double.toString(location.getLongitude()));
                    setWeatherFragment(fragment);
                }
                gotLocation = true;
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            @Override
            public void onProviderEnabled(String provider) {
            }

            @Override
            public void onProviderDisabled(String provider) {
            }
        };

        locationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, locationListener, null);
        locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, locationListener, null);
    }

    public static CitySelector getCitySelector() {
        if (citySelector == null) {
            citySelector = new CitySelector();
        }
        return citySelector;
    }

    private void setMapFragment() {
        setFragment(new MapsFragment());
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Проверим, входил ли пользователь в это приложение через Google
        account = GoogleSignIn.getLastSignedInAccount(this);
        if (account != null) {
            // Обновим данные пользователя и выведем их на экран
            updateUI(account);
        }
    }

    // Получаем результаты аутентификации от окна регистрации пользователя
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            // Когда сюда возвращается Task, результаты аутентификации уже
            // готовы
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    // Инициируем регистрацию пользователя
    public void signIn(WeatherFragment fragment) {
        weatherFragment = fragment;
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    // Получаем данные пользователя
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            // Регистрация прошла успешно
            weatherFragment.setSignInVisibility(false);
            this.account = account;
            updateUI(account);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure
            // reason. Please refer to the GoogleSignInStatusCodes class
            // reference for more information.
            Log.d(TAG, "signInResult: failed code=" + e.getStatusCode());
        }
    }

    // Обновляем данные о пользователе на экране
    public void updateUI(GoogleSignInAccount account) {
        if (account != null) {
            navName.setText(account.getDisplayName());
            navWeb.setText(account.getEmail());
            Picasso.get()
                    .load(account.getPhotoUrl())
                    .into(navAvatarIcon);
            Toast.makeText(this, getString(R.string.logged_in) + ": " + account.getDisplayName(),
                    Toast.LENGTH_SHORT).show();
        } else {
            navName.setText(getString(R.string.nav_header_title));
            navWeb.setText(getString(R.string.nav_header_subtitle));
            navAvatarIcon.setImageDrawable(getDrawable(R.mipmap.ic_launcher_round));
            Toast.makeText(this, getString(R.string.logged_out),
                    Toast.LENGTH_SHORT).show();
        }
    }

    // Выход из учётной записи в приложении
    public void signOut(WeatherFragment fragment) {
        googleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        account = null;
                        fragment.setSignInVisibility(true);
                        updateUI(null);
                    }
                });
    }

    public GoogleSignInAccount getAccount() {
        return account;
    }
}