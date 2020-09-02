package com.example.goodweather.data;

import android.app.Activity;
import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.goodweather.R;
import com.example.goodweather.data.source.OpenWeatherMap;
import com.example.goodweather.observer.Publisher;
import com.google.android.material.snackbar.Snackbar;

public class Getter extends Worker {
    public Getter(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        String cityName = getInputData().getString("cityName");
        String cityNotFound = getInputData().getString("cityNotFound");
        try {
            String data = OpenWeatherMap.getWeather(cityName, cityNotFound);
            Data output = new Data.Builder()
                    .putString("stringData", data)
                    .build();
            return Result.success(output);
        } catch (Exception e) {
            e.printStackTrace();
            Data output = new Data.Builder()
                    .putString("error", e.getLocalizedMessage())
                    .build();
            return Result.failure(output);
        }
    }

    public static abstract class RunnableWithData implements Runnable {
        public Data data;

        void setData(Data data) {
            this.data = data;
        }
    }

    public static void getData(Context context, LifecycleOwner lifecycleOwner, String cityName, int idx,
                                Activity activity, RunnableWithData onFinishAction, View view) {
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType (NetworkType.CONNECTED)
                .build();
        Data inputData = new Data.Builder()
                .putString("cityName", cityName)
                .putString("cityNotFound", activity == null? "": activity.getString(R.string.city_not_foud))
                .build();
        OneTimeWorkRequest dataGetterRequest = new OneTimeWorkRequest.Builder(Getter.class)
                .setConstraints(constraints)
                .setInputData(inputData)
                .build();

        OneTimeWorkRequest dataConverterRequest = new OneTimeWorkRequest.Builder(Converter.class)
                .build();

        WorkManager.getInstance(context).getWorkInfoByIdLiveData(dataGetterRequest.getId())
                .observe(lifecycleOwner, info -> {
                    if (info.getState() == WorkInfo.State.FAILED && activity != null && view != null) {
                        String error = info.getOutputData().getString("error");
                        if (error == null)
                            error = "";

                        Snackbar.make(view, cityName + ": " + activity.getString(R.string.error_getting_data) + ": " + error , Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }
                });

        WorkManager.getInstance(context).getWorkInfoByIdLiveData(dataConverterRequest.getId())
                .observe(lifecycleOwner, info -> {
                    //Если был запуск
                    if (info.getRunAttemptCount() > 0) {
                        Data outputData = info.getOutputData();
                        if (info.getState() == WorkInfo.State.FAILED && activity != null && view != null) {
                            String error = outputData.getString("error");
                            if (error == null)
                                error = "";
                            Snackbar.make(view, cityName + ": " + activity.getString(R.string.error_getting_data) + ": " + error, Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        }
                        if (info.getState() == WorkInfo.State.SUCCEEDED) {
                            if (onFinishAction != null && activity != null) {
                                onFinishAction.setData(outputData);
                                activity.runOnUiThread(onFinishAction);
                            }
                            Publisher.getInstance().notify(idx, outputData);

                            if (activity != null && view != null) {
                                Snackbar.make(view, cityName + ": " + activity.getString(R.string.data_updated), Snackbar.LENGTH_SHORT)
                                        .setAction("Action", null).show();
                            }
                        }
                    }
                });

        WorkManager.getInstance(context)
                .beginWith(dataGetterRequest)
                .then(dataConverterRequest)
                .enqueue();
    }
}
