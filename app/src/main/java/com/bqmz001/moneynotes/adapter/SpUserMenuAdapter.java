package com.bqmz001.moneynotes.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bqmz001.moneynotes.R;
import com.bqmz001.moneynotes.entity.Classification;
import com.bqmz001.moneynotes.entity.User;

import java.util.List;

public class SpUserMenuAdapter extends BaseAdapter {

    private Context context;
    private List<User> list;
    private LayoutInflater layoutInflater;

    public SpUserMenuAdapter(Context context, List<User> list) {
        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
        this.list = list;

    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.item_spinner, null);
            new SpUserMenuAdapter.ViewHolder(convertView);
        }
        SpCfMenuAdapter.ViewHolder holder = (SpCfMenuAdapter.ViewHolder) convertView.getTag();// get convertView's holder

        holder.itemText.setText(list.get(position).getName());
        holder.slideColor.setBackgroundColor(list.get(position).getColor());

        return convertView;
    }

    class ViewHolder {
        TextView itemText;
        View slideColor;

        public ViewHolder(View convertView) {
            itemText = convertView.findViewById(R.id.spinner_item_name);
            slideColor = convertView.findViewById(R.id.spinner_item_slide_color);
            convertView.setTag(this);
        }
    }
}