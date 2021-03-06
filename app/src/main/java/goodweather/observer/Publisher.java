package goodweather.observer;

import androidx.work.Data;
import java.util.ArrayList;
import java.util.List;

public class Publisher {
    private static Publisher instance = null;
    private List<IObserver> observers = new ArrayList<>();

    private Publisher() {}

    static public Publisher getInstance() {
        if(instance == null) {
            instance = new Publisher();
        }

        return instance;
    }

    public void subscribe(IObserver observer) {
        observers.add(observer);
    }

    public void unsubscribe(IObserver observer) {
        observers.remove(observer);
    }

    public void notify(String cityName, Data data) {
        for (IObserver observer : observers) {
            observer.updateData(cityName, data);
        }
    }
}
