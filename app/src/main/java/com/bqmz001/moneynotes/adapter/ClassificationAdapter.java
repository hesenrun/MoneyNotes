package com.bqmz001.moneynotes.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bqmz001.moneynotes.R;
import com.bqmz001.moneynotes.entity.Classification;

import java.util.List;

public class ClassificationAdapter extends RecyclerView.Adapter<ClassificationAdapter.ViewHolder> {

    private List<Classification> classificationList;
    private OnClickListener clickListener;
    private OnLongClickListener longClickListener;

    public ClassificationAdapter(List<Classification> classificationList) {
        this.classificationList = classificationList;
    }

    public interface OnClickListener {
        void onClick(int position, View v);
    }

    public interface OnLongClickListener {
        boolean onLongClick(int position, View v);
    }

    public void setClickListener(OnClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public void setLongClickListener(OnLongClickListener longClickListener) {
        this.longClickListener = longClickListener;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout classification;
        private TextView title;
        private View sildeColor;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            classification = itemView.findViewById(R.id.item_classification);
            title = itemView.findViewById(R.id.item_title);
            sildeColor = itemView.findViewById(R.id.silde_color);
        }
    }

    @NonNull
    @Override
    public ClassificationAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_classification, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ClassificationAdapter.ViewHolder holder, int position) {
        holder.title.setText(classificationList.get(position).getName());
        holder.sildeColor.setBackgroundColor(classificationList.get(position).getColor());
        final int p = position;
        holder.classification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickListener.onClick(classificationList.get(p).getId(), v);
            }
        });
        holder.classification.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                longClickListener.onLongClick(classificationList.get(p).getId(), v);
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return classificationList.size();
    }
}
