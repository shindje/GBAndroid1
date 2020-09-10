package com.example.goodweather.weather;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.goodweather.MainActivity;
import com.example.goodweather.R;

import java.util.ArrayList;
import java.util.List;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {
    private List<String> data1;
    private List<String> data2;
    private IRVOnItemClick onItemClickCallback;
    private int itemLayoutId;
    private MainActivity activity;
    private int itemIndexFromMenu;

    public RecyclerAdapter(List<String> data1, IRVOnItemClick onItemClickCallback, int itemLayoutId, MainActivity activity) {
        this.data1 = data1;
        data2 = new ArrayList<>(data1.size());
        for (int i = 0; i < data1.size(); i++) {
            data2.add(activity.getDefaultTemperature());
        }
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
        String text1 = data1.get(position);
        holder.setTextToTextView1(text1);
        String text2 = data2.get(position);
        holder.setTextToTextView2(text2);
        holder.setOnClickForItem(position);

        TextView textView1 = holder.getTextView1();
        TextView textView2 = holder.getTextView2();

        // Определяем текущую позицию в списке
        textView1.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                itemIndexFromMenu = position;
                return false;
            }
        });
        textView2.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                itemIndexFromMenu = position;
                return false;
            }
        });


        if (activity != null){
            activity.registerForContextMenu(textView1);
            activity.registerForContextMenu(textView2);
        }
    }

    @Override
    public int getItemCount() {
        return data1 == null ? 0 : data1.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView textView1;
        private TextView textView2;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView1 = itemView.findViewById(R.id.textView1);
            textView2  = itemView.findViewById(R.id.textView2);
        }

        void setTextToTextView1(String text) {
            textView1.setText(text);
        }

        void setTextToTextView2(String text) {
            textView2.setText(text);
        }

        void setOnClickForItem(int position) {
            textView1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(onItemClickCallback != null) {
                        onItemClickCallback.onItemClicked(data1.get(position));
                    }
                }
            });

            textView2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(onItemClickCallback != null) {
                        onItemClickCallback.onItemClicked(data1.get(position));
                    }
                }
            });
        }

        public TextView getTextView1() {
            return textView1;
        }

        public void setTextView1(TextView textView1) {
            this.textView1 = textView1;
        }

        public TextView getTextView2() {
            return textView2;
        }

        public void setTextView2(TextView textView2) {
            this.textView2 = textView2;
        }
    }

    public int getItemIndexFromMenu() {
        return itemIndexFromMenu;
    }

    public void remove(int posititon) {
        data1.remove(posititon);
        data2.remove(posititon);
        notifyItemRemoved(posititon);
    }

    public void add(String cityName) {
        data1.add(cityName);
        data2.add(activity.getDefaultTemperature());
        notifyItemInserted(data1.size() - 1);
    }
}
