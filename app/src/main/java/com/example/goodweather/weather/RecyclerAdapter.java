package com.example.goodweather.weather;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.goodweather.MainActivity;
import com.example.goodweather.R;
import com.example.goodweather.data.db.CityHistorySource;
import com.example.goodweather.data.db.model.CityHistory;

import java.util.List;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {
    private IRVOnItemClick onItemClickCallback;
    private int itemLayoutId;
    private MainActivity activity;
    private int itemIndexFromMenu;
    // Источник данных
    private CityHistorySource dataSource;

    public RecyclerAdapter(CityHistorySource dataSource, IRVOnItemClick onItemClickCallback, int itemLayoutId, MainActivity activity) {
        this.dataSource = dataSource;
        this.onItemClickCallback = onItemClickCallback;
        this.itemLayoutId = itemLayoutId;
        this.activity = activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(itemLayoutId, parent,
                false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Заполняем данными записи на экране
        List<CityHistory> historyList = dataSource.getFullHistory();
        CityHistory history = historyList.get(position);
        holder.setCityNameText(history.cityName);
        holder.setDateText(history.getDateText());
        holder.setTimeText(history.getTimeText());
        holder.setTemperatureText("" + history.temperature);
        holder.setOnClickForItem(position);


        // Определяем текущую позицию в списке
        View.OnLongClickListener onLongClickListener = new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                itemIndexFromMenu = position;
                return false;
            }
        };

        TextView cityNameTextView = holder.getCityNameTextView();
        cityNameTextView.setOnLongClickListener(onLongClickListener);
        TextView dateTextView = holder.getDateTextView();
        dateTextView.setOnLongClickListener(onLongClickListener);
        TextView timeTextView = holder.getTimeTextView();
        timeTextView.setOnLongClickListener(onLongClickListener);
        TextView temperatureTextView = holder.getTemperatureTextView();
        temperatureTextView.setOnLongClickListener(onLongClickListener);

        if (activity != null){
            activity.registerForContextMenu(cityNameTextView);
            activity.registerForContextMenu(dateTextView);
            activity.registerForContextMenu(timeTextView);
            activity.registerForContextMenu(temperatureTextView);
        }
    }

    @Override
    public int getItemCount() {
        return (int)dataSource.getCountHistory();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView cityNameTextView;
        private TextView dateTextView;
        private TextView timeTextView;
        private TextView temperatureTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cityNameTextView = itemView.findViewById(R.id.cityNameTextView);
            dateTextView  = itemView.findViewById(R.id.dateTextView);
            timeTextView  = itemView.findViewById(R.id.timeTextView);
            temperatureTextView  = itemView.findViewById(R.id.temperatureTextView);
        }

        void setCityNameText(String text) {
            cityNameTextView.setText(text);
        }
        void setDateText(String text) {
            dateTextView.setText(text);
        }
        void setTimeText(String text) {
            timeTextView.setText(text);
        }
        void setTemperatureText(String text) {
            temperatureTextView.setText(text);
        }

        void setOnClickForItem(int position) {
            View.OnClickListener onClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(onItemClickCallback != null) {
                        onItemClickCallback.onItemClicked(dataSource.getFullHistory().get(position).cityName);
                    }
                }
            };

            cityNameTextView.setOnClickListener(onClickListener);
            dateTextView.setOnClickListener(onClickListener);
            timeTextView.setOnClickListener(onClickListener);
            temperatureTextView.setOnClickListener(onClickListener);
        }

        public TextView getCityNameTextView() {
            return cityNameTextView;
        }
        public void setCityNameTextView(TextView cityNameTextView) {
            this.cityNameTextView = cityNameTextView;
        }
        public TextView getDateTextView() {
            return dateTextView;
        }
        public void setDateTextView(TextView dateTextView) {
            this.dateTextView = dateTextView;
        }
        public TextView getTimeTextView() {
            return timeTextView;
        }
        public void setTimeTextView(TextView timeTextView) {
            this.timeTextView = timeTextView;
        }
        public TextView getTemperatureTextView() {
            return temperatureTextView;
        }
        public void setTemperatureTextView(TextView temperatureTextView) {
            this.temperatureTextView = temperatureTextView;
        }
    }

    public int getItemIndexFromMenu() {
        return itemIndexFromMenu;
    }

    public void remove(int posititon) {
        //TODO
        //data1.remove(posititon);
        //data2.remove(posititon);
        //dataSource.removeHistory();
        notifyItemRemoved(posititon);
    }

    public void add(String cityName) {
        //TODO
        //data1.add(cityName);
        //data2.add(activity.getDefaultTemperature());
        //notifyItemInserted(data1.size() - 1);
    }
}
