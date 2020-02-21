package com.bqmz001.moneynotes.private_ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;

import com.bqmz001.moneynotes.R;
import com.bqmz001.moneynotes.util.DateTimeUtil;
import com.bqmz001.moneynotes.util.ToastUtil;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;

public class TimeSelectDialog extends Dialog {
    Context context;
    TabLayout tabLayout;
    DatePicker datePicker_day, datePicker_custom_start, datePicker_custom_stop;
    LinearLayout linearLayout_week, linearLayout_month, linearLayout_custom;
    EditText editText_year_forWeek, editText_week_forWeek, editText_year_forMonth, editText_month_forMonth;
    Button ok, cancel;
    int theYear, theMonth, theDay;
    int startYear, startMonth, startDay, endYear, endMonth, endDay;
    int yearForWeek, weekForWeek;
    int yearForMonth, monthForMonth;
    int type=1;

    long startTime, endTime;

    public TimeSelectDialog(@NonNull Context context) {
        super(context);
        this.context = context;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowManager windowManager = ((Activity) context).getWindowManager();
        final Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.width = display.getWidth() * 6 / 7;// 设置dialog宽度为屏幕的4/5
        getWindow().setAttributes(lp);
        setCanceledOnTouchOutside(false);//点击外部Dialog消失
        setContentView(R.layout.dialog_timeselect);

        tabLayout = findViewById(R.id.tabLayout);
        tabLayout.addTab(tabLayout.newTab().setText("单日"));
        tabLayout.addTab(tabLayout.newTab().setText("周"), true);
        tabLayout.addTab(tabLayout.newTab().setText("月"));
        tabLayout.addTab(tabLayout.newTab().setText("自定义"));

        datePicker_day = findViewById(R.id.datepicker_day);
        datePicker_custom_start = findViewById(R.id.datepicker_custom_start);
        datePicker_custom_stop = findViewById(R.id.datepicker_custom_stop);

        linearLayout_week = findViewById(R.id.linearLayout_week);
        linearLayout_month = findViewById(R.id.linearLayout_month);
        linearLayout_custom = findViewById(R.id.linearLayout_custom);

        editText_year_forWeek = findViewById(R.id.editText_year_forWeek);
        editText_week_forWeek = findViewById(R.id.editText_week_forWeek);
        editText_year_forMonth = findViewById(R.id.editText_year_forMonth);
        editText_month_forMonth = findViewById(R.id.editText_month_forMonth);

        datePicker_day.setDescendantFocusability(TimePicker.FOCUS_BLOCK_DESCENDANTS);
        datePicker_day.init(DateTimeUtil.getNowYear(), DateTimeUtil.getNowMonth() - 1, DateTimeUtil.getNowDay(), new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                theYear = year;
                theMonth = monthOfYear + 1;
                theDay = dayOfMonth;
            }
        });

        datePicker_custom_start.setDescendantFocusability(TimePicker.FOCUS_BLOCK_DESCENDANTS);
        datePicker_custom_start.init(DateTimeUtil.getNowYear(), DateTimeUtil.getNowMonth() - 1, DateTimeUtil.getNowDay(), new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                startYear = year;
                startMonth = monthOfYear + 1;
                startDay = dayOfMonth;
            }
        });
        datePicker_custom_stop.setDescendantFocusability(TimePicker.FOCUS_BLOCK_DESCENDANTS);
        datePicker_custom_stop.init(DateTimeUtil.getNowYear(), DateTimeUtil.getNowMonth() - 1, DateTimeUtil.getNowDay(), new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                endYear = year;
                endMonth = monthOfYear + 1;
                endDay = dayOfMonth;
            }
        });

        ok = findViewById(R.id.button_ok);
        cancel = findViewById(R.id.button_cancel);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                showTab(tab.getPosition() + 1);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        tabLayout.getTabAt(0).select();

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (check(type)) {
                        ToastUtil.showContent(startTime+" "+endTime);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });


    }

    void showTab(int type) {
        this.type = type;
        datePicker_day.setVisibility(View.GONE);
        linearLayout_custom.setVisibility(View.GONE);
        linearLayout_month.setVisibility(View.GONE);
        linearLayout_week.setVisibility(View.GONE);
        switch (type) {
            case 0:
                break;
            case 1:
                datePicker_day.setVisibility(View.VISIBLE);
                break;
            case 2:
                linearLayout_week.setVisibility(View.VISIBLE);
                break;
            case 3:
                linearLayout_month.setVisibility(View.VISIBLE);
                break;
            case 4:
                linearLayout_custom.setVisibility(View.VISIBLE);
        }
    }

    boolean check(int type) throws Exception{

        switch (type) {
            case 0:
                return false;
            case 1:
                if (theYear < 0 || theMonth < 0 && theDay < 0) {
                    return false;
                } else {
                    startTime = DateTimeUtil.textToTimestamp(theYear + "-" + theMonth + "-" + theDay + " 00:00:00");
                    endTime = DateTimeUtil.textToTimestamp(theYear + "-" + theMonth + "-" + theDay + " 23:59:59");
                }
                break;
            case 2:
                if (editText_year_forWeek.getText().toString().trim().length() <= 0 || editText_week_forWeek.getText().toString().trim().length() <= 0) {
                    return false;
                } else {
                    int year0 = Integer.parseInt(editText_year_forWeek.getText().toString());
                    int week = Integer.parseInt(editText_week_forWeek.getText().toString());
                    if ((year0 > 2100 || year0 < 1900) && (week > 0 || week < 53)) {
                        return false;
                    } else {
                        startTime = DateTimeUtil.getFirstTimeOfWeek(year0, week);
                        endTime = DateTimeUtil.getLastTimeOfWeek(year0, week);
                    }
                }
                break;
            case 3:
                if (editText_year_forMonth.getText().toString().trim().length() <= 0 || editText_month_forMonth.getText().toString().trim().length() <= 0) {
                    return false;
                } else {
                    int year = Integer.parseInt(editText_year_forMonth.getText().toString());
                    int month = Integer.parseInt(editText_month_forMonth.getText().toString());
                    if ((year > 2100 || year < 1900) && (month > 0 || month < 13)) {
                        return false;
                    } else {
                        startTime = DateTimeUtil.textToTimestamp(year + "-" + month + "-01 00:00:00");
                        endTime = DateTimeUtil.textToTimestamp(year + "-" + month + "-" + DateTimeUtil.getLastDayOfMonth(year, month) + " 23:59:59");
                    }
                }
                break;
            case 4:
                startTime = DateTimeUtil.textToTimestamp(startYear + "-" + startMonth + "-" + startDay + " 00:00:00");
                endTime = DateTimeUtil.textToTimestamp(endYear + "-" + endMonth + "-" + endDay + " 23:59:59");
                if (endTime < startTime) {
                    return false;
                }
                break;
        }


        return true;
    }
}
