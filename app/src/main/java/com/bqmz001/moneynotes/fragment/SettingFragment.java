package com.bqmz001.moneynotes.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;


import com.bqmz001.moneynotes.ManageActivity;
import com.bqmz001.moneynotes.R;
import com.bqmz001.moneynotes.SettingsActivity;
import com.bqmz001.moneynotes.assembly.ViewPagerFragment;
import com.bqmz001.moneynotes.data.DataCenter;
import com.bqmz001.moneynotes.entity.User;

public class SettingFragment extends ViewPagerFragment implements View.OnClickListener {
    TextView textView_name, textView_budget;
    View slide_color;
    ImageView icon;
    ViewGroup menu_user, menu_classicication, menu_budget,menu_settings;
    Intent intent;
    AlertDialog.Builder builder;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_settings, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        slide_color = view.findViewById(R.id.user_slide_color);
        icon = view.findViewById(R.id.imageView_icon);
        textView_budget = view.findViewById(R.id.textView_budget);
        textView_name = view.findViewById(R.id.textView_userName);

        menu_user = view.findViewById(R.id.menu_user);
        menu_classicication = view.findViewById(R.id.menu_classification);
        menu_budget = view.findViewById(R.id.menu_editbugdet);

        menu_settings = view.findViewById(R.id.menu_setting);
        menu_user.setOnClickListener(this);
        menu_classicication.setOnClickListener(this);
        menu_budget.setOnClickListener(this);
        menu_settings.setOnClickListener(this);

        User user = new User();
        user.setName("");
        user.setBudget(0);
        user.setColor(Color.parseColor("#00000000"));
        setUser(user);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.menu_user:
                intent = new Intent(getContext(), ManageActivity.class);
                intent.putExtra("type", "user");
                startActivityForResult(intent, 1);
                break;
            case R.id.menu_classification:
                intent = new Intent(getContext(), ManageActivity.class);
                intent.putExtra("type", "classification");
                startActivityForResult(intent, 2);
                break;
            case R.id.menu_editbugdet:
//                Toast.makeText(getContext(), "budget", Toast.LENGTH_SHORT).show();
                showDialog();
                break;
            case R.id.menu_setting:
                intent = new Intent(getContext(), SettingsActivity.class);
                startActivity(intent);
//                Toast.makeText(getContext(), "setting", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:
                setUser(DataCenter.getNowUser());
                break;
        }
    }

    private void setUser(User user) {
        textView_name.setText(user.getName());
        textView_budget.setText("预算：" + user.getBudget());
        slide_color.setBackgroundColor(user.getColor());
        icon.setImageTintList(ColorStateList.valueOf(user.getColor()));

    }

    @Override
    protected void onFragmentVisibleChange(boolean isVisible) {
        super.onFragmentVisibleChange(isVisible);
        if (isVisible) {
            setUser(DataCenter.getNowUser());
        }
    }

    private void showDialog() {
        final EditText editText = new EditText(getContext());
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        builder = new AlertDialog.Builder(getContext()).setTitle("输入新的预算").setView(editText)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (editText.getText().toString().trim().length() > 0)
                            if (Integer.parseInt(editText.getText().toString()) > 0) {
                                User user = DataCenter.getNowUser();
                                user.setBudget(Integer.parseInt(editText.getText().toString()));
                                DataCenter.saveUser(user);
                                setUser(DataCenter.getNowUser());


                            }
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setCancelable(true);
        builder.create().show();
    }
}
