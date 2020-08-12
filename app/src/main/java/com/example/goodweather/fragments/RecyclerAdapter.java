package com.example.goodweather.fragments;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.goodweather.R;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {
    private String[] data1;
    private String[] data2;
    private IRVOnItemClick onItemClickCallback;
    private int itemLayoutId;

    public RecyclerAdapter(String[] data1, String[] data2, IRVOnItemClick onItemClickCallback, int itemLayoutId) {
        this.data1 = data1;
        this.data2 = data2;
        this.onItemClickCallback = onItemClickCallback;
        this.itemLayoutId = itemLayoutId;
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
        String text1 = data1[position];
        holder.setTextToTextView1(text1);
        String text2 = data2[position];
        holder.setTextToTextView2(text2);

        holder.setOnClickForItem(position);
    }

    @Override
    public int getItemCount() {
        return data1 == null ? 0 : data1.length;
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
                        onItemClickCallback.onItemClicked(position);
                    }
                }
            });

            textView2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(onItemClickCallback != null) {
                        onItemClickCallback.onItemClicked(position);
                    }
                }
            });
        }
    }
}
