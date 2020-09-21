package goodweather.data.web;

import androidx.work.Data;

public abstract class RunnableWithData implements Runnable {
    public Data data;

    void setData(Data data) {
        this.data = data;
    }
}
