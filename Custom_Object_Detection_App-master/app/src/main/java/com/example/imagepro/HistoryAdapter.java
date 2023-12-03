package com.example.imagepro;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Objects;

public class HistoryAdapter extends ListAdapter<EachHistory, HistoryAdapter.ViewHolder> {


    private EachHistory curItem;
    private final Context mContext;

    public HistoryAdapter(Context mContext) {
        super(diffCallback);
        this.mContext = mContext;
    }

    static final DiffUtil.ItemCallback<EachHistory> diffCallback = new DiffUtil.ItemCallback<EachHistory>() {
        @Override
        public boolean areItemsTheSame(@NonNull EachHistory oldItem, @NonNull EachHistory newItem) {
            return oldItem.getAdded_on().equals(newItem.getAdded_on());
        }

        @Override
        public boolean areContentsTheSame(@NonNull EachHistory oldItem, @NonNull EachHistory newItem) {
            return Objects.equals(oldItem.getAdded_on(),newItem.getAdded_on()) &&
                 Objects.equals(oldItem.getText(),newItem.getText());
        }
    };

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_history_layout,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        curItem = getItem(position);
        holder.tvText.setText(curItem.getText());
        holder.tvAddedOn.setText(curItem.getAdded_on());
        holder.itemView.setOnClickListener((View v)->{
            Intent intent = new Intent(mContext,CameraActivity.class);
            intent.putExtra("object",getItem(holder.getBindingAdapterPosition()));
            mContext.startActivity(intent);
        });
    }

    static class ViewHolder extends RecyclerView.ViewHolder{

        private final TextView tvText;
        private final TextView tvAddedOn;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvText = itemView.findViewById(R.id.tvText);
            tvAddedOn = itemView.findViewById(R.id.tvAddedOn);
        }
    }
}
