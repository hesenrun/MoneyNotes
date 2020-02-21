package com.bqmz001.moneynotes.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bqmz001.moneynotes.R;
import com.bqmz001.moneynotes.entity.User;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private List<User> userList;
    private OnClickListener clickListener;
    private OnLongClickListener longClickListener;

    public UserAdapter(List<User> userList) {
        this.userList = userList;
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
        private LinearLayout user;
        private TextView title, budget, _default;
        private View sildeColor;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            user = itemView.findViewById(R.id.item_user);
            title = itemView.findViewById(R.id.item_title);
            budget = itemView.findViewById(R.id.item_budget);
            _default = itemView.findViewById(R.id.textView_default);
            sildeColor = itemView.findViewById(R.id.silde_color);
        }
    }

    @NonNull
    @Override
    public UserAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
        UserAdapter.ViewHolder viewHolder = new UserAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull UserAdapter.ViewHolder holder, int position) {
        holder.title.setText(userList.get(position).getName());
        holder.budget.setText("预算：" + userList.get(position).getBudget());
        holder.sildeColor.setBackgroundColor(userList.get(position).getColor());

        final int p = position;

        if (!userList.get(position).isDefault())
            holder._default.setVisibility(View.GONE);
        else if (userList.get(position).isDefault())
            holder._default.setVisibility(View.VISIBLE);

        holder.user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickListener.onClick(userList.get(p).getId(), v);
            }
        });

        holder.user.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return longClickListener.onLongClick(userList.get(p).getId(), v);
            }
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }
}
