package goodweather;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.goodweather.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;

public class CityBottomSheetDialog extends BottomSheetDialogFragment {

    public static final int OK_CODE = 1;
    public static final int CANCEL_CODE = 2;
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
        MaterialButton showCityButton = view.findViewById(R.id.dialog_ok_btn);
        showCityButton.setOnClickListener(view1 -> {
            listener.onBottomClicked(OK_CODE);
            dismiss();
        });
        MaterialButton deleteCityButton = view.findViewById(R.id.dialog_cancel_btn);
        deleteCityButton.setOnClickListener(view1 -> {
            listener.onBottomClicked(CANCEL_CODE);
            dismiss();
        });
        TextView cityNameTextView = view.findViewById(R.id.dialog_city_name);
        cityNameTextView.setText(cityName);
    }


    public interface BottomSheetListener{
        void onBottomClicked(int code);
    }

}
