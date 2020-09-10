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
import androidx.work.RxWorker;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;
import androidx.work.WorkerParameters;

import com.example.goodweather.R;
import com.example.goodweather.data.source.OpenWeatherMap;
import com.example.goodweather.observer.Publisher;
import com.google.android.material.snackbar.Snackbar;

import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.schedulers.Schedulers;

public class RxWorkerGetter extends RxWorker {
    public RxWorkerGetter(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Single<Result> createWork() {
        Single<Result> single = Single.create(new OnSubscribe());
        return single.subscribeOn(Schedulers.io()).observeOn(Schedulers.io())
                .onErrorReturn(e -> {
                    Data output = new Data.Builder()
                            .putString("error", e.getLocalizedMessage())
                            .build();
                    return Result.failure(output);
                });
    }

    private class OnSubscribe implements SingleOnSubscribe<Result> {
        @Override
        public void subscribe(SingleEmitter<Result> emitter) {
            String cityName = getInputData().getString("cityName");
            String cityNotFound = getInputData().getString("cityNotFound");
            try {
                String data = OpenWeatherMap.getWeather(cityName, cityNotFound);
                Data output = new Data.Builder()
                        .putString("stringData", data)
                        .build();
                emitter.onSuccess(Result.success(output));
            } catch (Exception e) {
                emitter.onError(e);
            }

        }
    }

    public static void getData(Context context, LifecycleOwner lifecycleOwner, String cityName,
                                Activity activity, RunnableWithData onOkAction, RunnableWithData onErrorAction,
                               View view) {
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();
        Data inputData = new Data.Builder()
                .putString("cityName", cityName)
                .putString("cityNotFound", activity == null ? "" : activity.getString(R.string.city_not_foud))
                .build();
        OneTimeWorkRequest dataGetterRequest = new OneTimeWorkRequest.Builder(RxWorkerGetter.class)
                .setConstraints(constraints)
                .setInputData(inputData)
                .build();

        OneTimeWorkRequest dataConverterRequest = new OneTimeWorkRequest.Builder(RxWorkerConverter.class)
                .build();

        WorkManager.getInstance(context).getWorkInfoByIdLiveData(dataGetterRequest.getId())
                .observe(lifecycleOwner, info -> {
                    Data outputData = info.getOutputData();

                    if (info.getState() == WorkInfo.State.FAILED) {
                        Publisher.getInstance().notify(cityName, outputData);

                        if (activity != null && view != null) {
                            String error = outputData.getString("error");
                            if (error == null)
                                error = "";

                            Snackbar.make(view, cityName + ": " + activity.getString(R.string.error_getting_data) + ": " + error, Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        }
                        if (onErrorAction != null && activity != null) {
                            onErrorAction.setData(outputData);
                            activity.runOnUiThread(onErrorAction);
                        }
                    }
                });

        WorkManager.getInstance(context).getWorkInfoByIdLiveData(dataConverterRequest.getId())
                .observe(lifecycleOwner, info -> {
                    //Если был запуск
                    if (info.getRunAttemptCount() > 0) {
                        Data outputData = info.getOutputData();
                        if (info.getState() == WorkInfo.State.FAILED) {
                            Publisher.getInstance().notify(cityName, outputData);

                            if (activity != null && view != null) {
                                String error = outputData.getString("error");
                                if (error == null)
                                    error = "";
                                Snackbar.make(view, cityName + ": " + activity.getString(R.string.error_getting_data) + ": " + error, Snackbar.LENGTH_LONG)
                                        .setAction("Action", null).show();
                            }
                            if (onErrorAction != null && activity != null) {
                                onErrorAction.setData(outputData);
                                activity.runOnUiThread(onErrorAction);
                            }
                        }
                        if (info.getState() == WorkInfo.State.SUCCEEDED) {
                            Publisher.getInstance().notify(cityName, outputData);
                            if (onOkAction != null && activity != null) {
                                onOkAction.setData(outputData);
                                activity.runOnUiThread(onOkAction);
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
