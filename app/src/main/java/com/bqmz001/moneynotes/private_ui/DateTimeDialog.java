package com.bqmz001.moneynotes.private_ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.bqmz001.moneynotes.R;
import com.bqmz001.moneynotes.data.DataCenter;
import com.bqmz001.moneynotes.util.DateTimeUtil;

import org.joda.time.DateTime;

import java.util.Calendar;

public class DateTimeDialog extends Dialog {
    Context context;
    DatePicker datePicker;
    TimePicker timePicker;
    Button ok, cancel;
    int thisYear, thisMonth, thisDay, thisHour, thisMinute;

    OnClickListener onClickListener;


    public DateTimeDialog(@NonNull Context context) {
        super(context);
        this.context = context;
    }

    public interface OnClickListener {
        void ok(long time, View view);

        void cancel(View view);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setGravity(Gravity.CENTER);//设置dialog显示居中
        setContentView(R.layout.dialog_datetime);

        WindowManager windowManager = ((Activity) context).getWindowManager();
        final Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.width = display.getWidth() * 9 / 10;// 设置dialog宽度为屏幕的4/5
        getWindow().setAttributes(lp);
        setCanceledOnTouchOutside(false);//点击外部Dialog消失

        datePicker = findViewById(R.id.datepicker);
        timePicker = findViewById(R.id.timepicker);
        ok = findViewById(R.id.button_ok);
        cancel = findViewById(R.id.button_cancel);
        datePicker.setDescendantFocusability(TimePicker.FOCUS_BLOCK_DESCENDANTS);
        timePicker.setDescendantFocusability(TimePicker.FOCUS_BLOCK_DESCENDANTS);

        datePicker.init(DateTimeUtil.getNowYear(), DateTimeUtil.getNowMonth() - 1, DateTimeUtil.getNowDay(), new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                thisYear = year;
                thisMonth = monthOfYear;
                thisDay = dayOfMonth;
            }
        });

        timePicker.setIs24HourView(true);
        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                thisHour = hourOfDay;
                thisMinute = minute;
            }
        });

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (thisYear == 0 || thisMonth == 0 || thisDay == 0 || thisHour == 0 || thisMinute == 0) {
                    Toast.makeText(context, "你没动日期和/或日期，默认给你当前时间", Toast.LENGTH_SHORT).show();
                    onClickListener.ok(DateTimeUtil.getNow(), v);
                } else {
                    try {
                        long t = DateTimeUtil.textToTimestamp(DateTimeUtil.textToDateTime(thisYear + "-" + (thisMonth + 1) + "-" + thisDay + " " + thisHour + ":" + thisMinute + ":00"));
                        onClickListener.ok(t, v);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                dismiss();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickListener.cancel(v);
                dismiss();
            }
        });

    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }
}
