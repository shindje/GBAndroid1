package goodweather.data.web;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.RxWorker;
import androidx.work.WorkerParameters;

import goodweather.data.web.model.WeatherData;
import com.google.gson.Gson;

import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.schedulers.Schedulers;

import static goodweather.data.web.Converter.convert;

public class RxWorkerConverter extends RxWorker {
    public RxWorkerConverter(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Single<Result> createWork() {
        Single<Result> single = Single.create(new RxWorkerConverter.OnSubscribe());
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
            String stringData = getInputData().getString("stringData");
            try {
                Gson gson = new Gson();
                final WeatherData weatherData = gson.fromJson(stringData, WeatherData.class);
                emitter.onSuccess(Result.success(convert(weatherData)));
            } catch (Exception e) {
                emitter.onError(e);
            }

        }
    }
}
