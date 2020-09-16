package goodweather.settings;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.goodweather.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

public class SettingsFragment extends Fragment {

    private RadioGroup themeRadioGroup;
    private CheckBox updateOnStartCb;
    private CheckBox updateInBackCb;
    private EditText tokenEditText;
    private Button updateTokenButton;

    private Settings settings;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        settings = Settings.getInstance(getContext());
        findViews(view);
        initData();
        setOnClickListeners();
    }

    private void findViews(View view){
        themeRadioGroup = view.findViewById(R.id.settings_theme_radio_group);
        updateOnStartCb = view.findViewById(R.id.settings_update_on_start_cb);
        updateInBackCb = view.findViewById(R.id.settings_update_in_back_cb);
        tokenEditText = view.findViewById(R.id.tokenEditText);
        updateTokenButton = view.findViewById(R.id.updateTokenButton);
    }

    private void setOnClickListeners() {
        themeRadioGroup.setOnCheckedChangeListener((RadioGroup group, int rb) -> {
            settings.setDarkTheme(rb == R.id.settings_dark_theme_rb);
            Activity activity = getActivity();
            if (activity != null)
                activity.recreate();
        });
        updateOnStartCb.setOnCheckedChangeListener((CompoundButton cb, boolean val) -> settings.setUpdateOnStart(val));
        updateInBackCb.setOnCheckedChangeListener((CompoundButton cb, boolean val) -> settings.setUpdateInBackgorund(val));
        updateTokenButton.setOnClickListener(view -> {
            FirebaseInstanceId.getInstance().getInstanceId()
                    .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                        @Override
                        public void onComplete(@NonNull Task<InstanceIdResult> task) {
                            if (!task.isSuccessful()) {
                                Toast.makeText(getContext(), getString(R.string.token) + ": " +
                                        getString(R.string.error_getting_data), Toast.LENGTH_LONG).show();
                                return;
                            }

                            // Получить токен
                            String token = task.getResult().getToken();
                            // Сохранить токен...
                            Toast.makeText(getContext(), getString(R.string.new_token_msg), Toast.LENGTH_LONG).show();
                            settings.setToken(token);
                            tokenEditText.setText(token);
                        }
                    });

        });
    }


    private void initData() {
        themeRadioGroup.check(settings.isDarkTheme()? R.id.settings_dark_theme_rb: R.id.settings_light_theme_rb);
        updateOnStartCb.setChecked(settings.isUpdateOnStart());
        updateInBackCb.setChecked(settings.isUpdateInBackgorund());
        tokenEditText.setText(settings.getToken());
    }

}
