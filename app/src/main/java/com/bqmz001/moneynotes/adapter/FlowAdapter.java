package com.bqmz001.moneynotes.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bqmz001.moneynotes.R;
import com.bqmz001.moneynotes.entity.Note;
import com.bqmz001.moneynotes.util.DateTimeUtil;

import java.text.DecimalFormat;
import java.util.List;

public class FlowAdapter extends RecyclerView.Adapter<FlowAdapter.ViewHolder> {

    private List<Note> noteList;
    private OnClickListener clickListener;
    private OnLongClickListener longClickListener;


    public FlowAdapter(List<Note> noteList) {
        this.noteList = noteList;

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
        private LinearLayout card;
        private TextView title, type, time, cost;
        private View sildeColor;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            card = itemView.findViewById(R.id.item_card);
            title = itemView.findViewById(R.id.item_title);
            type = itemView.findViewById(R.id.item_type);
            time = itemView.findViewById(R.id.item_time);
            cost = itemView.findViewById(R.id.item_cost);
            sildeColor = itemView.findViewById(R.id.silde_color);
        }
    }

    @NonNull
    @Override
    public FlowAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_flow, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull FlowAdapter.ViewHolder holder, int position) {
        final int p = position;
        holder.title.setText(noteList.get(position).getNote());
        holder.time.setText(DateTimeUtil.timestampToDate(noteList.get(position).getTime()));
        holder.type.setText(noteList.get(position).getClassification().getName());
        holder.cost.setText(Double.parseDouble(new DecimalFormat("#.0").format(noteList.get(position).getCost())) + "");
        holder.sildeColor.setBackgroundColor(noteList.get(position).getClassification().getColor());
        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickListener.onClick(noteList.get(p).getId(), v);
            }
        });
        holder.card.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                longClickListener.onLongClick(noteList.get(p).getId(), v);
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return noteList.size();
    }
}
